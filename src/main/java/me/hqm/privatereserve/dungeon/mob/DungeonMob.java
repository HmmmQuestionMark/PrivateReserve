package me.hqm.privatereserve.dungeon.mob;

import me.hqm.privatereserve.dungeon.drop.DropQuery;
import me.hqm.privatereserve.dungeon.drop.Loot;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

public interface DungeonMob extends Listener {
    String getName();

    EntityType getType();

    double getMaxHealth();

    boolean isBoss();

    int getStrength();

    double dropLuck();

    int dropStack();

    default LivingEntity spawnRaw(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, getType());
        entity.setCustomName(getName());
        entity.setCustomNameVisible(true);
        entity.setMaxHealth(getMaxHealth());
        entity.setHealth(getMaxHealth());
        return entity;
    }

    default void registerRunnables(Plugin plugin) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    default void onDeath(EntityDeathEvent event) {
        if (DungeonMobs.getMobs(this).contains(event.getEntity())) {
            event.getDrops().clear();
            event.getDrops().addAll(Loot.getEquipment().loot(new DropQuery("/equipment", dropLuck(), dropStack())));
        }
    }
}
