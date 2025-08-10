package me.hqm.privatereserve.listener;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.LockedBlockModel;
import me.hqm.privatereserve.registry.LockedBlockRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class LockedBlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (PrivateReserve.LOCKED_R.isLockable(event.getBlockPlaced())) {
            Location location = event.getBlockPlaced().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateReserve.PLUGIN, () -> {
                if (PrivateReserve.LOCKED_R.create(location.getBlock(), event.getPlayer())) {
                    event.getPlayer().sendMessage(Component.text("Block secured.", NamedTextColor.RED));
                    event.getPlayer().sendMessage(Component.text("Right-click while sneaking to lock/unlock.", NamedTextColor.YELLOW));
                }
            }, 5);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        String playerId = event.getPlayer().getUniqueId().toString();
        Optional<LockedBlockModel> oModel = PrivateReserve.LOCKED_R.fromLocation(event.getBlock().getLocation());
        if (oModel.isPresent()) {
            if (!PrivateReserve.LOCKED_R.isLockable(event.getBlock()) || oModel.get().getOwner().equals(playerId)) {
                PrivateReserve.LOCKED_R.delete(event.getBlock());
                event.getPlayer().sendMessage(Component.text("Secured block was destroyed.", NamedTextColor.RED));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (PrivateReserve.LOCKED_R.isRegistered(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();
        Block block = event.getClickedBlock();
        if (canLock(player) && player.isSneaking() && EquipmentSlot.HAND.equals(event.getHand()) &&
                PrivateReserve.LOCKED_R.isRegistered(block)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            LockedBlockRegistry.LockState state = PrivateReserve.LOCKED_R.lockUnlock(block, event.getPlayer());
            if (state == LockedBlockRegistry.LockState.LOCKED) {
                player.sendMessage(Component.text("This block is locked.", NamedTextColor.RED));
            } else if (state == LockedBlockRegistry.LockState.UNLOCKED) {
                player.sendMessage(Component.text("This block is unlocked.", NamedTextColor.YELLOW));
            } else if (state == LockedBlockRegistry.LockState.UNCHANGED) {
                player.sendMessage(Component.text("You don't have the key to this block.", NamedTextColor.YELLOW));
            }
        } else if (PrivateReserve.LOCKED_R.getLockState(event.getClickedBlock()) ==
                LockedBlockRegistry.LockState.LOCKED) {
            // Deny interaction
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            // Cancel break animation
            PrivateReserve.RELATIONAL_R.put(playerId, "NO-BREAK", true);
            player.addPotionEffect(PotionEffectType.MINING_FATIGUE.createEffect(9999999, 5));
        } else if (block == null || PrivateReserve.RELATIONAL_R.contains(playerId, "NO-BREAK")) {
            // Allow break animation
            PrivateReserve.RELATIONAL_R.remove(playerId, "NO-BREAK");
            player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (PrivateReserve.LOCKED_R.getLockState(event.getBlock()) == LockedBlockRegistry.LockState.LOCKED) {
            event.setNewCurrent(event.getOldCurrent()); // cancelled
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Block) {
            if (PrivateReserve.LOCKED_R.getLockState((Block) event.getSource().getHolder()) ==
                    LockedBlockRegistry.LockState.LOCKED) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        PrivateReserve.LOCKED_R.getRegisteredData().values().stream().filter(model -> model.getLocation().getWorld().
                        equals(event.getLocation().getWorld()) && model.getLocation().distance(event.getLocation()) <= 10).
                map(save -> save.getLocation().getBlock()).forEach(block -> {
                    if (LockedBlockRegistry.isBisected(block)) {
                        LockedBlockRegistry.getBisected(block).forEach(chest -> event.blockList().remove(chest));
                    } else if (LockedBlockRegistry.isDoubleChest(block)) {
                        LockedBlockRegistry.getDoubleChest(block).forEach(chest -> event.blockList().remove(chest));
                    } else {
                        event.blockList().remove(block);
                    }
                });
    }

    boolean canLock(Player player) {
        return !PrivateReserve.RELATIONAL_R.contains(player.getUniqueId().toString(), "NO-LOCK");
    }
}
