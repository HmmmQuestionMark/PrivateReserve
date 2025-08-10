package me.hqm.privatereserve.delivery.old.data;

import me.hqm.document.DocumentMap;
import me.hqm.document.Database;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface _DeliveryDatabase extends Database<_DeliveryDocument> {
    String NAME = "deliveries";

    @Override
    default _DeliveryDocument fromDataSection(String stringKey, DocumentMap data) {
        return new _DeliveryDocument(stringKey, data);
    }

    default void cancel(Entity entity) {
        Optional<_DeliveryDocument> maybe = fromKey(entity.getUniqueId().toString());
        if (maybe.isPresent()) {
            _DeliveryDocument delivery = maybe.get();
            Optional<DeliveryDocument> maybe2 = _PrivateReserve.DELIVERY_DATA.fromEntity(entity);
            if (maybe2.isPresent()) {
                DeliveryDocument mob = maybe2.get();
                delivery.clear();
                mob.setActive(false);
            }
        }
    }

    default void loadAllChunks() {
        getRawData().values().forEach(_DeliveryDocument::loadChunks);
    }

    default void clearAllChunks() {
        Bukkit.getWorlds().forEach(world -> world.removePluginChunkTickets(_PrivateReserve.PLUGIN));
    }

    default List<_DeliveryDocument> getDeliveriesAlongChunk(Chunk chunk) {
        Long chunkKey = chunk.getChunkKey();
        List<_DeliveryDocument> models = new ArrayList<>();
        for (_DeliveryDocument model : getRawData().values()) {
            if (model.getLoadedChunks().contains(chunkKey)) {
                models.add(model);
            }
        }
        return models;
    }

    default List<Long> getUniqueChunks(_DeliveryDocument model) {
        List<Long> chunks = model.getLoadedChunks();
        for (_DeliveryDocument other : getRawData().values()) {
            if (!other.getId().equals(model.getId())) {
                chunks.removeAll(other.getLoadedChunks());
            }
        }
        return chunks;
    }

    default boolean doOthersUseChunk(_DeliveryDocument model, Chunk chunk) {
        Long chunkKey = chunk.getChunkKey();
        for (_DeliveryDocument other : getRawData().values()) {
            if (!other.getKey().equals(model.getId())) {
                if (other.getLoadedChunks().contains(chunkKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    default void clearChunkTicketsForDelivery(_DeliveryDocument model) {
        World world = _PrivateReserve.DELIVERY_DATA.fromKey(model.getKey()).get().getCurrentLocation().getWorld();
        for (Long chunkKey : getUniqueChunks(model)) {
            world.removePluginChunkTicket((int) (long) chunkKey, (int) (chunkKey >> 32), _PrivateReserve.PLUGIN);
        }
    }
}
