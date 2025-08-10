package me.hqm.privatereserve.delivery.data;

import me.hqm.privatereserve.Locations;
import me.hqm.document.DocumentMap;
import me.hqm.document.Document;
import io.papermc.paper.entity.Leashable;
import me.hqm.privatereserve._PrivateReserve;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

public class DeliveryDocument implements Document {
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

        register();
    }

    public DeliveryDocument(String id, DocumentMap data) {
        this.id = id;
        subIds = data.getStringList("subIds");
        name = LegacyComponentSerializer.legacyAmpersand().deserialize(data.getString("name"));
        ownerId = data.getString("ownerId");
        entityType = data.getString("entityType");
        active = data.getBoolean("active", false);
        frozen = data.getBoolean("frozen", false);
        homeLocation = data.getStringNullable("homeLocation");
        loadLocation = data.getStringNullable("loadLocation");
        unloadLocation = data.getStringNullable("unloadLocation");
        loadTicks = data.getLongNullable("loadTicks");
        unloadTicks = data.getLongNullable("unloadTicks");
    }

    // -- GETTERS -- //


    public String getId() {
        return getKey();
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

    public boolean isFrozen() {
        return frozen;
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

    public Location getLoadLocation() {
        return Locations.locationFromString(loadLocation);
    }

    public Location getUnloadLocation() {
        return Locations.locationFromString(unloadLocation);
    }

    public Long getLoadTicks() {
        return loadTicks;
    }

    public Integer getLoadSeconds() {
        if (loadTicks != null) {
            return Math.toIntExact(loadTicks / 20);
        }
        return null;
    }

    public Long getUnloadTicks() {
        return unloadTicks;
    }

    public Integer getUnloadSeconds() {
        if (unloadTicks != null) {
            return Math.toIntExact(unloadTicks / 20);
        }
        return null;
    }

    @Override
    public String getKey() {
        return id;
    }

    @Override
    public Map<String, Object> serialize() {
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

    // -- MUTATORS -- //

    public void setActive(boolean active) {
        this.active = active;
        register();
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        register();
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = Locations.stringFromLocation(homeLocation);
        register();
    }

    public void setLoadLocation(Location loadLocation) {
        this.loadLocation = Locations.stringFromLocation(loadLocation);
        register();
    }

    public void setUnloadLocation(Location unloadLocation) {
        this.unloadLocation = Locations.stringFromLocation(unloadLocation);
        register();
    }

    public void setLoadTicks(long loadTicks) {
        this.loadTicks = loadTicks;
        register();
    }

    public void setLoadSeconds(int seconds) {
        setLoadTicks(seconds * 20L);
    }

    public void setUnloadTicks(long unloadTicks) {
        this.unloadTicks = unloadTicks;
        register();
    }

    public void setUnloadSeconds(int seconds) {
        setUnloadTicks(seconds * 20L);
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
            register();
        }
    }


    // -- UTIL -- //

    @Override
    public void register() {
        _PrivateReserve.DELIVERY_DATA.register(this);
    }
}
