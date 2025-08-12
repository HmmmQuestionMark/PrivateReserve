package me.hqm.document.msgpack;

import me.hqm.document.Document;
import me.hqm.document.DocumentMap;
import org.jetbrains.annotations.Nullable;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private MsgPackFileDocumentMap() {
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

    public MsgPackFileDocumentMap(byte[] byteArray) {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(byteArray);
        try {
            Map<String, Object> data = deserializeMap(unpacker);
            clear();
            putAll(data);

        } catch (IOException ignored) {
            throw new RuntimeException("Section data corrupted.");
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

    List<Object> deserializeList(MessageUnpacker unpacker) throws IOException {
        int listSize = unpacker.unpackMapHeader();
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            Object value = deserializeValue(unpacker);
            list.add(value);
        }
        return list;
    }

    Map<String, Object> deserializeMap(MessageUnpacker unpacker) throws IOException {
        int mapSize = unpacker.unpackMapHeader();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String key = unpacker.unpackString();
            Object value = deserializeValue(unpacker);
            map.put(key, value);
        }
        return map;
    }

    Object deserializeValue(MessageUnpacker unpacker) throws IOException {
        MessageFormat format = unpacker.getNextFormat();

        return switch (format) {
            case NIL -> null;
            case STR8, STR16, STR32 -> unpacker.unpackString();
            case INT8, UINT8, INT16, UINT16, INT32, UINT32 -> unpacker.unpackInt();
            case INT64, UINT64 -> unpacker.unpackLong();
            case BOOLEAN -> unpacker.unpackBoolean();
            case FLOAT32 -> unpacker.unpackFloat();
            case FLOAT64 -> unpacker.unpackDouble();
            case ARRAY16, ARRAY32 -> deserializeList(unpacker);
            case MAP16, MAP32 -> deserializeMap(unpacker);
            default -> throw new UnsupportedOperationException("Unsupported type: " + format);
        };
    }
}
