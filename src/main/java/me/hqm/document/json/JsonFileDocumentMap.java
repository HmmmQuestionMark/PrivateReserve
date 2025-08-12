package me.hqm.document.json;

import me.hqm.document.Document;
import me.hqm.document.DocumentMap;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Object representing a section of a json file.
 */
@SuppressWarnings("unchecked")
public class JsonFileDocumentMap extends DocumentMap {

    @SuppressWarnings("rawtypes")
    static final PersistentDataType[] FLATTENED_TYPES = {
            PersistentDataType.INTEGER,
            PersistentDataType.LONG,
            PersistentDataType.LIST.integers(),
            PersistentDataType.LIST.longs(),
    };

    // -- CONSTRUCTORS -- //

    /**
     * Default constructor.
     */
    private JsonFileDocumentMap() {
    }

    /**
     * Constructor accepting default data.
     *
     * @param data Default data.
     */
    public JsonFileDocumentMap(Map<String, Object> data) {
        if (data != null) {
            clear();
            putAll(data);
        } else {
            throw new NullPointerException("Section data cannot be null, is this a valid section?");
        }
    }

    // -- GETTERS -- //

    public JsonFileDocumentMap getSectionNullable(String s) {
        try {
            JsonFileDocumentMap section = new JsonFileDocumentMap();
            section.putAll((Map<String, Object>) get(s));
            return section;
        } catch (Exception ignored) {
        }
        return null;
    }

    // -- MUTATORS -- //

    public JsonFileDocumentMap createSection(String s) {
        JsonFileDocumentMap section = new JsonFileDocumentMap();
        put(s, section);
        return section;
    }

    public JsonFileDocumentMap createSection(String s, Map<String, Object> map) {
        JsonFileDocumentMap section = new JsonFileDocumentMap();
        section.putAll(map);
        put(s, section);
        return section;
    }

    @Override
    public @Nullable Document getSection(String key) {
        try {
            JsonFileDocumentMap section = new JsonFileDocumentMap();
            section.putAll((Map<String, Object>) get(key));
            return section;
        } catch (Exception ignored) {
        }
        return null;
    }

    // -- OVERRIDE -- //

    @Override
    public <P, C> boolean has(String key, PersistentDataType<P, C> type) {

        return super.has(key, type);
    }

    @Override
    public <P, C> @javax.annotation.Nullable C get(String key, PersistentDataType<P, C> type) {
        if (needsUnflattening(type)) {
            return has(key) ? unflatten(key, type) : null;
        }
        return super.get(key, type);
    }

    @Override
    public <P, C> C getOrDefault(String key, PersistentDataType<P, C> type, C defaultValue) {
        if (needsUnflattening(type)) {
            return has(key) ? unflatten(key, type) : defaultValue;
        }
        return super.get(key, type);
    }

    private <P, C> boolean needsUnflattening(PersistentDataType<P, C> type) {
        return Arrays.asList(FLATTENED_TYPES).contains(type);
    }

    private <P, C> C unflatten(String key, PersistentDataType<P, C> type) {
        if (PersistentDataType.INTEGER.equals(type))
            return (C) (Integer) ((Double) get(key)).intValue();
        if (PersistentDataType.LONG.equals(type))
            return (C) (Long) ((Double) get(key)).longValue();
        if (PersistentDataType.LIST.integers().equals(type))
            return (C) ((List<Double>) super.get(key)).stream().map(Double::intValue).collect(Collectors.toList());
        if (PersistentDataType.LIST.longs().equals(type))
            return (C) ((List<Double>) super.get(key)).stream().map(Double::longValue).collect(Collectors.toList());
        return (C) get(key);
    }
}
