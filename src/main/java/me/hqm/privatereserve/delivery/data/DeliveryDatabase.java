package me.hqm.privatereserve.delivery.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentDatabase;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.delivery.Deliveries;
import me.hqm.privatereserve.delivery.old.data._DeliveryDocument;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;


public interface DeliveryDatabase extends DocumentDatabase<DeliveryDocument> {
    String NAME = "delivery_mobs";
    List<EntityType> TYPES = Collections.singletonList(EntityType.HAPPY_GHAST);

    @Override
    default DeliveryDocument createDocument(String stringKey, Document data) {
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
                filter(deliveryMobModel -> deliveryMobModel.getOwnerId().equals(model.getId())).
                collect(Collectors.toList());
    }

    default int deliveryMobsOwned(MemberDocument model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<DeliveryDocument> fromOwnerName(String owner) {
        List<DeliveryDocument> mobDeliveryModels = new ArrayList<>();
        Optional<MemberDocument> maybe = Members.data().fromId(owner);
        if (maybe.isPresent()) {
            for (DeliveryDocument mobDeliveryModel : getRawData().values()) {
                if (mobDeliveryModel.getOwnerId().equals(maybe.get().getId())) {
                    mobDeliveryModels.add(mobDeliveryModel);
                }
            }
        }
        return mobDeliveryModels;
    }

    default Map<String, Integer> deliveryMobOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (DeliveryDocument mobDeliveryModel : getRawData().values()) {
            Optional<MemberDocument> maybe = Members.data().fromId(mobDeliveryModel.getOwnerId());
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
            Optional<_DeliveryDocument> maybe = Deliveries.___data().fromId(mob.getId());
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
