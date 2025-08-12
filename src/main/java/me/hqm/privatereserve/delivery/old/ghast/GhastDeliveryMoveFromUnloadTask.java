package me.hqm.privatereserve.delivery.old.ghast;

import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.delivery.data.DeliveryMob;
import me.hqm.privatereserve.delivery.old.MobDeliveryMovementTask;
import me.hqm.privatereserve.delivery.old.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public class GhastDeliveryMoveFromUnloadTask extends MobDeliveryMovementTask {

    public GhastDeliveryMoveFromUnloadTask(DeliveryMob model) {
        super(GhastDeliveryTaskType.MOVE_FROM_UNLOAD, model, model.getCurrentLocation(), Locations.atCloudLevel(model.getLoadLocation()));
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryDescendFromUnloadTask(getMob());
    }
}
