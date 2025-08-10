package me.hqm.privatereserve.delivery.old;

import me.hqm.privatereserve.delivery.old.ghast.GhastDeliveryTaskType;
import org.bukkit.entity.EntityType;

public interface DeliveryTaskType {

    String name();

    static DeliveryTaskType valueOf(EntityType entityType, String name) {
        if (name.equals(GeneralDeliveryTaskType.RETURN_HOME.name())) {
            return GeneralDeliveryTaskType.RETURN_HOME;
        }
        if (EntityType.HAPPY_GHAST == entityType) {
            return GhastDeliveryTaskType.valueOf(name);
        }
        return GeneralDeliveryTaskType.valueOf(name);
    }
}
