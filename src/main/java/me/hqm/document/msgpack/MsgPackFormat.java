package me.hqm.document.msgpack;

import me.hqm.document.DocumentFormat;
import me.hqm.document.DocumentMap;
import me.hqm.document.SupportedFormat;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MsgPackFormat extends DocumentFormat {
    static void serializeList(MessageBufferPacker packer, List<Object> list) throws IOException {
        packer.packArrayHeader(list.size());
        for (Object value : list) {
            serializeValue(packer, value);
        }
    }

    static void serializeMap(MessageBufferPacker packer, Map<String, Object> map) throws IOException {
        packer.packMapHeader(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            packer.packString(entry.getKey());
            serializeValue(packer, entry.getValue());
        }
    }

    static void serializeValue(MessageBufferPacker packer, Object value) throws IOException {
        switch (value) {
            case null -> packer.packNil();
            case String s -> packer.packString(s);
            case Integer i -> packer.packInt(i);
            case Boolean b -> packer.packBoolean(b);
            case Double d -> packer.packDouble(d);
            case Float f -> packer.packFloat(f);
            case Long l -> packer.packLong(l);
            case Short s -> packer.packShort(s);
            case Byte b -> packer.packByte(b);
            case List list -> serializeList(packer, (List<Object>) value);
            case Map map -> serializeMap(packer, (Map<String, Object>) value);
            default -> {
            }
        }
    }

    static List<Object> deserializeList(MessageUnpacker unpacker) throws IOException {
        int listSize = unpacker.unpackArrayHeader();
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            Object value = deserializeValue(unpacker);
            list.add(value);
        }
        return list;
    }

    static DocumentMap deserializeDocumentMap(MessageUnpacker unpacker) throws IOException {
        int mapSize = unpacker.unpackMapHeader();
        DocumentMap map = new MsgPackFileDocumentMap();
        for (int i = 0; i < mapSize; i++) {
            String key = unpacker.unpackString();
            Object value = deserializeValue(unpacker);
            map.put(key, value);
        }
        return map;
    }

    static Map<String, Object> deserializeMap(MessageUnpacker unpacker) throws IOException {
        int mapSize = unpacker.unpackMapHeader();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String key = unpacker.unpackString();
            Object value = deserializeValue(unpacker);
            map.put(key, value);
        }
        return map;
    }

    static Object deserializeValue(MessageUnpacker unpacker) throws IOException {
        MessageFormat format = unpacker.getNextFormat();

        return switch (format) {
            case NIL -> null;
            case STR8, STR16, STR32, FIXSTR -> unpacker.unpackString();
            case INT8, UINT8, INT16, UINT16, INT32, UINT32, NEGFIXINT, POSFIXINT -> unpacker.unpackInt();
            case INT64, UINT64 -> unpacker.unpackLong();
            case BOOLEAN -> unpacker.unpackBoolean();
            case FLOAT32 -> unpacker.unpackFloat();
            case FLOAT64 -> unpacker.unpackDouble();
            case ARRAY16, ARRAY32, FIXARRAY -> deserializeList(unpacker);
            case MAP16, MAP32, FIXMAP -> deserializeMap(unpacker);
            default -> throw new UnsupportedOperationException("Unsupported type: " + format);
        };
    }

    @Override
    default SupportedFormat format() {
        return SupportedFormat.MESSAGEPACK;
    }

    @Override
    default byte[] toRaw(Map<String, Object> data) {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

        try {
            serializeMap(packer, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packer.toMessageBuffer().array();
    }

    @Override
    default DocumentMap fromRaw(byte[] raw) {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(raw);
        try {
            return deserializeDocumentMap(unpacker);
        } catch (IOException ignored) {
            throw new RuntimeException("Section data corrupted.");
        }
    }
}
