package me.hqm.privatereserve.model;

import com.demigodsrpg.util.LocationUtil;
import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.privatereserve.PrivateReserve;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LandmarkModel implements Model {

    // -- DATA -- //

    private String name;
    private String location;
    private String owner;
    private long timeCreated;

    // -- CONSTRUCTORS -- //

    public LandmarkModel(String name, Location location, String owner) {
        this.name = name;
        this.location = LocationUtil.stringFromLocation(location);
        this.owner = owner;
        this.timeCreated = System.currentTimeMillis();
        register();
    }

    public LandmarkModel(String name, DataSection data) {
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
        return LocationUtil.locationFromString(location);
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
        return map;
    }

    // -- MUTATORS -- //

    public void setLocation(Location location) {
        this.location = LocationUtil.stringFromLocation(location);
        this.timeCreated = System.currentTimeMillis();
        register();
    }

    // -- UTIL -- //

    @Override
    public void register() {
        PrivateReserve.LANDMARK_R.register(this);
    }
}
