package me.hqm.document;

import java.util.Map;

public interface DocumentCompatible {
    String getId();

    Map<String, Object> asMap();
}
