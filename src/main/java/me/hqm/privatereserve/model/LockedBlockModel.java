package me.hqm.privatereserve.model;

import com.demigodsrpg.util.LocationUtil;
import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.privatereserve.PrivateReserve;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class LockedBlockModel implements Model {

    // -- DATA -- //

    private String location;
    private String owner;
    private boolean locked;

    // -- CONSTRUCTORS -- //

    public LockedBlockModel(String location, String owner) {
        this.location = location;
        this.owner = owner;
        locked = false;
    }

    public LockedBlockModel(Location location, String owner) {
        this.location = LocationUtil.stringFromLocation(location);
        this.owner = owner;
        locked = false;
    }

    public LockedBlockModel(String location, DataSection data) {
        this.location = location;
        owner = data.getString("owner");
        locked = data.getBoolean("locked");
    }

    // -- GETTERS -- //

    public Location getLocation() {
        return LocationUtil.locationFromString(location);
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
        PrivateReserve.LOCKED_R.register(this);
    }
}
