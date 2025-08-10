package me.hqm.privatereserve.lockedblock;

import me.hqm.privatereserve.Locations;
import me.hqm.document.DocumentMap;
import me.hqm.document.Document;
import me.hqm.privatereserve._PrivateReserve;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class LockedBlockDocument implements Document {

    // -- DATA -- //

    private final String location;
    private final String owner;
    private boolean locked;

    // -- CONSTRUCTORS -- //

    public LockedBlockDocument(String location, String owner) {
        this.location = location;
        this.owner = owner;
        locked = false;
    }

    public LockedBlockDocument(Location location, String owner) {
        this.location = Locations.stringFromLocation(location);
        this.owner = owner;
        locked = false;
    }

    public LockedBlockDocument(String location, DocumentMap data) {
        this.location = location;
        owner = data.getString("owner");
        locked = data.getBoolean("locked");
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
    public String getKey() {
        return location;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("owner", owner);
        map.put("locked", locked);
        return map;
    }

    // -- MUTATORS -- //

    public boolean setLocked(boolean locked) {
        this.locked = locked;
        register();
        return locked;
    }

    // -- UTIL -- //

    @Override
    public void register() {
        _PrivateReserve.LOCKED_DATA.register(this);
    }
}
