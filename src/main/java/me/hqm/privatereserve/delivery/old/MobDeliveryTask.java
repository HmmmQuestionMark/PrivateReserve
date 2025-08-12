package me.hqm.privatereserve.delivery.old;

import me.hqm.privatereserve.delivery.Deliveries;
import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import me.hqm.privatereserve.delivery.old.data._DeliveryDocument;
import me.hqm.privatereserve.delivery.old.ghast.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public abstract class MobDeliveryTask extends BukkitRunnable {
    final DeliveryTaskType type;
    final String mobId;
    final OfflinePlayer owner;

    protected MobDeliveryTask(DeliveryTaskType type, DeliveryDocument model) {
        this.type = type;
        this.mobId = model.getId();
        this.owner = Bukkit.getOfflinePlayer(UUID.fromString(model.getOwnerId()));
        new _DeliveryDocument(this);
    }

    public static MobDeliveryTask createFromData(DeliveryTaskType type, String mobId) {
        Optional<DeliveryDocument> maybe = Deliveries.data().fromId(mobId);
        if (maybe.isPresent()) {
            DeliveryDocument mob = maybe.get();
            if (type instanceof GhastDeliveryTaskType ghastTask) {
                return switch (ghastTask) {
                    case LOAD -> new GhastDeliveryLoadTask(mob);
                    case RISE_FROM_LOAD -> new GhastDeliveryRiseFromLoadTask(mob);
                    case MOVE_FROM_LOAD -> new GhastDeliveryMoveFromLoadTask(mob);
                    case DESCEND_FROM_LOAD -> new GhastDeliveryDescendFromLoadTask(mob);
                    case UNLOAD -> new GhastDeliveryUnloadTask(mob);
                    case RISE_FROM_UNLOAD -> new GhastDeliveryRiseFromUnloadTask(mob);
                    case MOVE_FROM_UNLOAD -> new GhastDeliveryMoveFromUnloadTask(mob);
                    case DESCEND_FROM_UNLOAD -> new GhastDeliveryDescendFromUnloadTask(mob);
                };
            }

            // If all else fails, return home
            return new MobDeliveryReturnHomeTask(mob);
        }

        // The mob isn't present, so do nothing
        return null;
    }

    public abstract void start();

    public abstract void stop(boolean last);

    public abstract void abort(boolean warn);

    public abstract MobDeliveryTask nextTask();

    public DeliveryTaskType getType() {
        return type;
    }

    public String getMobId() {
        return mobId;
    }

    public EntityType getEntityType() {
        return getMob().getEntityType();
    }

    protected DeliveryDocument getMob() {
        return Deliveries.data().fromId(mobId).get();
    }

    protected _DeliveryDocument getDelivery() {
        return Deliveries.___data().fromId(mobId).get();
    }

    public abstract Map<String, Object> serialize();
}
