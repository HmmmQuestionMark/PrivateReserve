package me.hqm.command;

import com.mojang.brigadier.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public enum CommandResult {
    SUCCESS,
    QUIET_ERROR,
    INVALID_SYNTAX("Invalid syntax, please try again."),
    NO_PERMISSIONS("You don't have permission to use this command."),
    CONSOLE_ONLY("This command is for the console only."),
    PLAYER_ONLY("This command can only be used by a player."),
    ERROR("An error occurred, please check the console."),
    UNKNOWN("The command can't run for some unknown reason.");

    private final String message;
    private final TextColor color;

    CommandResult() {
        message = null;
        color = null;
    }

    CommandResult(String message) {
        this.message = message;
        this.color = NamedTextColor.RED;
    }

    public @Nullable Component getComponent() {
        return message != null ? Component.text(message, color) : null;
    }

    public int send(CommandSender sender) {
        if (getComponent() != null) {
            sender.sendMessage(getComponent());
        }
        return Command.SINGLE_SUCCESS;
    }
}
