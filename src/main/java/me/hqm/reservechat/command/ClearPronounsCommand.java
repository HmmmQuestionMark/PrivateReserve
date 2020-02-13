package me.hqm.reservechat.command;

import me.hqm.reservechat.ReserveChat;
import me.hqm.reservechat.model.PlayerModel;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ClearPronounsCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if(args.length == 1) {
            if(sender.hasPermission("reservechat.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    clearPronouns(maybeTarget.get());
                    sender.sendMessage(ChatColor.GREEN + "Pronouns cleared for " + maybeTarget.get().getName());
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
                clearPronouns(self);
                sender.sendMessage(ChatColor.GREEN + "Pronouns cleared.");
                return CommandResult.SUCCESS;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    void clearPronouns(OfflinePlayer target) {
        PlayerModel model = ReserveChat.PLAYER_R.fromPlayer(target);
        model.setPronouns(null);
        model.buildNameTag();
    }
}
