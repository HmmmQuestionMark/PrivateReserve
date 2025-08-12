package me.hqm.privatereserve.lockedblock.data;


import me.hqm.document.msgpack.MsgPackFileDatabase;
import me.hqm.privatereserve.Settings;

public class MsgPackFileLockedBlockDB extends MsgPackFileDatabase<LockedBlock> implements LockedBlockDatabase {
    public MsgPackFileLockedBlockDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
