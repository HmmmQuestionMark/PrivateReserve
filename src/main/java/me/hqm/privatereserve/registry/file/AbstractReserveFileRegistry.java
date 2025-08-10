package me.hqm.privatereserve.registry.file;

import com.demigodsrpg.util.datasection.AbstractFileRegistry;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Setting;

public abstract class AbstractReserveFileRegistry<T extends Model> extends AbstractFileRegistry<T> {
    public AbstractReserveFileRegistry(String name, int expireInMins) {
        super(PrivateReserve.SAVE_PATH, name, Setting.SAVE_PRETTY.getBoolean(), expireInMins);
    }
}

