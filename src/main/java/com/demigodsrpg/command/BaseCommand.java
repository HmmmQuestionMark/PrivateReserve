package com.demigodsrpg.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class BaseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        CommandResult result = onCommand(sender, command, args);
        switch (result) {
            case SUCCESS:
            case QUIET_ERROR:
                break;
            case INVALID_SYNTAX:
                sender.sendMessage(ChatColor.RED + "Invalid syntax, please try again.");
                return false;
            case NO_PERMISSIONS:
                sender.sendMessage(ChatColor.RED + "You don't have the permissions to use this command.");
                break;
            case CONSOLE_ONLY:
                sender.sendMessage(ChatColor.RED + "This command is for the console only.");
                break;
            case PLAYER_ONLY:
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                break;
            case ERROR:
                sender.sendMessage(ChatColor.RED + "An error occurred, please check the console.");
                break;
            case UNKNOWN:
            default:
                sender.sendMessage(ChatColor.RED + "The command can't run for some unknown reason.");
                break;
        }
        return true;
    }

    protected abstract CommandResult onCommand(CommandSender sender, Command command, String[] args);

    protected Optional<Player> getPlayer(final String name) {
        Player found = Bukkit.getPlayerExact(name);
        // Try for an exact match first.
        if (found != null) {
            return Optional.of(found);
        }

        String lowerName = name.toLowerCase(java.util.Locale.ENGLISH);
        int delta = Integer.MAX_VALUE;
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Try display name first
            if (player.getDisplayName().toLowerCase(java.util.Locale.ENGLISH).startsWith(lowerName)) {
                int curDelta = Math.abs(player.getName().length() - lowerName.length());
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) break;
            }

            // Try username
            if (player.getName().toLowerCase(java.util.Locale.ENGLISH).startsWith(lowerName)) {
                int curDelta = Math.abs(player.getName().length() - lowerName.length());
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) break;
            }
        }
        return Optional.ofNullable(found);
    }
}
