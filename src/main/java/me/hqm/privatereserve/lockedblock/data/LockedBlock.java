package me.hqm.privatereserve.lockedblock.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.lockedblock.LockedBlocks;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class LockedBlock implements DocumentCompatible {

    // -- DATA -- //

    private final String location;
    private final String owner;
    private Boolean locked;

    // -- CONSTRUCTORS -- //

    public LockedBlock(String location, String owner) {
        this.location = location;
        this.owner = owner;
        locked = false;
    }

    public LockedBlock(Location location, String owner) {
        this.location = Locations.stringFromLocation(location);
        this.owner = owner;
        locked = false;
    }

    public LockedBlock(String location, Document data) {
        this.location = location;
        owner = data.get("owner", PersistentDataType.STRING);
        locked = data.get("locked", PersistentDataType.BOOLEAN);
    }

    // -- GETTERS -- //

    public Location getLocation() {
        return Locations.locationFromString(location);
    }

    public String getOwner() {
        return owner;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public String getId() {
        return location;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("owner", owner);
        map.put("locked", locked);
        return map;
    }

    // -- MUTATORS -- //

    public boolean setLocked(boolean locked) {
        this.locked = locked;
        write();
        return locked;
    }

    // -- UTIL -- //

    public void write() {
        LockedBlocks.data().add(this);
    }
}
