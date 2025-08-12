package me.hqm.privatereserve.delivery.data;

import me.hqm.document.msgpack.MsgPackFileDatabase;
import me.hqm.privatereserve.Settings;

public class MsgPackFileDeliveryDB extends MsgPackFileDatabase<DeliveryMob> implements DeliveryDatabase {
    public MsgPackFileDeliveryDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
