package me.hqm.privatereserve.relationaldata;

import me.hqm.privatereserve.FileDatabase;

public class RelationalDataFileDatabase extends FileDatabase<RelationalDataDocument> implements
        RelationalDataDatabase {
    public RelationalDataFileDatabase() {
        super(NAME, 0);
    }
}
