package me.hqm.privatereserve;

import com.demigodsrpg.chitchat.ChatTag;
import me.hqm.privatereserve.chat.ChatTags;
import me.hqm.privatereserve.relationaldata.RelationalData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public class PrivateReserve extends JavaPlugin {

    // -- STATIC INST -- //
    private static PrivateReserve INST;

    // -- BUKKIT ENABLE/DISABLE METHODS -- //

    @Override
    public void onEnable() {
        // Set static inst
        INST = this;

        // Save default config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load databases
        getLogger().info("Json file saving enabled.");
        RelationalData.


        // Build chat format
        Chitchat.getChatFormat().addAll(new ChatTag[]{
                ChatTags.WORLD_TYPE_TAG,
                ChatTags.ADMIN_TAG,
                ChatTags.VISITOR_TAG,
                ChatTags.TRUSTED_TAG,
                ChatTags.NAME_TAG
        });

        // Misc tasks
        new TimeTask().runTaskTimer(this, 1, 1);

        // Enable
        new _PrivateReserve(this);
    }

    @Override
    public void onDisable() {
        _PrivateReserve.PRIVATE_RESERVE.disable();
    }

    public static JavaPlugin plugin() {
        return INST;
    }

    public static Logger logger() {
        return plugin().getLogger();
    }
}
