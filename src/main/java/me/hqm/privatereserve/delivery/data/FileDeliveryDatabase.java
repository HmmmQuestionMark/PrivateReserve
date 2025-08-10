package me.hqm.privatereserve.delivery.data;

import me.hqm.privatereserve.FileDatabase;

public class FileDeliveryDatabase extends FileDatabase<DeliveryDocument> implements DeliveryDatabase {
    public FileDeliveryDatabase() {
        super(NAME, 0);
    }
}
