package me.hqm.privatereserve.command.member;

import com.demigodsrpg.chitchat.Chitchat;
import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.util.RegionUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Optional;

public class InviteCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("invite")) {
            // Needs at least 1 argument
            if (args.length < 1) {
                return CommandResult.INVALID_SYNTAX;
            }

            // Get the invitee
            OfflinePlayer invitee = Bukkit.getOfflinePlayer(args[0]);

            // Register from console
            if (sender instanceof ConsoleCommandSender) {
                PrivateReserve.PLAYER_R.inviteConsole(invitee);
                return CommandResult.SUCCESS;
            }

            // Already invited
            if (!PrivateReserve.PLAYER_R.isVisitor(invitee)) {
                sender.sendMessage(ChatColor.RED + "That player is already invited.");
                return CommandResult.QUIET_ERROR;
            }

            // Check if they were expelled and give a warning
            if (PrivateReserve.PLAYER_R.isExpelled(invitee)) {
                sender.sendMessage(ChatColor.RED + "That player was expelled, please be cautious of them.");
                Optional<PlayerModel> opModel = PrivateReserve.PLAYER_R.fromPlayer(invitee);
                if (opModel.isPresent()) {
                    PlayerModel expelled = opModel.get();
                    expelled.setExpelled(false);
                    if (sender instanceof ConsoleCommandSender) {
                        expelled.setInvitedFrom("CONSOLE");
                    } else {
                        expelled.setInvitedFrom(((Player) sender).getUniqueId().toString());
                    }
                }
            }

            // Stop untrusted from inviting
            else if (!PrivateReserve.PLAYER_R.isTrusted((Player) sender)) {
                sender.sendMessage(ChatColor.RED + "Sorry, you aren't (yet) a trusted player.");
                return CommandResult.QUIET_ERROR;
            }

            // Register from player
            else {
                PrivateReserve.PLAYER_R.invite(invitee, (Player) sender);
            }

            // Let the invitee know
            if (invitee.isOnline()) {
                invitee.getPlayer().teleport(RegionUtil.spawnLocation());
                Chitchat.sendTitle(invitee.getPlayer(), 10, 80, 10, ChatColor.YELLOW + "Celebrate!", ChatColor.GREEN +
                        "You were invited! Have fun!");
            }

            // If this is reached, the invite worked
            sender.sendMessage(ChatColor.RED + invitee.getName() + " has been invited.");

            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }
}
