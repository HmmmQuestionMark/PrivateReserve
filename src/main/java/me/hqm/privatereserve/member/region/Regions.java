package me.hqm.privatereserve.member.region;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@SuppressWarnings("ConstantConditions")
public class Regions {

    static Location DEFAULT_SPAWN;
    static boolean WG_ENABLED;

    private Regions() {
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(Location defaultSpawn) {
        DEFAULT_SPAWN = defaultSpawn;
        WG_ENABLED = Settings.MEMBER_REGION_ENABLED.getBoolean() && Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard");
        if (WG_ENABLED) {
            try {
                com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst();
                PrivateReserve.logger().info("WorldGuard found.");
            } catch (NoClassDefFoundError ignored) {
                WG_ENABLED = false;
            }
        }
    }

    public static boolean spawnContains(Location location) {
        if (WG_ENABLED) {
            try {
                return WGHandler.checkForRegion(Settings.MEMBER_SPAWN_REGION.getString(), location);
            } catch (NoClassDefFoundError ignored) {
                WG_ENABLED = false;
            }
        }
        return true;
    }

    public static boolean visitingContains(Location location) {
        if (WG_ENABLED) {
            try {
                return WGHandler.checkForRegion(Settings.VISITOR_SPAWN_REGION.getString(), location);
            } catch (NoClassDefFoundError ignored) {
                WG_ENABLED = false;
            }
        }
        return true;
    }

    public static Location spawnLocation() throws NullPointerException {
        if (WG_ENABLED) {
            try {
                return WGHandler.spawnLocation(Settings.MEMBER_SPAWN_REGION.getString(),
                        Bukkit.getWorld(Settings.MEMBER_SPAWN_REGION_WORLD.getString()),
                        DEFAULT_SPAWN);
            } catch (NoClassDefFoundError ignored) {
                WG_ENABLED = false;
            }
        }
        return DEFAULT_SPAWN;
    }

    public static Location visitingLocation() throws NullPointerException {
        if (WG_ENABLED) {
            try {
                return WGHandler.spawnLocation(Settings.VISITOR_SPAWN_REGION.getString(),
                        Bukkit.getWorld(Settings.VISITOR_SPAWN_REGION_WORLD.getString()),
                        DEFAULT_SPAWN);
            } catch (NoClassDefFoundError ignored) {
                WG_ENABLED = false;
            }
        }
        return DEFAULT_SPAWN;
    }
}
