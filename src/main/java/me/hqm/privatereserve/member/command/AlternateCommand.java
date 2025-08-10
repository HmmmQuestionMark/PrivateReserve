package me.hqm.privatereserve.member.command;

import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class AlternateCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        // Needs at least 2 arguments
        if (args.length < 2) {
            return CommandResult.INVALID_SYNTAX;
        }

        // Get the invitee
        Optional<MemberDocument> model = _PrivateReserve.MEMBER_DATA.fromName(args[0]);
        if (model.isEmpty()) {
            Bukkit.getServer().dispatchCommand(sender, "invite " + args[0] + " " + args[1]);
            return CommandResult.SUCCESS;
        } else if (model.get().isExpelled()) {
            sender.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
            return CommandResult.QUIET_ERROR;
        }
        OfflinePlayer invitee = model.get().getOfflinePlayer();

        if (!sender.hasPermission("privatereserve.admin")) {
            return CommandResult.NO_PERMISSIONS;
        }

        // Register from player
        else {
            Optional<MemberDocument> primary = _PrivateReserve.MEMBER_DATA.fromName(args[1]);
            if (primary.isPresent()) {
                model.get().setPrimaryAccount(primary.get().getKey());
            } else {
                sender.sendMessage(Component.text("The provided primary account does not exist.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
        }

        // If this is reached, the invite worked
        sender.sendMessage(Component.text(invitee.getName() + " has been set as an alternate account.", NamedTextColor.RED));

        return CommandResult.SUCCESS;
    }
}
