package me.hqm.privatereserve.landmark;

import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.landmark.data.JsonFileLandmarkDB;
import me.hqm.privatereserve.landmark.data.LandmarkDatabase;
import me.hqm.privatereserve.landmark.data.MsgPackFileLandmarkDB;

public class Landmarks {
    static LandmarkDatabase LANDMARK_DATA;

    // -- DATA -- //

    private Landmarks() {
    }

    public static LandmarkDatabase data() {
        return LANDMARK_DATA;
    }

    // -- INIT -- //

    public static void init() {
        // Files
        if (Settings.FILE_FORMAT.getString().equalsIgnoreCase(SupportedFormat.MESSAGEPACK.name())) {
            LANDMARK_DATA = new MsgPackFileLandmarkDB();
        } else {
            // Default to Json
            LANDMARK_DATA = new JsonFileLandmarkDB();
        }
        LANDMARK_DATA.loadAll();

        // Commands
        PrivateReserve.registerCommand(LandmarkCommand.createCommand(), "warp");

        // Log
        PrivateReserve.logger().info("Locked blocks enabled.");
    }
}
