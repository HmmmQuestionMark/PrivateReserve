package me.hqm.reservechat.registry.file;

import com.demigodsrpg.util.datasection.AbstractFileRegistry;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.reservechat.ReserveChat;
import me.hqm.reservechat.Setting;

public abstract class AbstractChatFileRegistry<T extends Model> extends AbstractFileRegistry<T> {
    public AbstractChatFileRegistry(String name, int expireInMins) {
        super(ReserveChat.SAVE_PATH, name, Setting.SAVE_PRETTY, expireInMins);
    }
}

