package me.hqm.privatereserve;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public enum Settings {
    // Persistence
    PERSISTENCE_TYPE("persistence.type", "file"),

    // File
    FILE_FORMAT("persistence.file.format", "json"),
    FILE_FOLDER("persistence.file.folder", JavaPlugin.getProvidingPlugin(Settings.class).getDataFolder().getPath() + "/data/"),
    FILE_CONVERT("persistence.file.convert.on_start", true),
    FILE_CONVERT_OVERWRITE("persistence.file.convert.overwrite", true),
    FILE_CONVERT_BACKUP("persistence.file.convert.backup", true),

    // PDC
    PDC_ENABLED("persistence.pdc.enabled", false),

    // Debug
    DEBUG_ENABLED("debug.enabled", false),

    // Server links
    SERVER_LINKS("server_links", null),

    // Chat
    CHAT_TAGS_ENABLED("chat.enabled", true),

    // WorldGuard regions
    MEMBER_REGION_ENABLED("member.region.enabled", true),

    // Memberships
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

    // Locked block
    LOCKED_BLOCK_ENABLED("locked_block.enabled", true),

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

    private static ConfigurationSection getConfig() {
        return JavaPlugin.getProvidingPlugin(Settings.class).getConfig();
    }

    public static void set(String settingPath, @Nullable Object value) {
        getConfig().set(settingPath, value);
        JavaPlugin.getProvidingPlugin(Settings.class).saveConfig();
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

    public boolean notNull() {
        return getConfig().contains(path);
    }

    public Map<String, Object> getMap() {
        if (getConfig().isConfigurationSection(path)) {
            return ImmutableMap.
                    copyOf(Objects.requireNonNull(getConfig().getConfigurationSection(path)).getValues(false));
        }
        return (Map<String, Object>) defaultValue;
    }
}
