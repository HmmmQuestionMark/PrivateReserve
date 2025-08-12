package me.hqm.document;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

public interface DocumentDatabase<D extends DocumentCompatible> {
    Optional<D> fromId(String id);

    void add(D compatible);

    void remove(String key);

    @ApiStatus.Internal
    void save(String key);

    void load(String key);

    void loadAll();

    void purge();

    void write(D document);

    D createDocument(String key, Document compatible);

    @ApiStatus.Internal
    Map<String, D> getRawData();
}
