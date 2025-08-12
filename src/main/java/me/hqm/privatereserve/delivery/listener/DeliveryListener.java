package me.hqm.privatereserve.delivery.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.delivery.Deliveries;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class DeliveryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHurt(EntityDamageEvent damageEvent) {
        if (Settings.DELIVERY_MOB_INVINCIBLE.getBoolean()) {
            boolean invincible = Deliveries.data().isInvincible(damageEvent.getEntity());
            if (invincible) {
                damageEvent.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(EntityMoveEvent event) {
        if (Deliveries.data().isFrozen(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onControl(VehicleEnterEvent event) {
        if (Deliveries.data().isActive(event.getVehicle())) {
            event.setCancelled(true);
        }
    }
}
