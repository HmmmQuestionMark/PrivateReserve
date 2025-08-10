package me.hqm.privatereserve.registry.file;

import me.hqm.privatereserve.model.DeliveryModel;
import me.hqm.privatereserve.registry.DeliveryRegistry;

public class FDeliveryRegistry extends AbstractReserveFileRegistry<DeliveryModel> implements DeliveryRegistry {
    public FDeliveryRegistry() {
        super(NAME, 0);
    }
}
