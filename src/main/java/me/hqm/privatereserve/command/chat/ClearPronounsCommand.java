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

public class ClearPronounsCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof Player &&
                (PrivateReserve.PLAYER_R.isVisitorOrExpelled(((Player) sender).getUniqueId()))) {
            return CommandResult.QUIET_ERROR;
        }
        if (args.length == 1) {
            if (sender.hasPermission("privatereserve.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    clearPronouns(PrivateReserve.PLAYER_R.fromPlayer(maybeTarget.get()).get());
                    sender.sendMessage(Component.text("Pronouns cleared for " + maybeTarget.get().getName(), NamedTextColor.GREEN));
                    return CommandResult.SUCCESS;
                }

                sender.sendMessage(Component.text("That player does not exist, please try again.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
            return CommandResult.INVALID_SYNTAX;
        }

        if (args.length == 0) {
            if (sender instanceof Player self) {
                clearPronouns(PrivateReserve.PLAYER_R.fromPlayer(self).get());
                sender.sendMessage(Component.text("Pronouns cleared.", NamedTextColor.GREEN));
                return CommandResult.SUCCESS;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    void clearPronouns(PlayerModel model) {
        model.setPronouns(null);
        model.buildNameTag();
    }
}
