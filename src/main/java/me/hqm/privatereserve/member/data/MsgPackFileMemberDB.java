package me.hqm.privatereserve.member.data;

import me.hqm.document.msgpack.MsgPackFileDatabase;
import me.hqm.privatereserve.Settings;

public class MsgPackFileMemberDB extends MsgPackFileDatabase<Member> implements MemberDatabase {
    public MsgPackFileMemberDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
