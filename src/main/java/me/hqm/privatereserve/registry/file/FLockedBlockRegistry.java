package me.hqm.privatereserve.registry.file;

import me.hqm.privatereserve.model.LockedBlockModel;
import me.hqm.privatereserve.registry.LockedBlockRegistry;

public class FLockedBlockRegistry extends AbstractReserveFileRegistry<LockedBlockModel> implements LockedBlockRegistry {
    public FLockedBlockRegistry() {
        super(NAME, 0);
    }
}
