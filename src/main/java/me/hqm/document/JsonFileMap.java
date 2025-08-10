package me.hqm.document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Object representing a section of a json file.
 */
@SuppressWarnings("unchecked")
public class JsonFileMap extends HashMap<String, Object> implements DocumentMap {

    // -- CONSTRUCTORS -- //

    /**
     * Default constructor.
     */
    private JsonFileMap() {
    }

    /**
     * Constructor accepting default data.
     *
     * @param data Default data.
     */
    public JsonFileMap(Map<String, Object> data) {
        if (data != null) {
            clear();
            putAll(data);
        } else {
            throw new NullPointerException("Section data cannot be null, is this a valid section?");
        }
    }

    // -- UTILITY METHODS -- //

    /**
     * Save this section to a json file.
     *
     * @param dataFile The file to hold the section data.
     * @return Save success or failure.
     */
    public boolean save(File dataFile) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String json = gson.toJson(this, Map.class);
            PrintWriter writer = new PrintWriter(dataFile);
            writer.print(json);
            writer.close();
            return true;
        } catch (Exception oops) {
            oops.printStackTrace();
        }
        return false;
    }

    /**
     * Save this section to a json file in a pretty format.
     *
     * @param dataFile The file to hold the section data.
     * @return Save success or failure.
     */
    public boolean savePretty(File dataFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
            String json = gson.toJson(this, Map.class);
            PrintWriter writer = new PrintWriter(dataFile);
            writer.print(json);
            writer.close();
            return true;
        } catch (Exception oops) {
            oops.printStackTrace();
        }
        return false;
    }

    // -- GETTERS -- //

    public JsonFileMap getSectionNullable(String s) {
        try {
            JsonFileMap section = new JsonFileMap();
            section.putAll((Map) get(s));
            return section;
        } catch (Exception ignored) {
        }
        return null;
    }

    // -- MUTATORS -- //

    public JsonFileMap createSection(String s) {
        JsonFileMap section = new JsonFileMap();
        put(s, section);
        return section;
    }

    public JsonFileMap createSection(String s, Map<String, Object> map) {
        JsonFileMap section = new JsonFileMap();
        section.putAll(map);
        put(s, section);
        return section;
    }

    @Override
    public JsonFileMap toFJsonSection() {
        return this;
    }

    /**@Override public MJsonSection toMJsonSection() {
    return new MJsonSection(this);
    }**/
}
