package me.hqm.privatereserve.registry;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Setting;
import me.hqm.privatereserve.model.DeliveryModel;
import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.model.PlayerModel;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;


public interface MobDeliveryRegistry extends Registry<MobDeliveryModel> {
    String NAME = "delivery_mobs";
    List<EntityType> TYPES = Collections.singletonList(EntityType.HAPPY_GHAST);

    @Override
    default MobDeliveryModel fromDataSection(String stringKey, DataSection data) {
        return new MobDeliveryModel(stringKey, data);
    }

    default List<MobDeliveryModel> fromName(String name) {
        return getRegisteredData().values().stream().
                filter(deliveryMob ->
                        PlainTextComponentSerializer.plainText().
                                serialize(deliveryMob.getName()).equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    default Optional<MobDeliveryModel> fromEntity(Entity entity) {
        return getRegisteredData().values().stream().
                filter(deliveryMob -> deliveryMob.getId().equals(entity.getUniqueId().toString())).
                findAny();
    }

    default List<MobDeliveryModel> fromOwner(PlayerModel model) {
        return getRegisteredData().values().stream().
                filter(deliveryMobModel -> deliveryMobModel.getOwnerId().equals(model.getKey())).
                collect(Collectors.toList());
    }

    default int deliveryMobsOwned(PlayerModel model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<MobDeliveryModel> fromOwnerName(String owner) {
        List<MobDeliveryModel> mobDeliveryModels = new ArrayList<>();
        Optional<PlayerModel> maybe = PrivateReserve.PLAYER_R.fromId(owner);
        if (maybe.isPresent()) {
            for (MobDeliveryModel mobDeliveryModel : getRegisteredData().values()) {
                if (mobDeliveryModel.getOwnerId().equals(maybe.get().getKey())) {
                    mobDeliveryModels.add(mobDeliveryModel);
                }
            }
        }
        return mobDeliveryModels;
    }

    default Map<String, Integer> deliveryMobOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (MobDeliveryModel mobDeliveryModel : getRegisteredData().values()) {
            Optional<PlayerModel> maybe = PrivateReserve.PLAYER_R.fromId(mobDeliveryModel.getOwnerId());
            if (maybe.isPresent()) {
                String lastKnownName = maybe.get().getLastKnownName();
                nameAndCount.merge(lastKnownName, 1, Integer::sum);
            }
        }
        return nameAndCount;
    }

    default boolean isActive(Entity entity) {
        return fromEntity(entity).isPresent() && fromEntity(entity).get().isActive();
    }

    default boolean isInvincible(Entity entity) {
        if (Setting.DELIVERY_MOB_INVINCIBLE.getBoolean()) {
            return getRegisteredData().values().stream().anyMatch(deliveryMob -> deliveryMob.isEntityOrSub(entity));
        }
        return false;
    }

    default boolean isFrozen(Entity entity) {
        return getRegisteredData().values().stream().anyMatch(deliveryMob -> deliveryMob.isEntityOrSubFrozen(entity));
    }

    default void cancelAll() {
        for (MobDeliveryModel mob : getRegisteredData().values()) {
            Optional<DeliveryModel> maybe = PrivateReserve.DELIVERY_R.fromKey(mob.getKey());
            if (maybe.isPresent()) {
                DeliveryModel delivery = maybe.get();
                delivery.clear();
                mob.setActive(false);
            }
        }
    }

    default List<MobDeliveryModel> fromType(EntityType type) {
        List<MobDeliveryModel> mobDeliveryModels = new ArrayList<>();
        if (TYPES.contains(type)) {
            for (MobDeliveryModel mobDeliveryModel : getRegisteredData().values()) {
                if (type.equals(mobDeliveryModel.getEntityType())) {
                    mobDeliveryModels.add(mobDeliveryModel);
                }
            }
        }
        return mobDeliveryModels;
    }
}
