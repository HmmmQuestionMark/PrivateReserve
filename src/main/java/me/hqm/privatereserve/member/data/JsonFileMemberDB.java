package me.hqm.privatereserve.member.data;


import me.hqm.document.json.JsonFileDatabase;
import me.hqm.privatereserve.Settings;

public class JsonFileMemberDB extends JsonFileDatabase<MemberDocument> implements MemberDatabase {
    public JsonFileMemberDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, Settings.FILE_PRETTY.getBoolean(), 0);
    }
}
