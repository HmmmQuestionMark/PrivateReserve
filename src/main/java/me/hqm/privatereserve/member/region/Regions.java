package me.hqm.privatereserve.member.region;

import com.sk89q.worldguard.protection.flags.Flags;
import me.hqm.privatereserve.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@SuppressWarnings("ConstantConditions")
public class Regions {
    private Regions() {
    }

    public static boolean spawnContains(Location location) {
        return WGHandler.checkForRegion(Settings.MEMBER_SPAWN_REGION.getString(), location);
    }

    public static boolean visitingContains(Location location) {
        return WGHandler.checkForRegion(Settings.VISITOR_SPAWN_REGION.getString(), location);
    }

    public static Location spawnLocation() throws NullPointerException {
        com.sk89q.worldguard.protection.regions.ProtectedRegion spawn =
                WGHandler.getRegion(Settings.MEMBER_SPAWN_REGION.getString(), Bukkit.getWorld(Settings.MEMBER_SPAWN_REGION_WORLD.getString()));
        return spawnLocation(spawn);
    }

    public static Location visitingLocation() throws NullPointerException {
        com.sk89q.worldguard.protection.regions.ProtectedRegion visiting =
                WGHandler.getRegion(Settings.VISITOR_SPAWN_REGION.getString(), Bukkit.getWorld(Settings.VISITOR_SPAWN_REGION_WORLD.getString()));
        return spawnLocation(visiting);
    }

    private static Location spawnLocation(com.sk89q.worldguard.protection.regions.ProtectedRegion region) {
        com.sk89q.worldedit.util.Location location = region.getFlag(Flags.SPAWN_LOC);
        float sY = location.getYaw();
        float sP = location.getPitch();
        return new Location(Bukkit.getWorlds().getFirst(), location.getX(), location.getY(), location.getZ(), sY, sP);
    }
}
