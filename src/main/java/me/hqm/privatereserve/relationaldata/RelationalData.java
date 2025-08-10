package me.hqm.privatereserve.relationaldata;

import me.hqm.privatereserve.PrivateReserve;

public class RelationalData {
    private RelationalData() {
    }

    static RelationalDataDatabase RELATIONAL_DATA;

    public static void init() {
        // Files
        RELATIONAL_DATA = new RelationalDataFileDatabase();
        RELATIONAL_DATA.loadAllFromDb();

        // Tasks
        new ExpiredTask().runTaskTimerAsynchronously(PrivateReserve.plugin(), 20, 20);

        // Log
        PrivateReserve.logger().info("Relational data enabled.");
    }

    public static RelationalDataDatabase data() {
        return RELATIONAL_DATA;
    }
}
