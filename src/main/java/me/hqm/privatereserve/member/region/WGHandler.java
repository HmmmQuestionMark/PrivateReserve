package me.hqm.privatereserve.member.region;

import com.google.common.collect.Iterators;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Custom flags will not require reflection in WorldGuard 6+, until then we'll use it.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class WGHandler {

    static WGPVPListener WG_PVP_LISTENER;

    private WGHandler() {
    }

    /**
     * @param id The name of a WorldGuard flag.
     * @deprecated If you don't have WorldGuard installed this will error.
     */
    @Deprecated
    static Flag<?> getFlag(String id) {
        return Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), id);
    }

    /**
     * Check that a ProtectedRegion exists at a Location.
     *
     * @param name     The name of the region.
     * @param location The location being checked.
     * @return The region does exist at the provided location.
     */
    static boolean checkForRegion(final String name, Location location) {
        return Iterators
                .any(Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer()
                                .get(BukkitAdapter.adapt(location.getWorld())))
                        .getApplicableRegions(BukkitAdapter.asBlockVector(location))
                        .iterator(), region -> Objects.requireNonNull(region).getId().toLowerCase().contains(name));
    }

    /**
     * Get a region in a given world based on name.
     *
     * @param name  The name of the region.
     * @param world The world containing the region.
     * @return The region object.
     */
    static ProtectedRegion getRegion(final String name, World world) throws NullPointerException {
        return Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer()
                        .get(BukkitAdapter.adapt(world)))
                .getRegion(name);
    }

    /**
     * Check for a flag at a given location.
     *
     * @param flag     The flag being checked.
     * @param location The location being checked.
     * @return The flag does exist at the provided location.
     */
    static boolean checkForFlag(final Flag flag, Location location) {
        return Iterators
                .any(Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer()
                                .get(BukkitAdapter.adapt(location.getWorld())))
                        .getApplicableRegions(BukkitAdapter.asBlockVector(location))
                        .iterator(), region -> {
                    try {
                        return Objects.requireNonNull(region).getFlags().containsKey(flag);
                    } catch (Exception ignored) {
                    }
                    return false;
                });
    }

    /**
     * Check if a StateFlag is enabled at a given location.
     *
     * @param flag     The flag being checked.
     * @param location The location being checked.
     * @return The flag is enabled.
     */
    static boolean checkStateFlagAllows(final StateFlag flag, Location location, RegionAssociable associable) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(location), associable, flag);
    }

    /**
     * Check if a StateFlag is enabled at a given location.
     *
     * @param flag     The flag being checked.
     * @param location The location being checked.
     * @return The flag is enabled.
     */
    static boolean checkStateFlagAllows(final StateFlag flag, Location location, Player player) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), flag);
    }

    /**
     * Check for a flag-value at a given location.
     *
     * @param flag     The flag being checked.
     * @param value    The value (marshalled) as a String.
     * @param location The location being checked.
     * @return The flag-value does exist at the provided location.
     */
    static boolean checkForFlagValue(final Flag flag, final String value, Location location) {
        return Iterators
                .any(Objects.requireNonNull(WorldGuard.getInstance().getPlatform().getRegionContainer()
                                .get(BukkitAdapter.adapt(location.getWorld())))
                        .getApplicableRegions(BukkitAdapter.asBlockVector(location))
                        .iterator(), region -> {
                    try {
                        return flag.marshal(Objects.requireNonNull(region).getFlag(flag)).equals(value);
                    } catch (Exception ignored) {
                    }
                    return false;
                });
    }

    /**
     * @param player   Given player.
     * @param location Given location.
     * @return The player can build here.
     */
    static boolean canBuild(Player player, Location location) {
        return checkStateFlagAllows(Flags.BUILD, location, player);
    }

    /**
     * @param location Given location.
     * @return PVP is allowed here.
     */
    static boolean canPVP(Player player, Location location) {
        return checkStateFlagAllows(Flags.PVP, location, player);
    }

    static void setWhenToOverridePVP(JavaPlugin plugin, Predicate<Event> checkPVP) {
        if (plugin.isEnabled()) {
            if (WG_PVP_LISTENER != null) {
                HandlerList.unregisterAll(WG_PVP_LISTENER);
            }
            WG_PVP_LISTENER = new WGPVPListener(plugin, checkPVP);
            new WGPVPRegistrationTask(plugin).runTaskLaterAsynchronously(plugin, 40);
        }
    }

    static Location spawnLocation(String regionName, World world, Location defaultLocation) {
        return spawnLocation(getRegion(regionName, world), world, defaultLocation);
    }

    static Location spawnLocation(ProtectedRegion region, World world, Location defaultLocation) {
        com.sk89q.worldedit.util.Location location = region.getFlag(Flags.SPAWN_LOC);
        if (location != null) {
            float sY = location.getYaw();
            float sP = location.getPitch();
            return new Location(world, location.getX(), location.getY(), location.getZ(), sY, sP);
        }
        return defaultLocation;
    }

    static class WGPVPRegistrationTask extends BukkitRunnable {
        JavaPlugin plugin;

        WGPVPRegistrationTask(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            plugin.getServer().getPluginManager().registerEvents(WG_PVP_LISTENER, plugin);
        }
    }

    static class WGPVPListener implements Listener {
        private final Predicate<Event> checkPVP;

        WGPVPListener(Plugin plugin, Predicate<Event> checkPVP) {
            this.checkPVP = checkPVP;
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        void onDisallowedPVP(DisallowedPVPEvent event) {
            if (checkPVP.test(event.getCause())) event.setCancelled(true);
        }
    }
}
