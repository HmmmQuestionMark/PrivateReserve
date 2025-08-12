package me.hqm.document.json;

import com.google.gson.GsonBuilder;
import me.hqm.document.DocumentFormat;
import me.hqm.document.SupportedFormat;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

public interface JsonFormat extends DocumentFormat {
    @Override
    default SupportedFormat format() {
        return SupportedFormat.JSON;
    }

    @Override
    default byte[] toRaw(Map<String, Object> data) {
        return toString(data).getBytes();
    }

    default String toString(Map<String, Object> data) {
        return new GsonBuilder().create().toJson(data, Map.class);
    }

    default Map<String, Object> fromString(String json) {
        StringReader reader = new StringReader(json);
        return read(reader);
    }

    @Override
    default JsonFileDocumentMap fromRaw(byte[] raw) {
        String rawString = new String(raw);
        StringReader reader = new StringReader(rawString);
        return read(reader);
    }

    default JsonFileDocumentMap read(Reader reader) {
        return new JsonFileDocumentMap(new GsonBuilder().create().fromJson(reader, Map.class));
    }
}
