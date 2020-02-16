package me.hqm.privatereserve.command.chat;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PronounsCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof Player &&
                (PrivateReserve.PLAYER_R.isVisitorOrExpelled(((Player) sender).getUniqueId()))) {
            return CommandResult.QUIET_ERROR;
        }
        if(args.length > 1) {
            if (args.length == 2 && sender.hasPermission("privatereserve.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    if (setPronouns(PrivateReserve.PLAYER_R.fromPlayer(maybeTarget.get()).get(), args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "Pronouns set for " + maybeTarget.get().getName());
                        return CommandResult.SUCCESS;
                    }

                    sender.sendMessage(ChatColor.RED + "Pronouns are too long, please try again.");
                    return CommandResult.QUIET_ERROR;
                }

                sender.sendMessage(ChatColor.RED + "That player does not exist, please try again.");
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.INVALID_SYNTAX;
        }

        if(args.length == 1) {
            if (sender instanceof Player) {
                Player self = (Player) sender;
                if (setPronouns(PrivateReserve.PLAYER_R.fromPlayer(self).get(), args[0])) {
                    sender.sendMessage(ChatColor.GREEN + "Pronouns set.");
                    return CommandResult.SUCCESS;
                }

                sender.sendMessage(ChatColor.RED + "Pronouns are too long, please try again.");
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    boolean setPronouns(PlayerModel model, String pronouns) {
        if (pronouns.length() <= 16) {
            model.setPronouns(pronouns);
            model.buildNameTag();
            return true;
        }
        return false;
    }
}
