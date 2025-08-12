package me.hqm.privatereserve.delivery.old.ghast;

import me.hqm.privatereserve.delivery.old.DeliveryTaskType;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public enum GhastDeliveryTaskType implements DeliveryTaskType {
    LOAD,
    RISE_FROM_LOAD,
    MOVE_FROM_LOAD,
    DESCEND_FROM_LOAD,
    UNLOAD,
    RISE_FROM_UNLOAD,
    MOVE_FROM_UNLOAD,
    DESCEND_FROM_UNLOAD
}
