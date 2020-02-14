package me.hqm.privatereserve.util;

import com.google.common.collect.Lists;
import me.hqm.privatereserve.Setting;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.BlockIterator;

import java.util.*;
import java.util.stream.Collectors;

public class TargetingUtil {
    private static final int TARGET_OFFSET = 3;

    @Deprecated
    public static LivingEntity autoTarget(Player player) {
        return autoTarget(player, Setting.MAX_TARGET_RANGE);
    }

    /**
     * Returns the LivingEntity that <code>player</code> is target.
     *
     * @param player the player
     * @return the targeted LivingEntity
     */
    public static LivingEntity autoTarget(Player player, int range) {
        // Define variables
        final int correction = 3;
        Location target = null;
        try {
            target = player.getTargetBlock((Set<Material>) null, range).getLocation();
        } catch (Exception ignored) {
        }
        if (target == null) return null;
        BlockIterator iterator = new BlockIterator(player, range);
        List<Entity> targets = Lists.newArrayList();

        // Iterate through the blocks and find the target
        while (iterator.hasNext()) {
            final Block block = iterator.next();
            targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class).stream().filter(entity -> {
                Location location = entity.getLocation();
                if (location.distance(player.getLocation()) < range) {
                    if (entity.getLocation().distance(block.getLocation()) <= correction) {
                        if (entity instanceof Player) {
                            return false; // No PvP in Seasons
                        }
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList()));
        }

        // Attempt to return the closest entity to the cursor
        for (Entity entity : targets) {
            if (entity.getLocation().distance(target) <= correction) {
                return (LivingEntity) entity;
            }
        }

        // If it failed to do that then just return the first entity
        try {
            return (LivingEntity) targets.get(0);
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Location directTarget(Player player) {
        return player.getTargetBlock((Set<Material>) null, 140).getLocation();
    }

    /**
     * Returns true if the <code>player</code> ability hits <code>target</code>.
     *
     * @param player the player using the ability
     * @param target the targeted LivingEntity
     * @return true/false depending on if the ability hits or misses
     */
    public static boolean target(Player player, Location target, boolean notify) {
        Location toHit = adjustedAimLocation(player, target);
        if (isHit(target, toHit)) return true;
        if (notify) player.sendMessage(ChatColor.RED + "Missed..."); // TODO Better message.
        return false;
    }

    /**
     * Returns the location that <code>character</code> is actually aiming
     * at when target <code>target</code>.
     *
     * @param player the character triggering the ability callAbilityEvent
     * @param target the location the character is target at
     * @return the aimed at location
     */
    public static Location adjustedAimLocation(Player player, Location target) {
        // FIXME: This needs major work.

        int accuracy = 15;

        int offset = (int) (TARGET_OFFSET + player.getLocation().distance(target));
        int adjustedOffset = offset / accuracy;
        if (adjustedOffset < 1) adjustedOffset = 1;
        Random random = new Random();
        World world = target.getWorld();

        int randomInt = random.nextInt(adjustedOffset);
        int sampleSpace = random.nextInt(3);

        double X = target.getX();
        double Z = target.getZ();
        double Y = target.getY();

        if (sampleSpace == 0) {
            X += randomInt;
            Z += randomInt;
        } else if (sampleSpace == 1) {
            X -= randomInt;
            Z -= randomInt;
        } else if (sampleSpace == 2) {
            X -= randomInt;
            Z += randomInt;
        } else if (sampleSpace == 3) {
            X += randomInt;
            Z -= randomInt;
        }

        return new Location(world, X, Y, Z);
    }

    /**
     * Returns true if <code>target</code> is hit at <code>hit</code>.
     *
     * @param target the LivingEntity being targeted
     * @param hit    the location actually hit
     * @return true/false if <code>target</code> is hit
     */
    public static boolean isHit(Location target, Location hit) {
        return hit.distance(target) <= 2;
    }
}
