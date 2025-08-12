package me.hqm.privatereserve.landmark.data;

import me.hqm.document.json.JsonFileDatabase;
import me.hqm.privatereserve.Settings;

public class JsonFileLandmarkDB extends JsonFileDatabase<Landmark> implements LandmarkDatabase {
    public JsonFileLandmarkDB() {
        super(Settings.FILE_FOLDER.getString(), NAME, 0);
    }
}
