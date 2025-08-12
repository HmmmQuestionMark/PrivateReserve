package me.hqm.document;

import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface Document extends DocumentCompatible {
    default String getId() {
        return has("id") ? get("id", PersistentDataType.STRING) : null;
    }

    boolean has(String key);

    <P, C> boolean has(String key, PersistentDataType<P, C> type);

    <P, C> @Nullable C get(String key, PersistentDataType<P, C> type);

    <P, C> C getOrDefault(String key, PersistentDataType<P, C> type, C defaultValue);

    @Nullable
    Document getSection(String key);

    boolean isEmpty();

    <P, C> void set(String key, @NotNull PersistentDataType<P, C> type, @NotNull C value);

    void remove(String key);
}
