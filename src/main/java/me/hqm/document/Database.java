package me.hqm.document;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

public interface Database<T extends Document> {
    Optional<T> fromKey(String key);

    T register(T value);

    T put(String key, T value);

    void remove(String key);

    @ApiStatus.Internal
    void saveToDb(String key);

    void loadFromDb(String key);

    void loadAllFromDb();

    void purge();

    T fromDataSection(String key, DocumentMap section);

    @ApiStatus.Internal
    Map<String, T> getRawData();

    boolean open();

    boolean close();
}
