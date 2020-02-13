package me.hqm.reservechat.command;

import me.hqm.reservechat.ReserveChat;
import me.hqm.reservechat.model.PlayerModel;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PronounsCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if(args.length > 1) {
            if(args.length == 2 && sender.hasPermission("reservechat.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    setPronouns(maybeTarget.get(), args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Pronouns set for " + maybeTarget.get().getName());
                    return CommandResult.SUCCESS;
                }

                sender.sendMessage(ChatColor.RED + "That player does not exist, please try again.");
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.INVALID_SYNTAX;
        }

        if(args.length == 1) {
            if (sender instanceof Player) {
                Player self = (Player) sender;
                setPronouns(self, args[0]);
                sender.sendMessage(ChatColor.GREEN + "Pronouns set.");
                return CommandResult.SUCCESS;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    void setPronouns(OfflinePlayer target, String pronouns) {
        PlayerModel model = ReserveChat.PLAYER_R.fromPlayer(target);
        model.setPronouns(pronouns);
        model.buildNameTag();
    }
}
