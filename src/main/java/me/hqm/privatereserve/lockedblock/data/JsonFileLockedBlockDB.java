package me.hqm.privatereserve.lockedblock.data;


import me.hqm.document.json.JsonFileDatabase;
import me.hqm.privatereserve.Settings;

public class JsonFileLockedBlockDB extends JsonFileDatabase<LockedBlock> implements LockedBlockDatabase {
    public JsonFileLockedBlockDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
