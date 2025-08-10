package me.hqm.privatereserve.task.deliverymob.ghast;

import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryLoadUnloadTask;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryLoadTask extends MobDeliveryLoadUnloadTask {
    public GhastDeliveryLoadTask(MobDeliveryModel model) {
        super(GhastDeliveryTaskType.LOAD, model);
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryRiseFromLoadTask(getMob());
    }
}
