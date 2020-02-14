package me.hqm.privatereserve;

import com.demigodsrpg.chitchat.Chitchat;
import com.mongodb.*;
import com.mongodb.client.*;
import me.hqm.privatereserve.command.*;
import me.hqm.privatereserve.listener.PlayerListener;
import me.hqm.privatereserve.registry.PlayerRegistry;
import me.hqm.privatereserve.registry.file.FPlayerRegistry;
import me.hqm.privatereserve.registry.mongo.MPlayerRegistry;
import me.hqm.privatereserve.tag.ChatTag;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.logging.Logger;

public class ReserveChat {

    public static ReserveChat RESERVE_CHAT;
    public static Plugin PLUGIN;
    public static Logger CONSOLE;
    public static String SAVE_PATH;

    private static MongoDatabase DATABASE;

    // -- DATA -- //

    public static PlayerRegistry PLAYER_R;

    void enableMongo(MongoDatabase database) {
        PLAYER_R = new MPlayerRegistry(database);
    }

    void enableFile() {
        PLAYER_R = new FPlayerRegistry();
    }

    // -- LOGIC -- //

    public ReserveChat(ReserveChatPlugin plugin) {
        // Define instances
        PLUGIN = plugin;
        RESERVE_CHAT = this;
        CONSOLE = plugin.getLogger();

        // Define the save path
        SAVE_PATH = plugin.getDataFolder().getPath() + "/data/";

        // Test for Mongo connection if enabled
        if (Setting.MONGODB_PERSISTENCE) {
            try {
                String hostname = Setting.MONGODB_HOSTNAME;
                int port = Setting.MONGODB_PORT;
                String database = Setting.MONGODB_DATABASE;
                String username = Setting.MONGODB_USERNAME;
                String password = Setting.MONGODB_PASSWORD;
                ServerAddress address = new ServerAddress(hostname, port);
                MongoCredential credential =
                        MongoCredential.createCredential(username, database, password.toCharArray());
                MongoClient client = MongoClients.create(MongoClientSettings.builder().applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(address))).credential(credential).build());
                DATABASE =
                        client.getDatabase(database);

                // Create Registries
                enableMongo(DATABASE);

                CONSOLE.info("MongoDB enabled.");
            } catch (Exception oops) {
                oops.printStackTrace();
                CONSOLE.warning("MongoDB connection failed. Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(PLUGIN);
                return;
            }
        } else {
            enableFile();

            CONSOLE.info("Json file saving enabled.");
        }

        // Listeners
        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), plugin);

        // Commands
        plugin.getCommand("nickname").setExecutor(new NickNameCommand());
        plugin.getCommand("pronouns").setExecutor(new PronounsCommand());
        plugin.getCommand("clearnickname").setExecutor(new ClearNickNameCommand());
        plugin.getCommand("clearpronouns").setExecutor(new ClearPronounsCommand());

        // Build chat format
        Chitchat.getChatFormat().add(ChatTag.ADMIN_TAG).add(ChatTag.NAME_TAG);
    }

    public void disable() {
        // Manually unregister events
        HandlerList.unregisterAll(PLUGIN);
    }
}
