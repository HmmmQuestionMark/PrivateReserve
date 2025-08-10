package me.hqm.privatereserve;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public enum Setting {
    SAVE_PRETTY("file.save_pretty", false),
    DEBUG("debug", false),
    SPAWN_REGION("region.spawn", "spawn"),
    SPAWN_REGION_WORLD("region.spawn_world", "world"),
    VISITOR_REGION("region.visitor", "visitor"),
    VISITOR_REGION_WORLD("region.visitor_world", "world"),

    DAYLIGHT_MULTIPLIER("time_multiplier.daylight", 2),
    NIGHT_MULTIPLIER("time_multiplier.night", 1),
    TIME_MULTIPLIER_WORLDS("time_multiplier.worlds", Collections.emptyList()),

    HOME_ENABLED("home.enabled", true),
    LANDMARK_ENABLED("landmark.enabled", true),

    LANDMARK_LIMIT("landmark.limit", 2),

    DELIVERY_MOB_ENABLED("delivery_mob.enabled", false),
    DELIVERY_MOB_LIMIT("delivery_mob.limit", 1),
    DELIVERY_MOB_INVINCIBLE("delivery_mob.invincible", true),
    DELIVERY_GHAST_ENABLED("delivery_mob.ghast.enabled", true);

    final private String path;
    final private Object defaultValue;

    Setting(String path, Object defaultValue) {
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
        return JavaPlugin.getProvidingPlugin(Setting.class).getConfig();
    }

    public static void set(String settingPath, @Nullable Object value) {
        getConfig().set(settingPath, value);
    }
}
