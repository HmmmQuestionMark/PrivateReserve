package me.hqm.document;

import java.util.Map;

public interface Document {
    String getKey();

    Map<String, Object> serialize();

    void register();
}
