package me.hqm.privatereserve.registry.file;

import me.hqm.privatereserve.model.LandmarkModel;
import me.hqm.privatereserve.registry.LandmarkRegistry;

public class FLandmarkRegistry extends AbstractReserveFileRegistry<LandmarkModel> implements LandmarkRegistry {
    public FLandmarkRegistry() {
        super(NAME, 0);
    }
}
