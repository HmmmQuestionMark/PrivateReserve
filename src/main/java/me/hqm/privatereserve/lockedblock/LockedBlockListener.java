package me.hqm.privatereserve.lockedblock;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.lockedblock.data.LockedBlock;
import me.hqm.privatereserve.lockedblock.data.LockedBlockDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class LockedBlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (LockedBlocks.data().isLockable(event.getBlockPlaced())) {
            Location location = event.getBlockPlaced().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateReserve.plugin(), () -> {
                if (LockedBlocks.data().create(location.getBlock(), event.getPlayer())) {
                    event.getPlayer().sendMessage(Component.text("Block secured.", NamedTextColor.RED));
                    event.getPlayer().sendMessage(Component.text("Right-click while sneaking to lock/unlock.", NamedTextColor.YELLOW));
                }
            }, 5);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        String playerId = event.getPlayer().getUniqueId().toString();
        Optional<LockedBlock> oModel = LockedBlocks.data().fromLocation(event.getBlock().getLocation());
        if (oModel.isPresent()) {
            if (!LockedBlocks.data().isLockable(event.getBlock()) || oModel.get().getOwner().equals(playerId)) {
                LockedBlocks.data().delete(event.getBlock());
                event.getPlayer().sendMessage(Component.text("Secured block was destroyed.", NamedTextColor.RED));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (LockedBlocks.data().isRegistered(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (canLock(player) && player.isSneaking() && EquipmentSlot.HAND.equals(event.getHand()) &&
                LockedBlocks.data().isRegistered(block)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            LockedBlockDatabase.LockState state = LockedBlocks.data().lockUnlock(block, event.getPlayer());
            if (state == LockedBlockDatabase.LockState.LOCKED) {
                player.sendMessage(Component.text("This block is locked.", NamedTextColor.RED));
            } else if (state == LockedBlockDatabase.LockState.UNLOCKED) {
                player.sendMessage(Component.text("This block is unlocked.", NamedTextColor.YELLOW));
            } else if (state == LockedBlockDatabase.LockState.UNCHANGED) {
                player.sendMessage(Component.text("You don't have the key to this block.", NamedTextColor.YELLOW));
            }
        } else if (LockedBlocks.data().getLockState(event.getClickedBlock()) ==
                LockedBlockDatabase.LockState.LOCKED) {
            // Deny interaction
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            // Cancel break animation
            player.getPersistentDataContainer().set(
                    new NamespacedKey(PrivateReserve.plugin().namespace(), "no_break"),
                    PersistentDataType.BOOLEAN,
                    true
            );
            player.addPotionEffect(PotionEffectType.MINING_FATIGUE.createEffect(9999999, 5));
        } else if (block == null || player.getPersistentDataContainer().has(
                new NamespacedKey(PrivateReserve.plugin().namespace(), "no_break"
                ))) {
            // Allow break animation
            player.getPersistentDataContainer().remove(
                    new NamespacedKey(PrivateReserve.plugin().namespace(), "no_break"
                    ));
            player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (LockedBlocks.data().getLockState(event.getBlock()) == LockedBlockDatabase.LockState.LOCKED) {
            event.setNewCurrent(event.getOldCurrent()); // cancelled
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Block) {
            if (LockedBlocks.data().getLockState((Block) event.getSource().getHolder()) ==
                    LockedBlockDatabase.LockState.LOCKED) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        LockedBlocks.data().getRawData().values().stream().filter(model -> model.getLocation().getWorld().
                        equals(event.getLocation().getWorld()) && model.getLocation().distance(event.getLocation()) <= 10).
                map(save -> save.getLocation().getBlock()).forEach(block -> {
                    if (LockedBlockDatabase.isBisected(block)) {
                        LockedBlockDatabase.getBisected(block).forEach(chest -> event.blockList().remove(chest));
                    } else if (LockedBlockDatabase.isDoubleChest(block)) {
                        LockedBlockDatabase.getDoubleChest(block).forEach(chest -> event.blockList().remove(chest));
                    } else {
                        event.blockList().remove(block);
                    }
                });
    }

    boolean canLock(Player player) {
        NamespacedKey nsk = new NamespacedKey(PrivateReserve.plugin().namespace(), "no_lock");
        return !player.getPersistentDataContainer().getOrDefault(nsk, PersistentDataType.BOOLEAN, false);
    }
}
