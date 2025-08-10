package me.hqm.privatereserve.delivery.old.ghast;

import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import me.hqm.privatereserve.delivery.old.MobDeliveryMovementTask;
import me.hqm.privatereserve.delivery.old.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryDescendFromUnloadTask extends MobDeliveryMovementTask {

    public GhastDeliveryDescendFromUnloadTask(DeliveryDocument model) {
        super(GhastDeliveryTaskType.DESCEND_FROM_UNLOAD, model, model.getCurrentLocation(), model.getLoadLocation());
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryLoadTask(getMob());
    }
}
