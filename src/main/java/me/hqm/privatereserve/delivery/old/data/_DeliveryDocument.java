package me.hqm.privatereserve.delivery.old.data;

import com.destroystokyo.paper.entity.Pathfinder;
import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.delivery.Deliveries;
import me.hqm.privatereserve.delivery.old.DeliveryTaskType;
import me.hqm.privatereserve.delivery.old.MobDeliveryTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public class _DeliveryDocument implements DocumentCompatible {
    // -- DATA -- //

    // NonNullable
    private final String deliveryMobId;
    private final EntityType deliveryMobEntityType;
    private final DeliveryTaskType currentTaskType;
    private final List<Double> loadedChunks;

    // -- CONSTRUCTORS -- //

    public _DeliveryDocument(MobDeliveryTask task) {
        deliveryMobId = task.getMobId();
        deliveryMobEntityType = task.getEntityType();
        currentTaskType = task.getType();
        loadedChunks = new ArrayList<>();
        write();
    }

    public _DeliveryDocument(String id, Document data) {
        deliveryMobId = id;
        deliveryMobEntityType = EntityType.valueOf(data.get("deliveryMobEntityType", PersistentDataType.STRING));
        currentTaskType = DeliveryTaskType.
                valueOf(deliveryMobEntityType, Objects.
                        requireNonNull(data.get("currentTaskType", PersistentDataType.STRING)));
        loadedChunks = data.get("loadedChunks", PersistentDataType.LIST.doubles());
        Objects.requireNonNull(MobDeliveryTask.createFromData(currentTaskType, deliveryMobId)).start();
    }

    // -- GETTERS -- //

    public String getId() {
        return deliveryMobId;
    }

    public DeliveryTaskType getCurrentTaskType() {
        return currentTaskType;
    }

    public List<Long> getLoadedChunks() {
        return loadedChunks.stream().map(Double::longValue).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("deliveryMobEntityType", deliveryMobEntityType.name());
        map.put("currentTaskType", currentTaskType.name());
        map.put("loadedChunks", loadedChunks);
        return map;
    }

    // -- MUTATORS -- //

    public void addChunk(Chunk chunk) {
        this.loadedChunks.add(Double.longBitsToDouble(chunk.getChunkKey()));
        write();
    }

    public void addPath(Pathfinder.PathResult result) {
        if (result != null) {
            Location previous = Deliveries.data().fromId(deliveryMobId).get().getCurrentLocation();
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
                        Double chunkKey = Double.longBitsToDouble(chunk.getChunkKey());
                        if (!loadedChunks.contains(chunkKey)) {
                            loadedChunks.add(chunkKey);
                            chunk.addPluginChunkTicket(PrivateReserve.plugin());
                        }
                    }
                }
            }
        }
        write();
    }

    // -- UTIL -- //

    public void loadChunks() {
        World currentWorld = Deliveries.data().fromId(deliveryMobId).get().getCurrentLocation().getWorld();
        for (Long chunkKey : getLoadedChunks()) {
            Chunk chunk = currentWorld.getChunkAt(chunkKey);
            chunk.addPluginChunkTicket(PrivateReserve.plugin());
        }
    }

    public void write() {
        Deliveries.___data().add(this);
    }

    public void clear() {
        Deliveries.___data().clearChunkTicketsForDelivery(this);
        Deliveries.___data().remove(deliveryMobId);
    }
}
