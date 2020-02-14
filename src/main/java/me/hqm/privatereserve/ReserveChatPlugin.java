package me.hqm.privatereserve;

import com.demigodsrpg.chitchat.util.LibraryHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ReserveChatPlugin extends JavaPlugin  {

    // -- LIBRARY HANDLER -- //

    private static LibraryHandler LIBRARIES;

    // -- BUKKIT ENABLE/DISABLE METHODS -- //

    @Override
    public void onEnable() {
        // Config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Get and load the libraries
        LIBRARIES = new LibraryHandler(this);

        // MongoDB
        if (Setting.MONGODB_PERSISTENCE) {
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, Depends.ORG_MONGO, Depends.MONGODB,
                    Depends.MONGODB_VER);
        }

        // Enable
        new ReserveChat(this);
    }

    @Override
    public void onDisable() {
        ReserveChat.RESERVE_CHAT.disable();
    }
}
