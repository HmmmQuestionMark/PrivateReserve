package me.hqm.privatereserve.lockedblock;

import me.hqm.privatereserve.PrivateReserve;
import org.bukkit.plugin.java.JavaPlugin;

public class LockedBlocks {
    private LockedBlocks() {
    }

    static LockedBlockDatabase LOCKED_DATA;

    public static void init(JavaPlugin plugin) {
        // Files

        // Listeners

        // Commands

        // Log
        PrivateReserve.logger().info("Locked blocks enabled.");
    }

    public static LockedBlockDatabase data() {
        return LOCKED_DATA;
    }
}
