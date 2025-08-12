package me.hqm.document.pdc;

import me.hqm.document.Document;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PDCDocument implements Document {

    static final PersistentDataType[] SUPPORTED_TYPES = {
            PersistentDataType.BOOLEAN,
            PersistentDataType.BYTE,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.DOUBLE,
            PersistentDataType.FLOAT,
            PersistentDataType.INTEGER,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.SHORT,
            PersistentDataType.STRING,
            PersistentDataType.LIST.booleans(),
            PersistentDataType.LIST.bytes(),
            PersistentDataType.LIST.byteArrays(),
            PersistentDataType.LIST.doubles(),
            PersistentDataType.LIST.floats(),
            PersistentDataType.LIST.integers(),
            PersistentDataType.LIST.integerArrays(),
            PersistentDataType.LIST.longs(),
            PersistentDataType.LIST.longArrays(),
            PersistentDataType.LIST.shorts(),
            PersistentDataType.LIST.strings(),
    };

    final JavaPlugin plugin;
    final PersistentDataContainer container;

    public PDCDocument(PersistentDataContainer container) {
        plugin = JavaPlugin.getProvidingPlugin(PDCDocument.class);
        this.container = container;
    }

    @Override
    public boolean has(String key) {
        return container.has(namespacedKey(key));
    }

    @Override
    public <P, C> boolean has(String key, PersistentDataType<P, C> type) {
        return container.has(namespacedKey(key), type);
    }

    @Override
    public <P, C> @Nullable C get(String key, PersistentDataType<P, C> type) {
        return container.get(namespacedKey(key), type);
    }

    @Override
    public <P, C> C getOrDefault(String key, PersistentDataType<P, C> type, C defaultValue) {
        return container.getOrDefault(namespacedKey(key), type, defaultValue);
    }

    @Override
    public @org.jetbrains.annotations.Nullable Document getSection(String key) {
        return new PDCDocument(container.get(namespacedKey(key), PersistentDataType.TAG_CONTAINER));
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> data = new HashMap<>();
        for (NamespacedKey nsk : container.getKeys()) {
            if (nsk.namespace().equals(plugin.namespace())) {
                for (PersistentDataType type : SUPPORTED_TYPES) {
                    if (container.has(nsk, type)) {
                        data.put(nsk.getKey(), container.get(nsk, type));
                        break;
                    }
                }
                if (container.has(nsk, PersistentDataType.TAG_CONTAINER)) {
                    data.put(nsk.getKey(), new PDCDocument(container.get(nsk, PersistentDataType.TAG_CONTAINER)).asMap());
                }
                if (container.has(nsk, PersistentDataType.LIST.dataContainers())) {
                    List<Map<String, Object>> mapList = new ArrayList<>();
                    for (PersistentDataContainer listedContainer : Objects.requireNonNull(container.get(nsk, PersistentDataType.LIST.dataContainers()))) {
                        mapList.add(new PDCDocument(listedContainer).asMap());
                    }
                    data.put(nsk.getKey(), mapList);
                }
            }
        }
        return data;
    }

    @Override
    public <P, C> void set(String key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        container.set(namespacedKey(key), type, value);
    }

    @Override
    public void remove(String key) {
        container.remove(namespacedKey(key));
    }

    public void clear() {
        for (NamespacedKey nsk : container.getKeys()) {
            if (nsk.namespace().equals(plugin.namespace())) {
                container.remove(nsk);
            }
        }
    }

    private NamespacedKey namespacedKey(String key) {
        return new NamespacedKey(plugin, key);
    }
}
