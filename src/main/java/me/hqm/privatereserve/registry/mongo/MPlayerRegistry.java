package me.hqm.privatereserve.registry.mongo;

import com.demigodsrpg.util.datasection.AbstractMongoRegistry;
import com.mongodb.client.MongoDatabase;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.registry.PlayerRegistry;

public class MPlayerRegistry extends AbstractMongoRegistry<PlayerModel> implements PlayerRegistry {
    public MPlayerRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), 0, true);
    }
}
