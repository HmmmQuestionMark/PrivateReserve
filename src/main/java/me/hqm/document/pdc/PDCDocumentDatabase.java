package me.hqm.document.pdc;

import me.hqm.document.DocumentDatabase;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class PDCDocumentDatabase<H extends PersistentDataHolder>
        implements DocumentDatabase<PDCDocument> {
    protected final JavaPlugin plugin;

    public PDCDocumentDatabase() {
        plugin = JavaPlugin.getProvidingPlugin(PDCDocumentDatabase.class);
    }

    @Override
    public Optional<PDCDocument> fromId(String id) {
        for (H holder : getHolders()) {
            if (getId(holder).equals(id)) {
                return Optional.of(new PDCDocument(holder.getPersistentDataContainer()));
            }
        }
        return Optional.empty();
    }

    @Override
    public void add(PDCDocument value) {
        // Do nothing.
    }

    @Override
    public void remove(String key) {
        // Do nothing.
    }

    @Override
    public void save(String key) {
        // Do nothing.
    }

    @Override
    public void load(String key) {
        // Do nothing.
    }

    @Override
    public void loadAll() {
        // Do nothing.
    }

    @Override
    public void purge() {
        getRawData().values().forEach(PDCDocument::clear);
    }

    @Override
    public Map<String, PDCDocument> getRawData() {
        Map<String, PDCDocument> data = new HashMap<>();
        for (H holder : getHolders()) {
            data.put(getId(holder), new PDCDocument(holder.getPersistentDataContainer()));
        }
        return data;
    }

    @Override
    public void write(PDCDocument document) {
        // Do nothing.
    }

    public abstract String getId(H holder);

    public abstract Set<H> getHolders();
}
