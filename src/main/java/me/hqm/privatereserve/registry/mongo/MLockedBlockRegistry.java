package me.hqm.privatereserve.registry.mongo;

import com.demigodsrpg.util.datasection.AbstractMongoRegistry;
import com.mongodb.client.MongoDatabase;
import me.hqm.privatereserve.model.LockedBlockModel;
import me.hqm.privatereserve.registry.LockedBlockRegistry;

public class MLockedBlockRegistry extends AbstractMongoRegistry<LockedBlockModel> implements LockedBlockRegistry {
    public MLockedBlockRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), 0, true);
    }
}
