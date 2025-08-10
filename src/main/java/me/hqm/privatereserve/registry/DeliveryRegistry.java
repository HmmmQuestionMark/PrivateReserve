package me.hqm.privatereserve.registry;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.DeliveryModel;
import me.hqm.privatereserve.model.MobDeliveryModel;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public interface DeliveryRegistry extends Registry<DeliveryModel> {
    String NAME = "deliveries";

    @Override
    default DeliveryModel fromDataSection(String stringKey, DataSection data) {
        return new DeliveryModel(stringKey, data);
    }

    default void cancel(Entity entity) {
        Optional<DeliveryModel> maybe = fromKey(entity.getUniqueId().toString());
        if (maybe.isPresent()) {
            DeliveryModel delivery = maybe.get();
            Optional<MobDeliveryModel> maybe2 = PrivateReserve.MOB_DELIVERY_R.fromEntity(entity);
            if (maybe2.isPresent()) {
                MobDeliveryModel mob = maybe2.get();
                delivery.clear();
                mob.setActive(false);
            }
        }
    }

    default void loadAllChunks() {
        getRegisteredData().values().forEach(DeliveryModel::loadChunks);
    }

    default void clearAllChunks() {
        Bukkit.getWorlds().forEach(world -> world.removePluginChunkTickets(PrivateReserve.PLUGIN));
    }

    default List<DeliveryModel> getDeliveriesAlongChunk(Chunk chunk) {
        Long chunkKey = chunk.getChunkKey();
        List<DeliveryModel> models = new ArrayList<>();
        for (DeliveryModel model : getRegisteredData().values()) {
            if (model.getLoadedChunks().contains(chunkKey)) {
                models.add(model);
            }
        }
        return models;
    }

    default List<Long> getUniqueChunks(DeliveryModel model) {
        List<Long> chunks = model.getLoadedChunks();
        for (DeliveryModel other : getRegisteredData().values()) {
            if (!other.getId().equals(model.getId())) {
                chunks.removeAll(other.getLoadedChunks());
            }
        }
        return chunks;
    }

    default boolean doOthersUseChunk(DeliveryModel model, Chunk chunk) {
        Long chunkKey = chunk.getChunkKey();
        for (DeliveryModel other : getRegisteredData().values()) {
            if (!other.getKey().equals(model.getId())) {
                if (other.getLoadedChunks().contains(chunkKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    default void clearChunkTicketsForDelivery(DeliveryModel model) {
        World world = PrivateReserve.MOB_DELIVERY_R.fromKey(model.getKey()).get().getCurrentLocation().getWorld();
        for (Long chunkKey : getUniqueChunks(model)) {
            world.removePluginChunkTicket((int) (long) chunkKey, (int) (chunkKey >> 32), PrivateReserve.PLUGIN);
        }
    }
}
