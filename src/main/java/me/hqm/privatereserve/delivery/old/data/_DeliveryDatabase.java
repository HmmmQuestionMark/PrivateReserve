package me.hqm.privatereserve.delivery.old.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentDatabase;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.delivery.Deliveries;
import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public interface _DeliveryDatabase extends DocumentDatabase<_DeliveryDocument> {
    String NAME = "deliveries";

    @Override
    default _DeliveryDocument createDocument(String stringKey, Document data) {
        return new _DeliveryDocument(stringKey, data);
    }

    default void cancel(Entity entity) {
        Optional<_DeliveryDocument> maybe = fromId(entity.getUniqueId().toString());
        if (maybe.isPresent()) {
            _DeliveryDocument delivery = maybe.get();
            Optional<DeliveryDocument> maybe2 = Deliveries.data().fromEntity(entity);
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
        Bukkit.getWorlds().forEach(world -> world.removePluginChunkTickets(PrivateReserve.plugin()));
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
            if (!other.getId().equals(model.getId())) {
                if (other.getLoadedChunks().contains(chunkKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    default void clearChunkTicketsForDelivery(_DeliveryDocument model) {
        World world = Deliveries.data().fromId(model.getId()).get().getCurrentLocation().getWorld();
        for (Long chunkKey : getUniqueChunks(model)) {
            world.removePluginChunkTicket((int) (long) chunkKey, (int) (chunkKey >> 32), PrivateReserve.plugin());
        }
    }
}
