package me.hqm.privatereserve.landmark.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.landmark.Landmarks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class Landmark implements DocumentCompatible {

    // -- DATA -- //

    private final String name;
    private final String owner;
    private String location;
    private long timeCreated;

    // -- CONSTRUCTORS -- //

    public Landmark(String name, Location location, String owner) {
        this.name = name;
        this.location = Locations.stringFromLocation(location);
        this.owner = owner;
        this.timeCreated = System.currentTimeMillis();
        write();
    }

    public Landmark(String name, Document data) {
        this.name = name;
        this.location = data.get("location", PersistentDataType.STRING);
        this.owner = data.get("owner", PersistentDataType.STRING);
        this.timeCreated = data.getOrDefault("timeCreated", PersistentDataType.LONG, System.currentTimeMillis());
    }

    // -- GETTERS -- //

    public String getName() {
        return getId();
    }

    public Location getLocation() {
        return Locations.locationFromString(location);
    }

    public void setLocation(Location location) {
        this.location = Locations.stringFromLocation(location);
        this.timeCreated = System.currentTimeMillis();
        write();
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
    public String getId() {
        return name;
    }

    // -- MUTATORS -- //

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("owner", owner);
        map.put("timeCreated", timeCreated);
        return map;
    }

    // -- UTIL -- //

    public void write() {
        Landmarks.data().add(this);
    }
}
