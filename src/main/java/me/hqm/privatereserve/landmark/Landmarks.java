package me.hqm.privatereserve.landmark;

import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.PrivateReserve;
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

    public static void init(SupportedFormat format) {
        // Files
        switch (format) {
            case SupportedFormat.MESSAGEPACK: {
                LANDMARK_DATA = new MsgPackFileLandmarkDB();
                PrivateReserve.logger().info("MessagePack enabled for landmark data.");
                break;
            }
            case JSON:
            default: {
                LANDMARK_DATA = new JsonFileLandmarkDB();
                PrivateReserve.logger().info("Json enabled for landmark data.");
            }
        }
        LANDMARK_DATA.loadAll();

        // Commands
        PrivateReserve.registerCommand(LandmarkCommand.createCommand(), "warp");

        // Log
        PrivateReserve.logger().info("Locked blocks enabled.");
    }
}
