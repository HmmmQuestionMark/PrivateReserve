package me.hqm.privatereserve.delivery;

import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.delivery.data.DeliveryDatabase;
import me.hqm.privatereserve.delivery.data.JsonFileDeliveryDB;
import me.hqm.privatereserve.delivery.data.MsgPackFileDeliveryDB;
import me.hqm.privatereserve.delivery.listener.DeliveryListener;
import me.hqm.privatereserve.delivery.old.data.JsonFile_DeliveryDB;
import me.hqm.privatereserve.delivery.old.data._DeliveryDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

public class Deliveries {
    private static DeliveryDatabase DELIVERY_DATA;

    // -- DATA -- //
    @Deprecated
    @ApiStatus.Obsolete
    @ApiStatus.ScheduledForRemoval(inVersion = "1.1")
    private static _DeliveryDatabase _DELIVERY_DATA;

    private Deliveries() {
    }

    public static DeliveryDatabase data() {
        return DELIVERY_DATA;
    }

    @Deprecated
    @ApiStatus.Obsolete
    @ApiStatus.ScheduledForRemoval(inVersion = "1.1")
    public static _DeliveryDatabase ___data() {
        return _DELIVERY_DATA;
    }

    // -- INIT -- //

    public static void init(JavaPlugin plugin, SupportedFormat format) {
        // Files
        switch (format) {
            case SupportedFormat.MESSAGEPACK: {
                DELIVERY_DATA = new MsgPackFileDeliveryDB();
                PrivateReserve.logger().info("MessagePack enabled for delivery data.");
                break;
            }
            case JSON:
            default: {
                DELIVERY_DATA = new JsonFileDeliveryDB();
                PrivateReserve.logger().info("Json enabled for delivery data.");
            }
        }
        DELIVERY_DATA.loadAll();

        _DELIVERY_DATA = new JsonFile_DeliveryDB();
        _DELIVERY_DATA.loadAll();

        // Listeners
        plugin.getServer().getPluginManager().registerEvents(new DeliveryListener(), plugin);

        // Commands

        // Log
        PrivateReserve.logger().info("Locked blocks enabled.");
    }

    public static void uninit() {
        // Unload chunks
        _DELIVERY_DATA.clearAllChunks();
    }
}
