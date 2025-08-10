package me.hqm.privatereserve.delivery.old.data;

import me.hqm.privatereserve.FileDatabase;

public class File_DeliveryDatabase extends FileDatabase<_DeliveryDocument> implements _DeliveryDatabase {
    public File_DeliveryDatabase() {
        super(NAME, 0);
    }
}
