package me.hqm.privatereserve.command.member;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import org.bukkit.*;
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
        Optional<PlayerModel> model = PrivateReserve.PLAYER_R.fromName(args[0]);
        if (!model.isPresent()) {
            Bukkit.getServer().dispatchCommand(sender, "invite " + args[0] + " " + args[1]);
            return CommandResult.SUCCESS;
        } else if (model.get().isExpelled()) {
            sender.sendMessage(ChatColor.RED + "Player is expelled, please try a different name.");
            return CommandResult.QUIET_ERROR;
        }
        OfflinePlayer invitee = model.get().getOfflinePlayer();

        if (!sender.hasPermission("seasons.admin")) {
            return CommandResult.NO_PERMISSIONS;
        }

        // Register from player
        else {
            Optional<PlayerModel> primary = PrivateReserve.PLAYER_R.fromName(args[1]);
            if (primary.isPresent()) {
                model.get().setPrimaryAccount(primary.get().getKey());
            } else {
                sender.sendMessage(ChatColor.RED + "The provided primary account does not exist.");
                return CommandResult.QUIET_ERROR;
            }
        }

        // If this is reached, the invite worked
        sender.sendMessage(ChatColor.RED + invitee.getName() + " has been set as an alternate account.");

        return CommandResult.SUCCESS;
    }
}
