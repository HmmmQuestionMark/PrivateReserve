package me.hqm.privatereserve.landmark.data;

import me.hqm.privatereserve.Locations;
import me.hqm.document.DocumentMap;
import me.hqm.document.Document;
import me.hqm.privatereserve._PrivateReserve;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LandmarkDocument implements Document {

    // -- DATA -- //

    private final String name;
    private String location;
    private final String owner;
    private long timeCreated;

    // -- CONSTRUCTORS -- //

    public LandmarkDocument(String name, Location location, String owner) {
        this.name = name;
        this.location = Locations.stringFromLocation(location);
        this.owner = owner;
        this.timeCreated = System.currentTimeMillis();
        register();
    }

    public LandmarkDocument(String name, DocumentMap data) {
        this.name = name;
        this.location = data.getString("location");
        this.owner = data.getString("owner");
        this.timeCreated = data.getLong("timeCreated", System.currentTimeMillis());
    }

    // -- GETTERS -- //

    public String getName() {
        return getKey();
    }

    public Location getLocation() {
        return Locations.locationFromString(location);
    }

    public boolean isOwnerOrAdmin(Player player) {
        return owner.equals(player.getUniqueId().toString()) || player.hasPermission("privatereserve.admin");
    }

    public String getOwner() {
        return owner;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("owner", owner);
        map.put("timeCreated", timeCreated);
        return map;
    }

    // -- MUTATORS -- //

    public void setLocation(Location location) {
        this.location = Locations.stringFromLocation(location);
        this.timeCreated = System.currentTimeMillis();
        register();
    }

    // -- UTIL -- //

    @Override
    public void register() {
        _PrivateReserve.LANDMARK_DATA.register(this);
    }
}
