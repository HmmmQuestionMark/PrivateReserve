package me.hqm.privatereserve.task.deliverymob;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.DeliveryModel;
import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.task.deliverymob.ghast.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class MobDeliveryTask extends BukkitRunnable {
    DeliveryTaskType type;
    String mobId;
    OfflinePlayer owner;

    protected MobDeliveryTask(DeliveryTaskType type, MobDeliveryModel model) {
        this.type = type;
        this.mobId = model.getId();
        this.owner = Bukkit.getOfflinePlayer(UUID.fromString(model.getOwnerId()));
        new DeliveryModel(this);
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

    protected MobDeliveryModel getMob() {
        return PrivateReserve.MOB_DELIVERY_R.fromKey(mobId).get();
    }

    protected DeliveryModel getDelivery() {
        return PrivateReserve.DELIVERY_R.fromKey(mobId).get();
    }

    public abstract Map<String, Object> serialize();

    public static MobDeliveryTask createFromData(DeliveryTaskType type, String mobId) {
        Optional<MobDeliveryModel> maybe = PrivateReserve.MOB_DELIVERY_R.fromKey(mobId);
        if (maybe.isPresent()) {
            MobDeliveryModel mob = maybe.get();
            if (type instanceof GhastDeliveryTaskType) {
                return switch ((GhastDeliveryTaskType) type) {
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
}
