package me.hqm.privatereserve.delivery.old.data;

import me.hqm.document.json.JsonFileDatabase;
import me.hqm.privatereserve.Settings;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public class JsonFile_DeliveryDB extends JsonFileDatabase<_DeliveryDocument> implements _DeliveryDatabase {
    public JsonFile_DeliveryDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
