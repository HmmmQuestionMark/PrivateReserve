package me.hqm.reservechat.registry.file;

import me.hqm.reservechat.model.PlayerModel;
import me.hqm.reservechat.registry.PlayerRegistry;

public class FPlayerRegistry extends AbstractChatFileRegistry<PlayerModel> implements PlayerRegistry {
    public FPlayerRegistry() {
        super(NAME, 0);
    }
}
