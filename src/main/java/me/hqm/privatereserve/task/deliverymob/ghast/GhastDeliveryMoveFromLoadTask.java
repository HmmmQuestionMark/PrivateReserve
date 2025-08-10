package me.hqm.privatereserve.task.deliverymob.ghast;

import com.demigodsrpg.util.LocationUtil;
import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryMovementTask;
import me.hqm.privatereserve.task.deliverymob.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class GhastDeliveryMoveFromLoadTask extends MobDeliveryMovementTask {

    public GhastDeliveryMoveFromLoadTask(MobDeliveryModel model) {
        super(GhastDeliveryTaskType.MOVE_FROM_LOAD, model, model.getCurrentLocation(), LocationUtil.atCloudLevel(model.getUnloadLocation()));
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryDescendFromLoadTask(getMob());
    }
}
