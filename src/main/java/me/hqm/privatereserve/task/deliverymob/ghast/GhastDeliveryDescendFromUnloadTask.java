package me.hqm.privatereserve.task.deliverymob.ghast;

import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryMovementTask;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryDescendFromUnloadTask extends MobDeliveryMovementTask {

    public GhastDeliveryDescendFromUnloadTask(MobDeliveryModel model) {
        super(GhastDeliveryTaskType.DESCEND_FROM_UNLOAD, model, model.getCurrentLocation(), model.getLoadLocation());
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryLoadTask(getMob());
    }
}
