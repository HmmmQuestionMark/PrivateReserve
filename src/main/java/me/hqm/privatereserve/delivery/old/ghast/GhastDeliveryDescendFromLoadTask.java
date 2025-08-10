package me.hqm.privatereserve.delivery.old.ghast;

import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import me.hqm.privatereserve.delivery.old.MobDeliveryMovementTask;
import me.hqm.privatereserve.delivery.old.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryDescendFromLoadTask extends MobDeliveryMovementTask {

    public GhastDeliveryDescendFromLoadTask(DeliveryDocument model) {
        super(GhastDeliveryTaskType.DESCEND_FROM_LOAD, model, model.getCurrentLocation(), model.getUnloadLocation());
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryUnloadTask(getMob());
    }
}
