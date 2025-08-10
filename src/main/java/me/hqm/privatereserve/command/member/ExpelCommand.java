package me.hqm.privatereserve.command.member;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.util.RegionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class ExpelCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("expel")) {
            // Needs at least 1 argument
            if (args.length < 1) {
                return CommandResult.INVALID_SYNTAX;
            }

            // Get the player to be expelled
            Optional<PlayerModel> model = PrivateReserve.PLAYER_R.fromName(args[0]);
            if (model.isEmpty()) {
                sender.sendMessage(Component.text("Player is still a visitor.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            } else if (model.get().isExpelled()) {
                sender.sendMessage(Component.text("Player is already expelled.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
            OfflinePlayer expelled = model.get().getOfflinePlayer();

            // Stop untrusted from expelling
            if (!model.get().getInvitedFrom().equals(((Player) sender).getUniqueId().toString()) &&
                    !(sender.hasPermission("privatereserve.admin") || sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Component.text("Sorry, can't expel that person.", NamedTextColor.RED));
                return CommandResult.NO_PERMISSIONS;
            }

            // Expell the player.
            model.get().setExpelled(true);

            if (expelled.isOnline()) {
                expelled.getPlayer().teleport(RegionUtil.visitingLocation());
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Expelled.", NamedTextColor.RED), Component.text("You were expelled, go away.", NamedTextColor.YELLOW), times);
                expelled.getPlayer().showTitle(title);
            }
            // If this is reached, the invite worked
            sender.sendMessage(Component.text(expelled.getName() + " has been expelled.", NamedTextColor.RED));

            return CommandResult.SUCCESS;
        }

        return CommandResult.ERROR;
    }
}
