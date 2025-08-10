package me.hqm.privatereserve.lockedblock;

import me.hqm.privatereserve._PrivateReserve;
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
        if (_PrivateReserve.LOCKED_DATA.isLockable(event.getBlockPlaced())) {
            Location location = event.getBlockPlaced().getLocation();
            Bukkit.getScheduler().scheduleSyncDelayedTask(_PrivateReserve.PLUGIN, () -> {
                if (_PrivateReserve.LOCKED_DATA.create(location.getBlock(), event.getPlayer())) {
                    event.getPlayer().sendMessage(Component.text("Block secured.", NamedTextColor.RED));
                    event.getPlayer().sendMessage(Component.text("Right-click while sneaking to lock/unlock.", NamedTextColor.YELLOW));
                }
            }, 5);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent event) {
        String playerId = event.getPlayer().getUniqueId().toString();
        Optional<LockedBlockDocument> oModel = _PrivateReserve.LOCKED_DATA.fromLocation(event.getBlock().getLocation());
        if (oModel.isPresent()) {
            if (!_PrivateReserve.LOCKED_DATA.isLockable(event.getBlock()) || oModel.get().getOwner().equals(playerId)) {
                _PrivateReserve.LOCKED_DATA.delete(event.getBlock());
                event.getPlayer().sendMessage(Component.text("Secured block was destroyed.", NamedTextColor.RED));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (_PrivateReserve.LOCKED_DATA.isRegistered(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();
        Block block = event.getClickedBlock();
        if (canLock(player) && player.isSneaking() && EquipmentSlot.HAND.equals(event.getHand()) &&
                _PrivateReserve.LOCKED_DATA.isRegistered(block)) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            LockedBlockDatabase.LockState state = _PrivateReserve.LOCKED_DATA.lockUnlock(block, event.getPlayer());
            if (state == LockedBlockDatabase.LockState.LOCKED) {
                player.sendMessage(Component.text("This block is locked.", NamedTextColor.RED));
            } else if (state == LockedBlockDatabase.LockState.UNLOCKED) {
                player.sendMessage(Component.text("This block is unlocked.", NamedTextColor.YELLOW));
            } else if (state == LockedBlockDatabase.LockState.UNCHANGED) {
                player.sendMessage(Component.text("You don't have the key to this block.", NamedTextColor.YELLOW));
            }
        } else if (_PrivateReserve.LOCKED_DATA.getLockState(event.getClickedBlock()) ==
                LockedBlockDatabase.LockState.LOCKED) {
            // Deny interaction
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);

            // Cancel break animation
            _PrivateReserve.RELATIONAL_DATA.put(playerId, "NO-BREAK", true);
            player.addPotionEffect(PotionEffectType.MINING_FATIGUE.createEffect(9999999, 5));
        } else if (block == null || _PrivateReserve.RELATIONAL_DATA.contains(playerId, "NO-BREAK")) {
            // Allow break animation
            _PrivateReserve.RELATIONAL_DATA.remove(playerId, "NO-BREAK");
            player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (_PrivateReserve.LOCKED_DATA.getLockState(event.getBlock()) == LockedBlockDatabase.LockState.LOCKED) {
            event.setNewCurrent(event.getOldCurrent()); // cancelled
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Block) {
            if (_PrivateReserve.LOCKED_DATA.getLockState((Block) event.getSource().getHolder()) ==
                    LockedBlockDatabase.LockState.LOCKED) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        _PrivateReserve.LOCKED_DATA.getRawData().values().stream().filter(model -> model.getLocation().getWorld().
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
        return !_PrivateReserve.RELATIONAL_DATA.contains(player.getUniqueId().toString(), "NO-LOCK");
    }
}
