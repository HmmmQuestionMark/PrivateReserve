package me.hqm.privatereserve.lockedblock;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.landmark.LandmarkCommand;
import me.hqm.privatereserve.lockedblock.data.JsonFileLockedBlockDB;
import me.hqm.privatereserve.lockedblock.data.LockedBlockDatabase;
import me.hqm.privatereserve.lockedblock.data.MsgPackFileLockedBlockDB;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Set;

public class LockedBlocks {
    private static LockedBlockDatabase LOCKED_DATA;

    // -- DATA -- //

    private LockedBlocks() {
    }

    public static LockedBlockDatabase data() {
        return LOCKED_DATA;
    }

    // -- COMMANDS -- //

    public static LiteralCommandNode<CommandSourceStack> command() {
        return LandmarkCommand.createCommand();
    }

    public static Collection<String> commandAlias() {
        return Set.of("lock");
    }

    // -- INIT -- //

    public static void init(JavaPlugin plugin, SupportedFormat format) {
        // Files
        switch (format) {
            case SupportedFormat.MESSAGEPACK: {
                LOCKED_DATA = new MsgPackFileLockedBlockDB();
                PrivateReserve.logger().info("MessagePack enabled for locked block data.");
                break;
            }
            case JSON:
            default: {
                LOCKED_DATA = new JsonFileLockedBlockDB();
                PrivateReserve.logger().info("Json enabled for locked block data.");
            }
        }
        LOCKED_DATA.loadAll();

        // Listeners
        plugin.getServer().getPluginManager().registerEvents(new LockedBlockListener(), plugin);

        // Commands
        PrivateReserve.registerCommand(LockModeCommand.createCommand(), "lock");

        // Log
        PrivateReserve.logger().info("Locked blocks enabled.");
    }
}
