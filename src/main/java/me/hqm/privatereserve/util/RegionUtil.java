package me.hqm.privatereserve.util;

import com.demigodsrpg.util.WorldGuardUtil;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.hqm.privatereserve.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@SuppressWarnings("ConstantConditions")
public class RegionUtil {
    private RegionUtil() {
    }

    public static boolean spawnContains(Location location) {
        return WorldGuardUtil.checkForRegion(Setting.SPAWN_REGION.getString(), location);
    }

    public static boolean visitingContains(Location location) {
        return WorldGuardUtil.checkForRegion(Setting.VISITOR_REGION.getString(), location);
    }

    public static Location spawnLocation() throws NullPointerException {
        ProtectedRegion spawn =
                WorldGuardUtil.getRegion(Setting.SPAWN_REGION.getString(), Bukkit.getWorld(Setting.SPAWN_REGION_WORLD.getString()));
        return getLocation(spawn);
    }

    public static Location visitingLocation() throws NullPointerException {
        ProtectedRegion visiting =
                WorldGuardUtil.getRegion(Setting.VISITOR_REGION.getString(), Bukkit.getWorld(Setting.VISITOR_REGION_WORLD.getString()));
        return getLocation(visiting);
    }

    private static Location getLocation(ProtectedRegion region) {
        com.sk89q.worldedit.util.Location location = region.getFlag(Flags.SPAWN_LOC);
        float sY = location.getYaw();
        float sP = location.getPitch();
        return new Location(Bukkit.getWorlds().getFirst(), location.getX(), location.getY(), location.getZ(), sY, sP);
    }
}
