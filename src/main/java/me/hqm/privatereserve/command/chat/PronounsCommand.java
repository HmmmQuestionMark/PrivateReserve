package me.hqm.privatereserve.command.chat;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (args.length > 1) {
            if (args.length == 2 && sender.hasPermission("privatereserve.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    if (setPronouns(PrivateReserve.PLAYER_R.fromPlayer(maybeTarget.get()).get(), args[1])) {
                        sender.sendMessage(Component.text("Pronouns set for " + maybeTarget.get().getName(), NamedTextColor.GREEN));
                        return CommandResult.SUCCESS;
                    }

                    sender.sendMessage(Component.text("Pronouns are too long, please try again.", NamedTextColor.RED));
                    return CommandResult.QUIET_ERROR;
                }

                sender.sendMessage(Component.text("That player does not exist, please try again.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.INVALID_SYNTAX;
        }

        if (args.length == 1) {
            if (sender instanceof Player self) {
                if (setPronouns(PrivateReserve.PLAYER_R.fromPlayer(self).get(), args[0])) {
                    sender.sendMessage(Component.text("Pronouns set.", NamedTextColor.GREEN));
                    return CommandResult.SUCCESS;
                }

                sender.sendMessage(Component.text("Pronouns are too long, please try again.", NamedTextColor.RED));
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
