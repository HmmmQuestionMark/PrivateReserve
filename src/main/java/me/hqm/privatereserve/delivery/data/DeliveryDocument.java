package me.hqm.privatereserve.delivery.data;

import io.papermc.paper.entity.Leashable;
import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.delivery.Deliveries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class DeliveryDocument implements DocumentCompatible {
    // -- DATA -- //

    // NonNullable
    private final String id;
    private final List<String> subIds;
    private final Component name;
    private final String ownerId;
    private final String entityType;
    private boolean active;
    private boolean frozen;

    // Nullable
    private String homeLocation;
    private String loadLocation;
    private String unloadLocation;
    private Long loadTicks;
    private Long unloadTicks;

    // -- CONSTRUCTORS -- //

    public DeliveryDocument(Entity entity, String name, String ownerId) {
        this.id = entity.getUniqueId().toString();
        this.subIds = new ArrayList<>();
        refreshSubIds(entity, false);
        if (entity.customName() != null &&
                LegacyComponentSerializer.legacySection().serialize(entity.customName()).equalsIgnoreCase(name)) {
            this.name = entity.customName();
        } else {
            this.name = LegacyComponentSerializer.legacySection().deserialize(name);
        }
        this.ownerId = ownerId;
        this.entityType = entity.getType().name();
        this.active = false;
        this.frozen = false;

        write();
    }

    public DeliveryDocument(String id, Document data) {
        this.id = id;
        subIds = data.get("subIds", PersistentDataType.LIST.strings());
        name = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(Objects.requireNonNull(data.get("name", PersistentDataType.STRING)));
        ownerId = data.get("ownerId", PersistentDataType.STRING);
        entityType = data.get("entityType", PersistentDataType.STRING);
        active = data.getOrDefault("active", PersistentDataType.BOOLEAN, false);
        frozen = data.getOrDefault("frozen", PersistentDataType.BOOLEAN, false);
        homeLocation = data.get("homeLocation", PersistentDataType.STRING);
        loadLocation = data.get("loadLocation", PersistentDataType.STRING);
        unloadLocation = data.get("unloadLocation", PersistentDataType.STRING);
        loadTicks = data.get("loadTicks", PersistentDataType.LONG);
        unloadTicks = data.get("unloadTicks", PersistentDataType.LONG);
    }

    // -- GETTERS -- //


    public String getId() {
        return id;
    }

    public List<String> getSubIds() {
        return subIds;
    }

    public Component getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public EntityType getEntityType() {
        return EntityType.valueOf(entityType);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        write();
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        write();
    }

    public boolean isEntityOrSub(Entity entity) {
        String entityId = entity.getUniqueId().toString();
        return id.equals(entityId) || subIds.contains(id);
    }

    public boolean isEntityOrSubFrozen(Entity entity) {
        return frozen && isEntityOrSub(entity);
    }

    public List<Entity> getBukkitSubEntities() {
        List<Entity> entities = new ArrayList<>();
        entities.add(getBukkitEntity());
        for (String subId : subIds) {
            entities.add(Bukkit.getEntity(UUID.fromString(subId)));
        }
        return entities;
    }

    public List<Entity> getBukkitEntities() {
        List<Entity> entities = getBukkitSubEntities();
        entities.add(getBukkitEntity());
        return entities;
    }

    public List<InventoryHolder> getInventoryHolders() {
        List<InventoryHolder> holders = new ArrayList<>();
        for (Entity entity : getBukkitEntities()) {
            if (entity instanceof InventoryHolder) {
                holders.add((InventoryHolder) entity);
            }
        }
        return holders;
    }

    public Entity getBukkitEntity() {
        return Bukkit.getEntity(UUID.fromString(id));
    }

    public Location getCurrentLocation() {
        return getBukkitEntity().getLocation();
    }

    public Location getHomeLocation() {
        return Locations.locationFromString(homeLocation);
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = Locations.stringFromLocation(homeLocation);
        write();
    }

    public Location getLoadLocation() {
        return Locations.locationFromString(loadLocation);
    }

    public void setLoadLocation(Location loadLocation) {
        this.loadLocation = Locations.stringFromLocation(loadLocation);
        write();
    }

    public Location getUnloadLocation() {
        return Locations.locationFromString(unloadLocation);
    }

    public void setUnloadLocation(Location unloadLocation) {
        this.unloadLocation = Locations.stringFromLocation(unloadLocation);
        write();
    }

    // -- MUTATORS -- //

    public Long getLoadTicks() {
        return loadTicks;
    }

    public void setLoadTicks(long loadTicks) {
        this.loadTicks = loadTicks;
        write();
    }

    public Integer getLoadSeconds() {
        if (loadTicks != null) {
            return Math.toIntExact(loadTicks / 20);
        }
        return null;
    }

    public void setLoadSeconds(int seconds) {
        setLoadTicks(seconds * 20L);
    }

    public Long getUnloadTicks() {
        return unloadTicks;
    }

    public void setUnloadTicks(long unloadTicks) {
        this.unloadTicks = unloadTicks;
        write();
    }

    public Integer getUnloadSeconds() {
        if (unloadTicks != null) {
            return Math.toIntExact(unloadTicks / 20);
        }
        return null;
    }

    public void setUnloadSeconds(int seconds) {
        setUnloadTicks(seconds * 20L);
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();

        //NonNullable
        map.put("subIds", subIds);
        map.put("name", LegacyComponentSerializer.legacyAmpersand().serialize(name));
        map.put("ownerId", ownerId);
        map.put("entityType", entityType);
        map.put("active", active);
        map.put("frozen", frozen);

        // Nullable
        if (homeLocation != null) {
            map.put("homeLocation", homeLocation);
        }
        if (loadLocation != null) {
            map.put("loadLocation", loadLocation);
        }
        if (unloadLocation != null) {
            map.put("unloadLocation", unloadLocation);
        }
        if (loadTicks != null) {
            map.put("loadTicks", loadTicks);
        }
        if (unloadTicks != null) {
            map.put("unloadTicks", unloadTicks);
        }
        return map;
    }

    public void refreshSubIds() {
        refreshSubIds(getBukkitEntity(), true);
    }

    public void refreshSubIds(Entity entity, boolean register) {
        // Clear old data
        subIds.clear();

        // Check passengers
        if (!entity.getPassengers().isEmpty()) {
            for (Entity subEntity : entity.getPassengers()) {
                if (subEntity instanceof InventoryHolder && !(subEntity instanceof Player)) {
                    subIds.add(subEntity.getUniqueId().toString());
                }
            }
        }

        // Check leashed
        for (Entity nearby : entity.getNearbyEntities(4, 4, 4)) {
            if (nearby instanceof Leashable leashed) {
                if (leashed.isLeashed() && leashed.getLeashHolder().getUniqueId().toString().equals(id)) {
                    subIds.add(nearby.getUniqueId().toString());
                }
            }
        }

        if (register) {
            write();
        }
    }

    // -- UTIL -- //

    public void write() {
        Deliveries.data().add(this);
    }
}
