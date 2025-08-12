package me.hqm.privatereserve.delivery.old;

import me.hqm.privatereserve.delivery.old.ghast.GhastDeliveryTaskType;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public interface DeliveryTaskType {

    static DeliveryTaskType valueOf(EntityType entityType, String name) {
        if (name.equals(GeneralDeliveryTaskType.RETURN_HOME.name())) {
            return GeneralDeliveryTaskType.RETURN_HOME;
        }
        if (EntityType.HAPPY_GHAST == entityType) {
            return GhastDeliveryTaskType.valueOf(name);
        }
        return GeneralDeliveryTaskType.valueOf(name);
    }

    String name();
}
