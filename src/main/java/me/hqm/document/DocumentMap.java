package me.hqm.document;

import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object representing a section of document.
 */
@SuppressWarnings("unchecked")
public abstract class DocumentMap extends HashMap<String, Object> implements Document {
    @Override
    public boolean has(String key) {
        return containsKey(key);
    }

    @Override
    public <P, C> boolean has(String key, PersistentDataType<P, C> type) {
        return containsKey(key) && type.getComplexType().isInstance(get(key));
    }

    @Override
    public <P, C> @Nullable C get(String key, PersistentDataType<P, C> type) {
        return has(key) ? (C) get(key) : null;
    }

    @Override
    public <P, C> C getOrDefault(String key, PersistentDataType<P, C> type, C defaultValue) {
        return (C) getOrDefault(key, defaultValue);
    }

    @Override
    public Map<String, Object> asMap() {
        return this;
    }

    @Override
    public <P, C> void set(String key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        put(key, value);
    }

    @Override
    public void remove(String key) {
        remove((Object) key);
    }
}
