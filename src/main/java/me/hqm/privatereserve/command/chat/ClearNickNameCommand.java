package me.hqm.privatereserve.command.chat;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ClearNickNameCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof Player && (PrivateReserve.PLAYER_R.isVisitorOrExpelled((Player) sender))) {
            return CommandResult.QUIET_ERROR;
        }
        if(args.length == 1) {
            if (sender.hasPermission("privatereserve.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    clearNickName(maybeTarget.get());
                    sender.sendMessage(ChatColor.GREEN + "Nickname cleared for " + maybeTarget.get().getName());
                    return CommandResult.SUCCESS;
                }

                sender.sendMessage(ChatColor.RED + "That player does not exist, please try again.");
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.INVALID_SYNTAX;
        }

        if(args.length == 0) {
            if (sender instanceof Player) {
                Player self = (Player) sender;
                clearNickName(self);
                sender.sendMessage(ChatColor.GREEN + "Nickname cleared.");
                return CommandResult.SUCCESS;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    void clearNickName(OfflinePlayer target) {
        PlayerModel model = PrivateReserve.PLAYER_R.fromPlayer(target).get();
        model.setNickName(target.getName());
        model.buildNameTag();
    }
}
