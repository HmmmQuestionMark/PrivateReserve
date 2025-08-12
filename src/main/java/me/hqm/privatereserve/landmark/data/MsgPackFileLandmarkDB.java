package me.hqm.privatereserve.landmark.data;

import me.hqm.document.msgpack.MsgPackFileDatabase;
import me.hqm.privatereserve.Settings;

public class MsgPackFileLandmarkDB extends MsgPackFileDatabase<Landmark> implements LandmarkDatabase {
    public MsgPackFileLandmarkDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
