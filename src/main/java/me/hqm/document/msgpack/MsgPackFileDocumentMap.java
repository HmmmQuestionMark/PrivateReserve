package me.hqm.document.msgpack;

import me.hqm.document.Document;
import me.hqm.document.DocumentMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Object representing a section of a json file.
 */
@SuppressWarnings("unchecked")
public class MsgPackFileDocumentMap extends DocumentMap {

    // -- CONSTRUCTORS -- //

    /**
     * Default constructor.
     */
    MsgPackFileDocumentMap() {
    }

    /**
     * Constructor accepting default data.
     *
     * @param data Default data.
     */
    public MsgPackFileDocumentMap(Map<String, Object> data) {
        if (data != null) {
            clear();
            putAll(data);
        } else {
            throw new NullPointerException("Section data cannot be null, is this a valid section?");
        }
    }

    // -- GETTERS -- //

    public MsgPackFileDocumentMap getSectionNullable(String s) {
        try {
            MsgPackFileDocumentMap section = new MsgPackFileDocumentMap();
            section.putAll((Map) get(s));
            return section;
        } catch (Exception ignored) {
        }
        return null;
    }

    // -- MUTATORS -- //

    public MsgPackFileDocumentMap createSection(String s) {
        MsgPackFileDocumentMap section = new MsgPackFileDocumentMap();
        put(s, section);
        return section;
    }

    public MsgPackFileDocumentMap createSection(String s, Map<String, Object> map) {
        MsgPackFileDocumentMap section = new MsgPackFileDocumentMap();
        section.putAll(map);
        put(s, section);
        return section;
    }

    @Override
    public @Nullable Document getSection(String key) {
        try {
            MsgPackFileDocumentMap section = new MsgPackFileDocumentMap();
            section.putAll((Map<String, Object>) get(key));
            return section;
        } catch (Exception ignored) {
        }
        return null;
    }
}
