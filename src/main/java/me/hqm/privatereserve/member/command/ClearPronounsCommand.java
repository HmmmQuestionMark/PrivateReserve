package me.hqm.privatereserve.member.command;

import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.member.data.MemberDocument;
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
                (_PrivateReserve.MEMBER_DATA.isVisitorOrExpelled(((Player) sender).getUniqueId()))) {
            return CommandResult.QUIET_ERROR;
        }
        if (args.length == 1) {
            if (sender.hasPermission("privatereserve.admin")) {
                Optional<Player> maybeTarget = getPlayer(args[0]);
                if (maybeTarget.isPresent()) {
                    clearPronouns(_PrivateReserve.MEMBER_DATA.fromPlayer(maybeTarget.get()).get());
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
                clearPronouns(_PrivateReserve.MEMBER_DATA.fromPlayer(self).get());
                sender.sendMessage(Component.text("Pronouns cleared.", NamedTextColor.GREEN));
                return CommandResult.SUCCESS;
            }
            return CommandResult.PLAYER_ONLY;
        }

        return CommandResult.INVALID_SYNTAX;
    }

    void clearPronouns(MemberDocument model) {
        model.setPronouns(null);
        model.buildNameTag();
    }
}
