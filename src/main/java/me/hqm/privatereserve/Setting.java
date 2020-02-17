package me.hqm.privatereserve;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unchecked")
public class Setting {
    public static final boolean MONGODB_PERSISTENCE = getConfig().getBoolean("mongo.use", false);
    public static final String MONGODB_HOSTNAME = getConfig().getString("mongo.hostname", "localhost");
    public static final int MONGODB_PORT = getConfig().getInt("mongo.port", 27017);
    public static final String MONGODB_DATABASE = getConfig().getString("mongo.database", "chat");
    public static final String MONGODB_USERNAME = getConfig().getString("mongo.username", "chat");
    public static final String MONGODB_PASSWORD = getConfig().getString("mongo.password", "chat");
    public static final boolean SAVE_PRETTY = getConfig().getBoolean("file.save_pretty", false);

    public static final String SPAWN_REGION = getConfig().getString("region.spawn", "spawn");
    public static final String SPAWN_REGION_WORLD = getConfig().getString("region.spawn_world", "world");
    public static final String VISITOR_REGION = getConfig().getString("region.visitor", "visitor");
    public static final String VISITOR_REGION_WORLD = getConfig().getString("region.visitor_world", "world");

    public static final int MAX_TARGET_RANGE = getConfig().getInt("targeting.max_range", 100);

    public static final int DAYLIGHT_MULTIPLIER = getConfig().getInt("time_multiplier.daylight", 2);
    public static final int NIGHT_MULTIPLIER = getConfig().getInt("time_multiplier.night", 1);
    public static final ImmutableList<String> TIME_MULTIPLIER_WORLDS =
            ImmutableList.copyOf(getConfig().getStringList("time_multiplier.worlds"));

    private static ConfigurationSection getConfig() {
        return JavaPlugin.getProvidingPlugin(Setting.class).getConfig();
    }
}
