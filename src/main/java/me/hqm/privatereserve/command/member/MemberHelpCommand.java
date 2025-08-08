package me.hqm.privatereserve.command.member;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                sender.sendMessage(Component.text("Trusted", NamedTextColor.DARK_AQUA).
                        append(Component.text(" players are trusted members of the community.", NamedTextColor.YELLOW)));
                sender.sendMessage(Component.text("Once a member is trusted, they can invite new members.", NamedTextColor.YELLOW));
                break;
            }
            case "VISITING": {
                sender.sendMessage(Component.text("Visiting", NamedTextColor.GREEN).
                        append(Component.text(" players are cannot leave their spawn.", NamedTextColor.YELLOW)));
                sender.sendMessage(Component.text("Players need to be invited to play on this server.", NamedTextColor.YELLOW));
                break;
            }
            case "ADMIN": {
                sender.sendMessage(Component.text("Admins", NamedTextColor.DARK_RED).
                        append(Component.text(" are staff who administrate the entire server.", NamedTextColor.YELLOW)));
                sender.sendMessage(Component.text("Development and server maintenance are handled by them too.", NamedTextColor.YELLOW));
                break;
            }
        }
        return CommandResult.SUCCESS;
    }
}

