package me.hqm.privatereserve.delivery.old.ghast;

import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import me.hqm.privatereserve.delivery.old.MobDeliveryLoadUnloadTask;
import me.hqm.privatereserve.delivery.old.MobDeliveryTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public class GhastDeliveryLoadTask extends MobDeliveryLoadUnloadTask {
    public GhastDeliveryLoadTask(DeliveryDocument model) {
        super(GhastDeliveryTaskType.LOAD, model);
    }

    @Override
    public MobDeliveryTask nextTask() {
        Bukkit.getServer().broadcast(Component.text(getType().name()));
        return new GhastDeliveryRiseFromLoadTask(getMob());
    }
}
