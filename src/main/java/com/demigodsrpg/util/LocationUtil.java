package com.demigodsrpg.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class LocationUtil {
    public static String stringFromLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() +
                ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location locationFromString(String location) {
        String[] part = location.split(";");
        if (Bukkit.getWorld(part[0]) != null) {
            return new Location(Bukkit.getWorld(part[0]), Double.parseDouble(part[1]), Double.parseDouble(part[2]),
                    Double.parseDouble(part[3]), Float.parseFloat(part[4]), Float.parseFloat(part[4]));
        }
        return null;
    }

    public static String calculateDistance(Location loc1, Location loc2) {
        double x = loc1.getX() - loc2.getX();
        double y = loc1.getY() - loc2.getY();
        double z = loc1.getZ() - loc2.getZ();

        DecimalFormat f = new DecimalFormat("##.##");
        return "~" + f.format(x) + " ~" + f.format(y) + " ~" + f.format(z);
    }
}
