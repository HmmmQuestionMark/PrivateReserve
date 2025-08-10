package me.hqm.privatereserve;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public enum Settings {
    // File Databases
    FILE_ENABLED("file.enabled", true),
    FILE_FOLDER("file.folder", JavaPlugin.getProvidingPlugin(Settings.class).getDataFolder().getPath() + "/data/"),
    FILE_PRETTY("file.pretty", false),

    // Debug
    DEBUG_ENABLED("debug.enabled", false),

    // Memberships
    MEMBER_ENABLED("member.enabled", true),
    MEMBER_HOME_ENABLED("home.enabled", true),
    MEMBER_SPAWN_REGION("region.spawn", "spawn"),
    MEMBER_SPAWN_REGION_WORLD("region.spawn_world", "world"),
    VISITOR_SPAWN_REGION("region.visitor", "visiting"),
    VISITOR_SPAWN_REGION_WORLD("region.visitor_world", "world"),

    // Time
    TIME_ENABLED("time.enabled", true),
    DAYLIGHT_MULTIPLIER("time_multiplier.daylight", 2),
    NIGHT_MULTIPLIER("time_multiplier.night", 1),
    TIME_MULTIPLIER_WORLDS("time_multiplier.worlds", Collections.emptyList()),

    // Landmarks
    LANDMARK_ENABLED("landmark.enabled", true),
    LANDMARK_LIMIT("landmark.limit", 2),

    // Deliveries
    DELIVERY_MOB_ENABLED("delivery_mob.enabled", false),
    DELIVERY_MOB_LIMIT("delivery_mob.limit", 1),
    DELIVERY_MOB_INVINCIBLE("delivery_mob.invincible", true),
    DELIVERY_GHAST_ENABLED("delivery_mob.ghast.enabled", true);

    final private String path;
    final private Object defaultValue;

    Settings(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public String getString() {
        return getConfig().isString(path) ? getConfig().getString(path) : (String) defaultValue;
    }

    public boolean getBoolean() {
        return getConfig().isBoolean(path) ? getConfig().getBoolean(path) : (Boolean) defaultValue;
    }

    public int getInteger() {
        return getConfig().isInt(path) ? getConfig().getInt(path) : (Integer) defaultValue;
    }

    public List<String> getStringList() {
        return getConfig().isList(path) ?
                ImmutableList.copyOf(getConfig().getStringList(path)) : (List<String>) defaultValue;
    }

    private static ConfigurationSection getConfig() {
        return JavaPlugin.getProvidingPlugin(Settings.class).getConfig();
    }

    public static void set(String settingPath, @Nullable Object value) {
        getConfig().set(settingPath, value);
        JavaPlugin.getProvidingPlugin(Settings.class).saveConfig();
    }
}
