package me.hqm.privatereserve.delivery.data;

import me.hqm.document.json.JsonFileDatabase;
import me.hqm.privatereserve.Settings;

public class JsonFileDeliveryDB extends JsonFileDatabase<DeliveryMob> implements DeliveryDatabase {
    public JsonFileDeliveryDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, Settings.FILE_PRETTY.getBoolean(), 0);
    }
}
