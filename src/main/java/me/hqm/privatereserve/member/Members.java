package me.hqm.privatereserve.member;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.member.command.*;
import me.hqm.privatereserve.member.data.FileMemberDatabase;
import me.hqm.privatereserve.member.data.MemberDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public class Members {
    private Members() {
    }

    static MemberDatabase MEMBER_DATA;

    public static void init(JavaPlugin plugin) {
        // Files
        MEMBER_DATA = new FileMemberDatabase();
        MEMBER_DATA.loadAllFromDb();

        // Listeners
        plugin.getServer().getPluginManager().registerEvents(new MemberListener(), plugin);

        // Commands
        plugin.getCommand("nickname").setExecutor(new NickNameCommand());
        plugin.getCommand("pronouns").setExecutor(new PronounsCommand());
        plugin.getCommand("clearnickname").setExecutor(new ClearNickNameCommand());
        plugin.getCommand("clearpronouns").setExecutor(new ClearPronounsCommand());
        plugin.getCommand("invite").setExecutor(new InviteCommand());
        plugin.getCommand("trust").setExecutor(new TrustCommand());
        plugin.getCommand("expel").setExecutor(new ExpelCommand());
        plugin.getCommand("alternate").setExecutor(new AlternateCommand());
        plugin.getCommand("spawn").setExecutor(new SpawnCommand());
        if (Settings.MEMBER_HOME_ENABLED.getBoolean()) {
            plugin.getCommand("home").setExecutor(new HomeCommand());
        }
        plugin.getCommand("visiting").setExecutor(new VisitingCommand());
        plugin.getCommand("memberhelp").setExecutor(new MemberHelpCommand());

        // Log
        PrivateReserve.logger().info("Members enabled.");
    }

    public static MemberDatabase data() {
        return MEMBER_DATA;
    }
}
