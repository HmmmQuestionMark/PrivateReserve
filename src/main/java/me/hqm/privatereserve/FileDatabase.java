package me.hqm.privatereserve;

import me.hqm.document.Document;
import me.hqm.document.GeneralFileDatabase;

public abstract class FileDatabase<T extends Document> extends GeneralFileDatabase<T> {
    public FileDatabase(String name, int expireInMins) {
        super(Settings.FILE_FOLDER.getString(), name, Settings.FILE_PRETTY.getBoolean(), expireInMins);
    }
}

