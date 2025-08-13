package me.hqm.privatereserve.task;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {
    Player player;
    Location to;
    boolean async;

    public TeleportTask(Player player, Location to, boolean async) {
        this.player = player;
        this.to = to;
        this.async = async;
    }

    @Override
    public void run() {
        if(player.isInsideVehicle() && player.getVehicle() instanceof HappyGhast ghast) {
            if(async) {
                ghast.teleportAsync(to.add(0.0, 4.0, 0.0),
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        TeleportFlag.EntityState.RETAIN_PASSENGERS
                );
            } else {
                ghast.teleport(to.add(0.0, 4.0, 0.0),
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        TeleportFlag.EntityState.RETAIN_PASSENGERS
                );
            }
        } else {
            if(async) {
                player.teleportAsync(
                        to,
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        TeleportFlag.EntityState.RETAIN_VEHICLE
                );
            } else {
                player.teleport(
                        to,
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        TeleportFlag.EntityState.RETAIN_VEHICLE
                );
            }
        }
    }
}