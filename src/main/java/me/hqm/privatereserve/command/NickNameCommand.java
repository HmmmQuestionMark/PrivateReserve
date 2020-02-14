package me.hqm.privatereserve.command;

import me.hqm.privatereserve.ReserveChat;
import me.hqm.privatereserve.model.PlayerModel;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class NickNameCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if(args.length > 1) {
            if(args.length == 2 && sender.hasPermission("reservechat.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    if (setNickName(maybeTarget.get(), args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "Nickname set for " + maybeTarget.get().getName());
                        return CommandResult.SUCCESS;
                    }

                    sender.sendMessage(ChatColor.RED + "Nickname is too long, please try again.");
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
                if (setNickName(self, args[0])) {
                    sender.sendMessage(ChatColor.GREEN + "Nickname set.");
                    return CommandResult.SUCCESS;
                }

                sender.sendMessage(ChatColor.RED + "Nickname is too long, please try again.");
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    boolean setNickName(OfflinePlayer target, String nickName) {
        if (nickName.length() <= 25) {
            PlayerModel model = ReserveChat.PLAYER_R.fromPlayer(target);
            model.setNickName(nickName);
            model.buildNameTag();
            return true;
        }
        return false;
    }
}
