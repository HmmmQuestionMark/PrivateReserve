package me.hqm.privatereserve;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.chat.ChatTags;
import me.hqm.privatereserve.delivery.Deliveries;
import me.hqm.privatereserve.landmark.Landmarks;
import me.hqm.privatereserve.lockedblock.LockedBlocks;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.region.Regions;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Logger;

public class PrivateReserve extends JavaPlugin {

    // -- STATIC INST -- //
    private static PrivateReserve INST;

    // -- BUKKIT ENABLE/DISABLE METHODS -- //

    public static void registerCommand(LiteralCommandNode<CommandSourceStack> command, String... alias) {
        INST.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            if (alias.length < 1) {
                commands.registrar().register(command);
            } else {
                commands.registrar().register(command, Arrays.asList(alias));
            }
        });
    }

    public static JavaPlugin plugin() {
        return INST;
    }

    public static Logger logger() {
        return plugin().getLogger();
    }

    @Override
    public void onEnable() {
        // Set static inst
        INST = this;

        // Save default config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Configure database type
        SupportedFormat saveFormat = SupportedFormat.JSON;
        try {
            saveFormat = SupportedFormat.valueOf(Settings.FILE_FORMAT.getString().toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        // Check WorldGuard
        Regions.init(Bukkit.getWorlds().getFirst().getSpawnLocation());

        // Enable members
        Members.init(this, saveFormat);

        // Enable landmarks
        if (Settings.LANDMARK_ENABLED.getBoolean()) {
            Landmarks.init(saveFormat);
        }

        // Enable locked blocks
        if (Settings.LOCKED_BLOCK_ENABLED.getBoolean()) {
            LockedBlocks.init(this, saveFormat);
        }

        // Deliveries
        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            Deliveries.init(this, saveFormat);
        }

        // Build chat format
        if (Settings.CHAT_TAGS_ENABLED.getBoolean() && getServer().getPluginManager().isPluginEnabled("Chitchat")) {
            com.demigodsrpg.chitchat.Chitchat.getChatFormat().addAll(new com.demigodsrpg.chitchat.ChatTag[]{
                    ChatTags.WORLD_TYPE_TAG,
                    ChatTags.ADMIN_TAG,
                    ChatTags.VISITOR_TAG,
                    ChatTags.TRUSTED_TAG,
                    ChatTags.NAME_TAG
            });
        }

        // Misc tasks
        new TimeTask().runTaskTimer(this, 1, 1);
    }

    @Override
    public void onDisable() {
        // Disable deliveries
        if (Settings.DELIVERY_MOB_ENABLED.getBoolean()) {
            Deliveries.uninit();
        }

        // Manually unregister listeners and tasks
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }
}
