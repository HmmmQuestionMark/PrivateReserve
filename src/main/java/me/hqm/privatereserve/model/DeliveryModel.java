package me.hqm.privatereserve.model;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Model;
import com.destroystokyo.paper.entity.Pathfinder;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.task.deliverymob.DeliveryTaskType;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryModel implements Model {
    // -- DATA -- //

    // NonNullable
    private final String deliveryMobId;
    private final EntityType deliveryMobEntityType;
    private final DeliveryTaskType currentTaskType;
    private final List<Long> loadedChunks;

    // -- CONSTRUCTORS -- //

    public DeliveryModel(MobDeliveryTask task) {
        deliveryMobId = task.getMobId();
        deliveryMobEntityType = task.getEntityType();
        currentTaskType = task.getType();
        loadedChunks = new ArrayList<>();
        register();
    }

    public DeliveryModel(String id, DataSection data) {
        deliveryMobId = id;
        deliveryMobEntityType = EntityType.valueOf(data.getString("deliveryMobEntityType"));
        currentTaskType = DeliveryTaskType.valueOf(deliveryMobEntityType, data.getString("currentTaskType"));
        loadedChunks = data.getLongList("loadedChunks");
        MobDeliveryTask.createFromData(currentTaskType, deliveryMobId).start();
    }

    // -- GETTERS -- //

    public String getId() {
        return getKey();
    }

    public DeliveryTaskType getCurrentTaskType() {
        return currentTaskType;
    }

    public List<Long> getLoadedChunks() {
        return loadedChunks;
    }

    @Override
    public String getKey() {
        return deliveryMobId;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("deliveryMobEntityType", deliveryMobEntityType.name());
        map.put("currentTaskType", currentTaskType.name());
        map.put("loadedChunks", loadedChunks);
        return map;
    }

    // -- MUTATORS -- //

    public void addChunk(Chunk chunk) {
        this.loadedChunks.add(chunk.getChunkKey());
        register();
    }

    public void addPath(Pathfinder.PathResult result) {
        if (result != null) {
            Location previous = PrivateReserve.MOB_DELIVERY_R.fromKey(deliveryMobId).get().getCurrentLocation();
            World world = previous.getWorld();
            for (Location next : result.getPoints()) {
                int minX = Math.min(previous.getBlockX(), next.getBlockX());
                int maxX = Math.max(previous.getBlockX(), next.getBlockX());
                int minZ = Math.min(previous.getBlockZ(), next.getBlockZ());
                int maxZ = Math.max(previous.getBlockZ(), next.getBlockZ());
                previous = next;

                for (int x = minX; x < maxX; x++) {
                    for (int z = minZ; z < maxZ; z++) {
                        Chunk chunk = world.getChunkAt(x, z);
                        if (!loadedChunks.contains(chunk.getChunkKey())) {
                            loadedChunks.add(chunk.getChunkKey());
                            chunk.addPluginChunkTicket(PrivateReserve.PLUGIN);
                        }
                    }
                }
            }
        }
        register();
    }

    // -- UTIL -- //

    public void loadChunks() {
        World currentWorld = PrivateReserve.MOB_DELIVERY_R.fromKey(deliveryMobId).get().getCurrentLocation().getWorld();
        for (Long chunkKey : loadedChunks) {
            Chunk chunk = currentWorld.getChunkAt(chunkKey);
            chunk.addPluginChunkTicket(PrivateReserve.PLUGIN);
        }
    }

    @Override
    public void register() {
        PrivateReserve.DELIVERY_R.register(this);
    }

    public void clear() {
        PrivateReserve.DELIVERY_R.clearChunkTicketsForDelivery(this);
        PrivateReserve.DELIVERY_R.remove(deliveryMobId);
    }
}
