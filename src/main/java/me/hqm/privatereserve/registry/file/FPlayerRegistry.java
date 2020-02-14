package me.hqm.privatereserve.registry.file;

import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.registry.PlayerRegistry;

public class FPlayerRegistry extends AbstractChatFileRegistry<PlayerModel> implements PlayerRegistry {
    public FPlayerRegistry() {
        super(NAME, 0);
    }
}
