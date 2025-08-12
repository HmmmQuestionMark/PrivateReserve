package me.hqm.privatereserve.member;

import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.member.command.*;
import me.hqm.privatereserve.member.data.JsonFileMemberDB;
import me.hqm.privatereserve.member.data.MemberDatabase;
import me.hqm.privatereserve.member.data.MsgPackFileMemberDB;
import org.bukkit.plugin.java.JavaPlugin;

public class Members {
    private static MemberDatabase MEMBER_DATA;

    // -- DATA -- //

    private Members() {
    }

    public static MemberDatabase data() {
        return MEMBER_DATA;
    }

    // -- INIT -- //

    public static void init(JavaPlugin plugin, SupportedFormat format) {
        // Files
        switch (format) {
            case SupportedFormat.MESSAGEPACK: {
                MEMBER_DATA = new MsgPackFileMemberDB();
                PrivateReserve.logger().info("MessagePack enabled for member data.");
                break;
            }
            case JSON:
            default: {
                MEMBER_DATA = new JsonFileMemberDB();
                PrivateReserve.logger().info("Json enabled for member data.");
            }
        }
        MEMBER_DATA.loadAll();

        // Listeners
        plugin.getServer().getPluginManager().registerEvents(new MemberListener(), plugin);

        // Commands
        if (Settings.MEMBER_HOME_ENABLED.getBoolean()) {
            PrivateReserve.registerCommand(HomeCommand.createCommand());
        }
        PrivateReserve.registerCommand(NickNameCommand.createCommand(), "nick");
        PrivateReserve.registerCommand(PronounsCommand.createCommand());
        PrivateReserve.registerCommand(InviteCommand.createCommand());
        PrivateReserve.registerCommand(ExpelCommand.createCommand());
        PrivateReserve.registerCommand(TrustCommand.createCommand());
        PrivateReserve.registerCommand(AlternateCommand.createCommand(), "alt");
        PrivateReserve.registerCommand(SpawnCommand.createCommand());
        PrivateReserve.registerCommand(VisitingCommand.createCommand(), "vspawn");
        PrivateReserve.registerCommand(MemberHelpCommand.createCommand());

        // Log
        PrivateReserve.logger().info("Members enabled.");
    }
}
