package me.hqm.privatereserve;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.delivery.listener.DeliveryListener;
import me.hqm.privatereserve.lockedblock.LockModeCommand;
import me.hqm.privatereserve.landmark.command.LandmarkCommand;
import me.hqm.privatereserve.delivery.data.DeliveryDatabase;
import me.hqm.privatereserve.delivery.data.FileDeliveryDatabase;
import me.hqm.privatereserve.delivery.old.data.File_DeliveryDatabase;
import me.hqm.privatereserve.delivery.old.data._DeliveryDatabase;
import me.hqm.privatereserve.landmark.data.FileLandmarkDatabase;
import me.hqm.privatereserve.landmark.data.LandmarkDatabase;
import me.hqm.privatereserve.lockedblock.FileLockedBlockDatabase;
import me.hqm.privatereserve.lockedblock.LockedBlockDatabase;
import me.hqm.privatereserve.lockedblock.LockedBlockListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class _PrivateReserve {

    // -- DATABASES -- //


    public static LockedBlockDatabase LOCKED_DATA;
    public static LandmarkDatabase LANDMARK_DATA;
    public static DeliveryDatabase DELIVERY_DATA;
    public static _DeliveryDatabase _DELIVERY_DATA;

    void enableFile() {

        LOCKED_DATA = new FileLockedBlockDatabase();
        LANDMARK_DATA = new FileLandmarkDatabase();

        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            DELIVERY_DATA = new FileDeliveryDatabase();
            _DELIVERY_DATA = new File_DeliveryDatabase();
        }
    }

    // -- LOGIC -- //

    public _PrivateReserve(PrivateReserve plugin) {


        enableFile();


        // Load all from data

        LOCKED_DATA.loadAllFromDb();
        LANDMARK_DATA.loadAllFromDb();

        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            DELIVERY_DATA.loadAllFromDb();
            _DELIVERY_DATA.loadAllFromDb();

            // Load all chunks
            _DELIVERY_DATA.loadAllChunks();
        }

        // Listeners
        PluginManager manager = plugin.getServer().getPluginManager();

        manager.registerEvents(new LockedBlockListener(), plugin);
        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            manager.registerEvents(new DeliveryListener(), plugin);
        }

        // Commands

        if (Settings.LANDMARK_ENABLED.getBoolean()) {
            plugin.getCommand("landmark").setExecutor(new LandmarkCommand());
        }
        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            //plugin.getCommand("delivery").setExecutor();
        }

        plugin.getCommand("lockmode").setExecutor(new LockModeCommand());

        if (Settings.DEBUG_ENABLED.getBoolean()) {
            plugin.getCommand("prdebug").setExecutor(new DebugCommand());
        }
    }

    public void disable() {

        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            // Unload chunks
            _DELIVERY_DATA.clearAllChunks();
        }

        // Manually unregister events
        HandlerList.unregisterAll(PLUGIN);
        Bukkit.getScheduler().cancelTasks(PLUGIN);
    }
}
