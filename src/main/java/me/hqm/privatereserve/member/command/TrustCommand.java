package me.hqm.privatereserve.member.command;

import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.Optional;

public class TrustCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("trust")) {
            // Needs at least 1 argument
            if (args.length < 1) {
                return CommandResult.INVALID_SYNTAX;
            }

            // Get the invitee
            Optional<MemberDocument> model = _PrivateReserve.MEMBER_DATA.fromName(args[0]);
            if (model.isEmpty()) {
                sender.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            } else if (model.get().isExpelled()) {
                sender.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
            OfflinePlayer invitee = model.get().getOfflinePlayer();

            if (!sender.hasPermission("privatereserve.admin")) {
                return CommandResult.NO_PERMISSIONS;
            }

            // Register from player
            else {
                model.get().setTrusted(true);
            }

            // If they are online, let them know
            if (invitee.isOnline()) {
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Celebrate!", NamedTextColor.YELLOW), Component.text("You are now trusted!", NamedTextColor.GREEN), times);
                invitee.getPlayer().showTitle(title);
            }

            // If this is reached, the invite worked
            sender.sendMessage(Component.text(invitee.getName() + " has been trusted.", NamedTextColor.GREEN));

            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }
}
