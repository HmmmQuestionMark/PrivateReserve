package me.hqm.privatereserve.command.member;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.util.RegionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class InviteCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("invite")) {
            // Needs at least 1 argument
            if (args.length < 1) {
                return CommandResult.INVALID_SYNTAX;
            }

            // Get the invitee
            OfflinePlayer invitee = Bukkit.getOfflinePlayer(args[0]);

            // Already invited
            if (!PrivateReserve.PLAYER_R.isVisitor(invitee.getUniqueId())) {
                sender.sendMessage(Component.text("That player is already invited.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }

            // Check if they were expelled and give a warning
            if (PrivateReserve.PLAYER_R.isExpelled(invitee.getUniqueId())) {
                sender.sendMessage(Component.text("That player was previously expelled, please be cautious of them.", NamedTextColor.RED));
                Optional<PlayerModel> opModel = PrivateReserve.PLAYER_R.fromPlayer(invitee);
                if (opModel.isPresent()) {
                    PlayerModel expelled = opModel.get();
                    expelled.setExpelled(false);
                    if (sender instanceof ConsoleCommandSender) {
                        expelled.setInvitedFrom("CONSOLE");
                    } else {
                        expelled.setInvitedFrom(((Player) sender).getUniqueId().toString());
                    }
                }
            }

            // Stop untrusted from inviting
            else if (!PrivateReserve.PLAYER_R.isTrusted(((Player) sender).getUniqueId())) {
                sender.sendMessage(Component.text("Sorry, you aren't (yet) a trusted player.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }

            // Register from player
            else {
                if (args.length > 1) {
                    Optional<PlayerModel> primary = PrivateReserve.PLAYER_R.fromName(args[1]);
                    if (primary.isPresent()) {
                        PrivateReserve.PLAYER_R.invite(invitee, (Player) sender, primary.get().getKey());
                    } else {
                        sender.sendMessage(Component.text("The provided primary account does not exist.", NamedTextColor.RED));
                        return CommandResult.QUIET_ERROR;
                    }
                }
                PrivateReserve.PLAYER_R.invite(invitee, (Player) sender);
            }

            // Register from console
            if (sender instanceof ConsoleCommandSender) {
                PrivateReserve.PLAYER_R.inviteConsole(invitee);
            }

            // Let the invitee know
            if (invitee.isOnline()) {
                invitee.getPlayer().teleport(RegionUtil.spawnLocation());
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Celebrate!", NamedTextColor.YELLOW), Component.text("You were invited! Have fun!", NamedTextColor.GREEN), times);
                invitee.getPlayer().showTitle(title);
            }

            // If this is reached, the invite worked
            sender.sendMessage(Component.text(invitee.getName() + " has been invited.", NamedTextColor.GREEN));

            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }
}
