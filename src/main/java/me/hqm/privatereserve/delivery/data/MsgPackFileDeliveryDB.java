package me.hqm.privatereserve.delivery.data;

import me.hqm.document.msgpack.MsgPackFileDatabase;
import me.hqm.privatereserve.Settings;

public class MsgPackFileDeliveryDB extends MsgPackFileDatabase<DeliveryDocument> implements DeliveryDatabase {
    public MsgPackFileDeliveryDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
