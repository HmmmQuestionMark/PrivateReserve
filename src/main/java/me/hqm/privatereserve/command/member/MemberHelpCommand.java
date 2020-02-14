package me.hqm.privatereserve.command.member;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class MemberHelpCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length < 1) {
            return CommandResult.INVALID_SYNTAX;
        }
        switch (args[0].toUpperCase()) {
            case "TRUSTED": {
                sender.sendMessage(ChatColor.DARK_AQUA + "Trusted" + ChatColor.YELLOW +
                        " players are trusted members of the community.");
                sender.sendMessage(ChatColor.YELLOW + "Once a member is trusted, they can invite new members.");
                break;
            }
            case "VISITING": {
                sender.sendMessage(ChatColor.GREEN + "Visiting" + ChatColor.YELLOW +
                        " players are cannot leave their spawn.");
                sender.sendMessage(ChatColor.YELLOW + "Players need to be invited to play on this server.");
                break;
            }
            case "MODERATOR": {
                sender.sendMessage(ChatColor.DARK_GREEN + "Moderators" + ChatColor.YELLOW +
                        " are staff who police the activity on the server.");
                sender.sendMessage(ChatColor.YELLOW + "When you need help from a staff member, please ask for a " +
                        ChatColor.DARK_GREEN + "Moderator" + ChatColor.YELLOW + ".");
                break;
            }
            case "MODERATOR+": {
                sender.sendMessage(ChatColor.DARK_GREEN + "Moderator" + ChatColor.DARK_AQUA + "+" + ChatColor.YELLOW +
                        " are staff who manage the entire server.");
                sender.sendMessage(ChatColor.YELLOW + "On many other servers, this rank is equivalent to " +
                        ChatColor.DARK_RED + "Admin" + ChatColor.YELLOW + ".");
                break;
            }
            case "ADMIN": {
                sender.sendMessage(ChatColor.DARK_RED + "Admins" + ChatColor.YELLOW +
                        " are staff who administrate the entire server.");
                sender.sendMessage(ChatColor.YELLOW + "Development and server maintenance are handled by " +
                        ChatColor.DARK_RED + "Admins" + ChatColor.YELLOW + ".");
                break;
            }
        }
        return CommandResult.SUCCESS;
    }
}

