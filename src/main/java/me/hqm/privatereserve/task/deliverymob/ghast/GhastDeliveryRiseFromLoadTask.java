package me.hqm.privatereserve.task.deliverymob.ghast;

import com.demigodsrpg.util.LocationUtil;
import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryMovementTask;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryRiseFromLoadTask extends MobDeliveryMovementTask {

    public GhastDeliveryRiseFromLoadTask(MobDeliveryModel model) {
        super(GhastDeliveryTaskType.RISE_FROM_LOAD, model, model.getCurrentLocation(), LocationUtil.atCloudLevel(model.getLoadLocation()));
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryMoveFromLoadTask(getMob());
    }
}
