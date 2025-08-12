package me.hqm.document.json;

import me.hqm.document.Document;
import me.hqm.document.DocumentMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Object representing a section of a json file.
 */
@SuppressWarnings("unchecked")
public class JsonFileDocumentMap extends DocumentMap {

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
            section.putAll((Map) get(s));
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
}
