package me.hqm.document;

import java.util.Map;

public interface DocumentFormat {
    SupportedFormat format();

    byte[] toRaw(Map<String, Object> data);

    DocumentMap fromRaw(byte[] raw);
}
