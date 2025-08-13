package me.hqm.privatereserve;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Optional;

public class Locations {

    public static void teleportWithGhast(Player player, Location to) {
        if(player.isInsideVehicle() && player.getVehicle() instanceof HappyGhast ghast) {
            ghast.teleport(to.add(0.0, 4.0, 0.0),
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    TeleportFlag.EntityState.RETAIN_PASSENGERS
            );
        } else {
            player.teleport(
                    to,
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    TeleportFlag.EntityState.RETAIN_VEHICLE
            );
        }
    }

    public static void teleportAsyncWithGhast(Player player, Location to) {
        if(player.isInsideVehicle() && player.getVehicle() instanceof HappyGhast ghast) {
            ghast.teleportAsync(to.add(0.0, 4.0, 0.0),
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    TeleportFlag.EntityState.RETAIN_PASSENGERS
            );
        } else {
            player.teleportAsync(
                    to,
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    TeleportFlag.EntityState.RETAIN_VEHICLE
            );
        }
    }

    public static String stringFromLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() +
                ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location locationFromString(String location) {
        String[] part = location.split(";");
        if (Bukkit.getWorld(part[0]) != null) {
            return new Location(Bukkit.getWorld(part[0]), Double.parseDouble(part[1]), Double.parseDouble(part[2]),
                    Double.parseDouble(part[3]), Float.parseFloat(part[4]), Float.parseFloat(part[5]));
        }
        return null;
    }

    public static String prettyLocation(Location loc) {
        return "~" + loc.getBlockX() + " ~" + loc.getBlockY() + " ~" + loc.getBlockZ();
    }

    public static String calculateDistance(Location loc1, Location loc2) {
        double x = loc1.getX() - loc2.getX();
        double y = loc1.getY() - loc2.getY();
        double z = loc1.getZ() - loc2.getZ();

        DecimalFormat f = new DecimalFormat("##.##");
        return "~" + f.format(x) + " ~" + f.format(y) + " ~" + f.format(z);
    }

    public static Location atCloudLevel(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), 195D, location.getZ());
    }

    public static Optional<Block> nearestOfType(Location location, int radius, Material... types) {
        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();
        World world = location.getWorld();
        int worldMin = world.getMinHeight();
        int worldMax = world.getMaxHeight();

        Block nearest = null;
        double distance = Double.MAX_VALUE;

        for (int x = locX - radius; x <= locX + radius; x++) {
            for (int y = Math.max(locY - radius, worldMin); y <= Math.min(locY + radius, worldMax); y++) {
                for (int z = locZ - radius; z <= locZ + radius; z++) {
                    Location looking = new Location(world, x, y, z);
                    if (looking.distance(location) < distance) {
                        Block block = looking.getBlock();
                        if (Arrays.asList(types).contains(block.getType())) {
                            nearest = block;
                        }
                    }
                }
            }
        }

        return Optional.ofNullable(nearest);
    }
}
