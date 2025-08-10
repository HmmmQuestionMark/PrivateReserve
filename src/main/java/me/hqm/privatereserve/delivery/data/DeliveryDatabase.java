package me.hqm.privatereserve.delivery.data;

import me.hqm.document.DocumentMap;
import me.hqm.document.Database;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.delivery.old.data._DeliveryDocument;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;


public interface DeliveryDatabase extends Database<DeliveryDocument> {
    String NAME = "delivery_mobs";
    List<EntityType> TYPES = Collections.singletonList(EntityType.HAPPY_GHAST);

    @Override
    default DeliveryDocument fromDataSection(String stringKey, DocumentMap data) {
        return new DeliveryDocument(stringKey, data);
    }

    default List<DeliveryDocument> fromName(String name) {
        return getRawData().values().stream().
                filter(deliveryMob ->
                        PlainTextComponentSerializer.plainText().
                                serialize(deliveryMob.getName()).equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    default Optional<DeliveryDocument> fromEntity(Entity entity) {
        return getRawData().values().stream().
                filter(deliveryMob -> deliveryMob.getId().equals(entity.getUniqueId().toString())).
                findAny();
    }

    default List<DeliveryDocument> fromOwner(MemberDocument model) {
        return getRawData().values().stream().
                filter(deliveryMobModel -> deliveryMobModel.getOwnerId().equals(model.getKey())).
                collect(Collectors.toList());
    }

    default int deliveryMobsOwned(MemberDocument model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<DeliveryDocument> fromOwnerName(String owner) {
        List<DeliveryDocument> mobDeliveryModels = new ArrayList<>();
        Optional<MemberDocument> maybe = _PrivateReserve.MEMBER_DATA.fromId(owner);
        if (maybe.isPresent()) {
            for (DeliveryDocument mobDeliveryModel : getRawData().values()) {
                if (mobDeliveryModel.getOwnerId().equals(maybe.get().getKey())) {
                    mobDeliveryModels.add(mobDeliveryModel);
                }
            }
        }
        return mobDeliveryModels;
    }

    default Map<String, Integer> deliveryMobOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (DeliveryDocument mobDeliveryModel : getRawData().values()) {
            Optional<MemberDocument> maybe = _PrivateReserve.MEMBER_DATA.fromId(mobDeliveryModel.getOwnerId());
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
        if (Settings.DELIVERY_MOB_INVINCIBLE.getBoolean()) {
            return getRawData().values().stream().anyMatch(deliveryMob -> deliveryMob.isEntityOrSub(entity));
        }
        return false;
    }

    default boolean isFrozen(Entity entity) {
        return getRawData().values().stream().anyMatch(deliveryMob -> deliveryMob.isEntityOrSubFrozen(entity));
    }

    default void cancelAll() {
        for (DeliveryDocument mob : getRawData().values()) {
            Optional<_DeliveryDocument> maybe = _PrivateReserve._DELIVERY_DATA.fromKey(mob.getKey());
            if (maybe.isPresent()) {
                _DeliveryDocument delivery = maybe.get();
                delivery.clear();
                mob.setActive(false);
            }
        }
    }

    default List<DeliveryDocument> fromType(EntityType type) {
        List<DeliveryDocument> mobDeliveryModels = new ArrayList<>();
        if (TYPES.contains(type)) {
            for (DeliveryDocument mobDeliveryModel : getRawData().values()) {
                if (type.equals(mobDeliveryModel.getEntityType())) {
                    mobDeliveryModels.add(mobDeliveryModel);
                }
            }
        }
        return mobDeliveryModels;
    }
}
