package me.hqm.privatereserve.registry.file;

import com.demigodsrpg.util.datasection.AbstractFileRegistry;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.privatereserve.ReserveChat;
import me.hqm.privatereserve.Setting;

public abstract class AbstractChatFileRegistry<T extends Model> extends AbstractFileRegistry<T> {
    public AbstractChatFileRegistry(String name, int expireInMins) {
        super(ReserveChat.SAVE_PATH, name, Setting.SAVE_PRETTY, expireInMins);
    }
}

