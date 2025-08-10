package me.hqm.privatereserve;

import org.bukkit.plugin.java.JavaPlugin;

public class PrivateReservePlugin extends JavaPlugin {

    // -- BUKKIT ENABLE/DISABLE METHODS -- //

    @Override
    public void onEnable() {
        // Config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Enable
        new PrivateReserve(this);
    }

    @Override
    public void onDisable() {
        PrivateReserve.PRIVATE_RESERVE.disable();
    }
}
