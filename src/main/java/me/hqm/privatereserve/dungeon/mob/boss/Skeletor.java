package me.hqm.privatereserve.dungeon.mob.boss;

import com.demigodsrpg.chitchat.Chitchat;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.dungeon.mob.DungeonMob;
import me.hqm.privatereserve.dungeon.mob.DungeonMobs;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Skeletor implements DungeonMob {

    @Override
    public String getName() {
        return "Skeletor";
    }

    @Override
    public EntityType getType() {
        return EntityType.SKELETON;
    }

    @Override
    public double getMaxHealth() {
        return 20;
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public int getStrength() {
        return 1;
    }

    @Override
    public double dropLuck() {
        return 1.0;
    }

    @Override
    public int dropStack() {
        return 2;
    }

    @Override
    public LivingEntity spawnRaw(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, getType());
        entity.setCustomName(getName());
        entity.setCustomNameVisible(true);
        entity.setMaxHealth(getMaxHealth());
        entity.setHealth(getMaxHealth());
        entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999, 2, true, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999, 2, true, false));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999, 2, true, false));
        return entity;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball && event.getEntity().getShooter() instanceof Skeleton &&
                DungeonMobs.getMobs(this).contains(event.getEntity().getShooter())) {
            // Get the hit location
            final Location location = event.getEntity().getLocation();

            // Do some dank effects
            for (int i = 0; i < 6; i++) {
                final int j = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateReserve.PLUGIN, () -> {
                    location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
                    location.getWorld().playSound(location, Sound.ENTITY_PLAYER_BURP, 0.5F + (0.25F * j), 2F);
                }, 5 * j);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity && DungeonMobs.getMobs(this).contains(event.getEntity())) {
            // Get the skeleton
            Skeleton skeleton = (Skeleton) event.getEntity();

            // Get the target
            LivingEntity target = event.getTarget();

            // Special stuff for players
            if (target instanceof Player) {
                Player player = (Player) target;

                // TODO DEBUG
                Chitchat.sendTitle(player, 10, 20, 10, "Skeletor has targeted you.", "Watch out for his dank arrows.");
                skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
            } else {
                skeleton.setSkeletonType(Skeleton.SkeletonType.NORMAL);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArrowLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Skeleton &&
                DungeonMobs.getMobs(this).contains(event.getEntity().getShooter())) {
            // Get the skeleton
            Skeleton skeleton = (Skeleton) event.getEntity().getShooter();

            // Spawn wither skull
            Fireball fireball = skeleton.launchProjectile(Fireball.class);
            fireball.setVelocity(event.getEntity().getVelocity());
            fireball.setIsIncendiary(true);
            fireball.setYield(8F);
            fireball.setShooter(skeleton);

            // Cancel the event
            event.setCancelled(true);
        }
    }
}
