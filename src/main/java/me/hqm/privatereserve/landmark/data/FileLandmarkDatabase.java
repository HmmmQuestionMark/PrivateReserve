package me.hqm.privatereserve.landmark.data;

import me.hqm.privatereserve.FileDatabase;

public class FileLandmarkDatabase extends FileDatabase<LandmarkDocument> implements LandmarkDatabase {
    public FileLandmarkDatabase() {
        super(NAME, 0);
    }
}
