package me.hqm.privatereserve.registry.mongo;

import com.demigodsrpg.util.datasection.AbstractMongoRegistry;
import com.mongodb.client.MongoDatabase;
import me.hqm.privatereserve.model.RelationalDataModel;
import me.hqm.privatereserve.registry.RelationalDataRegistry;

public class MRelationalDataRegistry extends AbstractMongoRegistry<RelationalDataModel> implements
        RelationalDataRegistry {
    public MRelationalDataRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), 0, true);
    }
}
