package me.hqm.privatereserve.member.data;

import me.hqm.privatereserve.FileDatabase;

public class FileMemberDatabase extends FileDatabase<MemberDocument> implements MemberDatabase {
    public FileMemberDatabase() {
        super(NAME, 0);
    }
}
