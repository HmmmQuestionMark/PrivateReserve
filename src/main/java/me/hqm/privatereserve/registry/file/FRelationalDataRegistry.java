package me.hqm.privatereserve.registry.file;

import me.hqm.privatereserve.model.RelationalDataModel;
import me.hqm.privatereserve.registry.RelationalDataRegistry;

public class FRelationalDataRegistry extends AbstractReserveFileRegistry<RelationalDataModel> implements
        RelationalDataRegistry {
    public FRelationalDataRegistry() {
        super(NAME, 0);
    }
}
