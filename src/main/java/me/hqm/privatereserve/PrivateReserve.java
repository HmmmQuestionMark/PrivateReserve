package me.hqm.privatereserve;

import com.demigodsrpg.chitchat.Chitchat;
import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.command.DebugCommand;
import me.hqm.privatereserve.command.LockModeCommand;
import me.hqm.privatereserve.command.chat.*;
import me.hqm.privatereserve.command.location.HomeCommand;
import me.hqm.privatereserve.command.location.LandmarkCommand;
import me.hqm.privatereserve.command.location.SpawnCommand;
import me.hqm.privatereserve.command.location.VisitingCommand;
import me.hqm.privatereserve.command.member.*;
import me.hqm.privatereserve.dungeon.mob.DungeonMobs;
import me.hqm.privatereserve.listener.LockedBlockListener;
import me.hqm.privatereserve.listener.PlayerListener;
import me.hqm.privatereserve.registry.*;
import me.hqm.privatereserve.registry.file.*;
import me.hqm.privatereserve.runnable.TimeRunnable;
import me.hqm.privatereserve.tag.ChatTag;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.logging.Logger;

public class PrivateReserve {

    public static PrivateReserve PRIVATE_RESERVE;
    public static Plugin PLUGIN;
    public static Logger CONSOLE;
    public static String SAVE_PATH;

    // -- DATA -- //

    public static PlayerRegistry PLAYER_R;
    public static LockedBlockRegistry LOCKED_R;
    public static RelationalDataRegistry RELATIONAL_R;
    public static LandmarkRegistry LANDMARK_R;

    void enableFile() {
        PLAYER_R = new FPlayerRegistry();
        LOCKED_R = new FLockedBlockRegistry();
        RELATIONAL_R = new FRelationalDataRegistry();
        LANDMARK_R = new FLandmarkRegistry();
    }

    // -- LOGIC -- //

    public PrivateReserve(PrivateReservePlugin plugin) {
        // Define instances
        PLUGIN = plugin;
        PRIVATE_RESERVE = this;
        CONSOLE = plugin.getLogger();

        // Define the save path
        SAVE_PATH = plugin.getDataFolder().getPath() + "/data/";

        enableFile();

        CONSOLE.info("Json file saving enabled.");

        // Load all from data
        PLAYER_R.loadAllFromDb();
        LOCKED_R.loadAllFromDb();
        RELATIONAL_R.loadAllFromDb();
        LANDMARK_R.loadAllFromDb();

        // Listeners
        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), plugin);
        manager.registerEvents(new LockedBlockListener(), plugin);

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
        if(Setting.HOME_ENABLED) {
            plugin.getCommand("home").setExecutor(new HomeCommand());
        }
        if(Setting.LANDMARK_ENABLED) {
            plugin.getCommand("landmark").setExecutor(new LandmarkCommand());
        }
        plugin.getCommand("visiting").setExecutor(new VisitingCommand());
        plugin.getCommand("lockmode").setExecutor(new LockModeCommand());
        plugin.getCommand("memberhelp").setExecutor(new MemberHelpCommand());
        plugin.getCommand("prdebug").setExecutor(new DebugCommand());

        // Register dungeon mobs
        Arrays.asList(DungeonMobs.values()).forEach(mob -> {
            manager.registerEvents(mob, plugin);
            mob.registerRunnables(plugin);
        });

        // Register tasks
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(PLUGIN, RELATIONAL_R::clearExpired, 20, 20);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PLUGIN, new TimeRunnable(), 1, 1);

        // Build chat format
        Chitchat.getChatFormat().addAll(new PlayerTag[]{
                ChatTag.ADMIN_TAG, ChatTag.VISITOR_TAG, ChatTag.ALTERNATE_TAG, ChatTag.TRUSTED_TAG, ChatTag.NAME_TAG
        });
    }

    public void disable() {
        // Manually unregister events
        HandlerList.unregisterAll(PLUGIN);
        Bukkit.getScheduler().cancelTasks(PLUGIN);
    }
}
