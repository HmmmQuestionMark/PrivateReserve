package me.hqm.privatereserve.task;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {
    Entity entity;
    Location to;
    boolean async;
    PlayerTeleportEvent.TeleportCause cause;
    TeleportFlag flag;

    public TeleportTask(Entity entity, Location to, boolean async, PlayerTeleportEvent.TeleportCause cause) {
        this.entity = entity;
        this.to = to;
        this.async = async;
        this.cause = cause;
    }

    @Override
    public void run() {
        if(entity.isInsideVehicle()) {
            entity = entity.getVehicle();
            flag = TeleportFlag.EntityState.RETAIN_PASSENGERS;
            if (entity instanceof HappyGhast) {
                to = to.add(0.0, 4.0, 0.0);
            } else {
                to = to.add(0.0, 1.0, 0.0);
            }
            teleport();
        } else {
            flag = TeleportFlag.EntityState.RETAIN_VEHICLE;
            teleport();
        }
    }

    private void teleport() {
        if (async) {
            entity.teleportAsync(to,
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    flag
            );
        } else {
            entity.teleport(to,
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    flag
            );
        }
    }

}