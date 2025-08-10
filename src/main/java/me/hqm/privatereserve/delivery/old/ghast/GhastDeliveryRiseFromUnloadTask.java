package me.hqm.privatereserve.delivery.old.ghast;

import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import me.hqm.privatereserve.delivery.old.MobDeliveryMovementTask;
import me.hqm.privatereserve.delivery.old.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryRiseFromUnloadTask extends MobDeliveryMovementTask {

    public GhastDeliveryRiseFromUnloadTask(DeliveryDocument model) {
        super(GhastDeliveryTaskType.RISE_FROM_UNLOAD, model, model.getCurrentLocation(), Locations.atCloudLevel(model.getUnloadLocation()));
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryMoveFromUnloadTask(getMob());
    }
}
