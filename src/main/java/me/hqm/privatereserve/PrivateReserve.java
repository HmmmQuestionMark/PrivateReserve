package me.hqm.privatereserve;

import com.demigodsrpg.chitchat.Chitchat;
import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.command.DebugCommand;
import me.hqm.privatereserve.command.LockModeCommand;
import me.hqm.privatereserve.command.chat.ClearNickNameCommand;
import me.hqm.privatereserve.command.chat.ClearPronounsCommand;
import me.hqm.privatereserve.command.chat.NickNameCommand;
import me.hqm.privatereserve.command.chat.PronounsCommand;
import me.hqm.privatereserve.command.location.HomeCommand;
import me.hqm.privatereserve.command.location.LandmarkCommand;
import me.hqm.privatereserve.command.location.SpawnCommand;
import me.hqm.privatereserve.command.location.VisitingCommand;
import me.hqm.privatereserve.command.member.*;
import me.hqm.privatereserve.listener.DeliveryMobListener;
import me.hqm.privatereserve.listener.LockedBlockListener;
import me.hqm.privatereserve.listener.PlayerListener;
import me.hqm.privatereserve.registry.*;
import me.hqm.privatereserve.registry.file.*;
import me.hqm.privatereserve.tag.ChatTag;
import me.hqm.privatereserve.task.TimeTask;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

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
    public static MobDeliveryRegistry MOB_DELIVERY_R;
    public static DeliveryRegistry DELIVERY_R;

    void enableFile() {
        PLAYER_R = new FPlayerRegistry();
        LOCKED_R = new FLockedBlockRegistry();
        RELATIONAL_R = new FRelationalDataRegistry();
        LANDMARK_R = new FLandmarkRegistry();

        if (Setting.DELIVERY_MOB_ENABLED.getBoolean()) {
            MOB_DELIVERY_R = new FMobDeliveryRegistry();
            DELIVERY_R = new FDeliveryRegistry();
        }
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

        if (Setting.DELIVERY_MOB_ENABLED.getBoolean()) {
            MOB_DELIVERY_R.loadAllFromDb();
            DELIVERY_R.loadAllFromDb();

            // Load all chunks
            DELIVERY_R.loadAllChunks();
        }

        // Listeners
        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), plugin);
        manager.registerEvents(new LockedBlockListener(), plugin);
        if (Setting.DELIVERY_MOB_ENABLED.getBoolean()) {
            manager.registerEvents(new DeliveryMobListener(), plugin);
        }

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
        if (Setting.HOME_ENABLED.getBoolean()) {
            plugin.getCommand("home").setExecutor(new HomeCommand());
        }
        if (Setting.LANDMARK_ENABLED.getBoolean()) {
            plugin.getCommand("landmark").setExecutor(new LandmarkCommand());
        }
        if (Setting.DELIVERY_MOB_ENABLED.getBoolean()) {
            //plugin.getCommand("delivery").setExecutor();
        }
        plugin.getCommand("visiting").setExecutor(new VisitingCommand());
        plugin.getCommand("lockmode").setExecutor(new LockModeCommand());
        plugin.getCommand("memberhelp").setExecutor(new MemberHelpCommand());
        if (Setting.DEBUG.getBoolean()) {
            plugin.getCommand("prdebug").setExecutor(new DebugCommand());
        }

        // Register tasks
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(PLUGIN, RELATIONAL_R::clearExpired, 20, 20);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PLUGIN, new TimeTask(), 1, 1);

        // Build chat format
        Chitchat.getChatFormat().addAll(new PlayerTag[]{
                ChatTag.ADMIN_TAG, ChatTag.VISITOR_TAG, ChatTag.ALTERNATE_TAG, ChatTag.TRUSTED_TAG, ChatTag.NAME_TAG
        });
    }

    public void disable() {

        if (Setting.DELIVERY_MOB_ENABLED.getBoolean()) {
            // Unload chunks
            DELIVERY_R.clearAllChunks();
        }

        // Manually unregister events
        HandlerList.unregisterAll(PLUGIN);
        Bukkit.getScheduler().cancelTasks(PLUGIN);
    }
}
