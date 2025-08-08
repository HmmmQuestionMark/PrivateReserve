package me.hqm.privatereserve;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unchecked")
public class Setting {
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

    public static final boolean HOME_ENABLED = getConfig().getBoolean("home.enabled", true);
    public static final boolean LANDMARK_ENABLED = getConfig().getBoolean("landmark.enabled", true);

    public static final int LANDMARK_LIMIT = getConfig().getInt("landmark.limit", 5);

    private static ConfigurationSection getConfig() {
        return JavaPlugin.getProvidingPlugin(Setting.class).getConfig();
    }
}
