package me.hqm.privatereserve.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Setting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class DeliveryMobListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHurt(EntityDamageEvent damageEvent) {
        if (Setting.DELIVERY_MOB_INVINCIBLE.getBoolean()) {
            boolean invincible = PrivateReserve.MOB_DELIVERY_R.isInvincible(damageEvent.getEntity());
            if (invincible) {
                damageEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(EntityMoveEvent event) {
        if (PrivateReserve.MOB_DELIVERY_R.isFrozen(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onControl(VehicleEnterEvent event) {
        if (PrivateReserve.MOB_DELIVERY_R.isActive(event.getVehicle())) {
            event.setCancelled(true);
        }
    }
}
