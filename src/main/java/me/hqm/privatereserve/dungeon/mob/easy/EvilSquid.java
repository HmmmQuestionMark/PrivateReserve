package me.hqm.privatereserve.dungeon.mob.easy;

import me.hqm.privatereserve.dungeon.mob.DungeonMob;
import me.hqm.privatereserve.dungeon.mob.DungeonMobs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class EvilSquid implements DungeonMob {
    @Override
    public String getName() {
        return "Evil Squid";
    }

    @Override
    public EntityType getType() {
        return EntityType.SQUID;
    }

    @Override
    public double getMaxHealth() {
        return 20;
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public int getStrength() {
        return 1;
    }

    @Override
    public double dropLuck() {
        return 0;
    }

    @Override
    public int dropStack() {
        return 0;
    }

    @Override
    public void registerRunnables(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new SquidRunnable(), 20, 20);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSprint(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSprinting() && player.getPassenger() instanceof Squid) {
            Optional<DungeonMob> type = DungeonMobs.getType(player.getPassenger());
            if (type.isPresent() && type.get().equals(this)) {
                player.eject();
            }
        }
    }

    public static class SquidRunnable implements Runnable {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // Safe if wearing helmet
                if (player.getInventory().getHelmet() == null || Material.AIR.equals(player.getInventory().getHelmet().
                        getType())) {
                    List<Entity> nearbyEntities = player.getNearbyEntities(4, 4, 4);
                    nearbyEntities.stream().filter(entity -> entity instanceof Squid).filter(squid -> DungeonMobs.
                            isTracked((Squid) squid)).map(squid -> (Squid) squid).forEach(squid -> {
                        if (player.isEmpty()) {
                            player.setPassenger(squid);
                            player.sendMessage(ChatColor.RED + "EVIL SQUID WATCH OUT!"); //TODO DEBUG
                        }
                        if (squid.equals(player.getPassenger())) {
                            EntityDamageByEntityEvent squidDamage = new EntityDamageByEntityEvent(squid, player,
                                    EntityDamageEvent.DamageCause.ENTITY_ATTACK, 2);
                            Bukkit.getPluginManager().callEvent(squidDamage);
                            if (!squidDamage.isCancelled()) {
                                player.damage(2, squid);
                                player.setLastDamageCause(squidDamage);
                            }
                        }
                    });
                }
            }
        }
    }
}
