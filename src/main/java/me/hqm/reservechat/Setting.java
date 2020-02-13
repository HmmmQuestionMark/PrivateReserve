package me.hqm.reservechat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unchecked")
public class Setting {
    public static final boolean MONGODB_PERSISTENCE = getConfig().getBoolean("mongo.use", false);
    public static final String MONGODB_HOSTNAME = getConfig().getString("mongo.hostname", "127.0.0.1");
    public static final int MONGODB_PORT = getConfig().getInt("mongo.port", 27017);
    public static final String MONGODB_DATABASE = getConfig().getString("mongo.database", "chat");
    public static final String MONGODB_USERNAME = getConfig().getString("mongo.username", "chat");
    public static final String MONGODB_PASSWORD = getConfig().getString("mongo.password", "chat");
    public static final boolean SAVE_PRETTY = getConfig().getBoolean("file.save_pretty", false);

    private static ConfigurationSection getConfig() {
        return JavaPlugin.getProvidingPlugin(Setting.class).getConfig();
    }
}
