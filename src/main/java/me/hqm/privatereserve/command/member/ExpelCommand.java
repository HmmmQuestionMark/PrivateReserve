package me.hqm.privatereserve.command.member;

import com.demigodsrpg.chitchat.Chitchat;
import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.util.RegionUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ExpelCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("expel")) {
            // Needs at least 1 argument
            if (args.length < 1) {
                return CommandResult.INVALID_SYNTAX;
            }

            // Get the player to be expelled
            Optional<PlayerModel> model = PrivateReserve.PLAYER_R.fromName(args[0]);
            if (!model.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Player is still a visitor.");
                return CommandResult.QUIET_ERROR;
            } else if (model.get().isExpelled()) {
                sender.sendMessage(ChatColor.RED + "Player is already expelled.");
                return CommandResult.QUIET_ERROR;
            }
            OfflinePlayer expelled = model.get().getOfflinePlayer();

            // Stop untrusted from expelling
            if (!model.get().getInvitedFrom().equals(((Player) sender).getUniqueId().toString()) &&
                    !(sender.hasPermission("privatereserve.admin") || sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "Sorry, can't expel that person.");
                return CommandResult.NO_PERMISSIONS;
            }

            // Expell the player.
            model.get().setExpelled(true);

            if (expelled.isOnline()) {
                expelled.getPlayer().teleport(RegionUtil.visitingLocation());
                Chitchat.sendTitle(expelled.getPlayer(), 10, 80, 10, ChatColor.RED + "Expelled.", ChatColor.YELLOW +
                        "You were expelled, go away.");
            }
            // If this is reached, the invite worked
            sender.sendMessage(ChatColor.RED + expelled.getName() + " has been expelled.");

            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }
}
