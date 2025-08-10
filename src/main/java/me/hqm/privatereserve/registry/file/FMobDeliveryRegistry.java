package me.hqm.privatereserve.registry.file;

import me.hqm.privatereserve.model.MobDeliveryModel;
import me.hqm.privatereserve.registry.MobDeliveryRegistry;

public class FMobDeliveryRegistry extends AbstractReserveFileRegistry<MobDeliveryModel> implements MobDeliveryRegistry {
    public FMobDeliveryRegistry() {
        super(NAME, 0);
    }
}
