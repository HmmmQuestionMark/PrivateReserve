package me.hqm.reservechat.registry.mongo;

import com.demigodsrpg.util.datasection.AbstractMongoRegistry;
import com.mongodb.client.MongoDatabase;
import me.hqm.reservechat.model.PlayerModel;
import me.hqm.reservechat.registry.PlayerRegistry;

public class MPlayerRegistry extends AbstractMongoRegistry<PlayerModel> implements PlayerRegistry {
    public MPlayerRegistry(MongoDatabase database) {
        super(database.getCollection(NAME), 0, true);
    }
}
