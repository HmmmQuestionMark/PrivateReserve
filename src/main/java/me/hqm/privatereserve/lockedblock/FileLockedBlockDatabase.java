package me.hqm.privatereserve.lockedblock;

import me.hqm.privatereserve.FileDatabase;

public class FileLockedBlockDatabase extends FileDatabase<LockedBlockDocument> implements LockedBlockDatabase {
    public FileLockedBlockDatabase() {
        super(NAME, 0);
    }
}
