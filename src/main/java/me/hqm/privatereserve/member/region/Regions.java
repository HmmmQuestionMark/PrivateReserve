package me.hqm.privatereserve.member.region;

import me.hqm.privatereserve.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@SuppressWarnings("ConstantConditions")
public class Regions {

    static Location DEFAULT_SPAWN;
    static boolean WG_ENABLED;

    private Regions() {
    }

    public static void init(Location defaultSpawn) {
        DEFAULT_SPAWN = defaultSpawn;
        WG_ENABLED = Settings.MEMBER_REGION_ENABLED.getBoolean();
        if (WG_ENABLED) {
            WG_ENABLED = Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard");
            if (WG_ENABLED) {
                try {
                    com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst();
                } catch (Exception ignored) {
                    WG_ENABLED = false;
                }
            }
        }
    }

    public static boolean spawnContains(Location location) {
        return WG_ENABLED && WGHandler.checkForRegion(Settings.MEMBER_SPAWN_REGION.getString(), location);
    }

    public static boolean visitingContains(Location location) {
        return WG_ENABLED && WGHandler.checkForRegion(Settings.VISITOR_SPAWN_REGION.getString(), location);
    }

    public static Location spawnLocation() throws NullPointerException {
        if (WG_ENABLED) {
            com.sk89q.worldguard.protection.regions.ProtectedRegion spawn =
                    WGHandler.getRegion(Settings.MEMBER_SPAWN_REGION.getString(), Bukkit.getWorld(Settings.MEMBER_SPAWN_REGION_WORLD.getString()));
            return spawnLocation(spawn);
        }
        return DEFAULT_SPAWN;
    }

    public static Location visitingLocation() throws NullPointerException {
        if (WG_ENABLED) {
            com.sk89q.worldguard.protection.regions.ProtectedRegion visiting =
                    WGHandler.getRegion(Settings.VISITOR_SPAWN_REGION.getString(), Bukkit.getWorld(Settings.VISITOR_SPAWN_REGION_WORLD.getString()));
            return spawnLocation(visiting);
        }
        return DEFAULT_SPAWN;
    }

    private static Location spawnLocation(com.sk89q.worldguard.protection.regions.ProtectedRegion region) {
        if (WG_ENABLED) {
            com.sk89q.worldedit.util.Location location = region.getFlag(com.sk89q.worldguard.protection.flags.Flags.SPAWN_LOC);
            float sY = location.getYaw();
            float sP = location.getPitch();
            return new Location(Bukkit.getWorlds().getFirst(), location.getX(), location.getY(), location.getZ(), sY, sP);
        }
        return DEFAULT_SPAWN;
    }
}
