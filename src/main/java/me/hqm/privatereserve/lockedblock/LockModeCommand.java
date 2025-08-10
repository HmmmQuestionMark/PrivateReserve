package me.hqm.privatereserve.lockedblock;

import me.hqm.privatereserve._PrivateReserve;
import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LockModeCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return CommandResult.PLAYER_ONLY;
        }
        UUID playerId = ((Player) sender).getUniqueId();

        if (_PrivateReserve.MEMBER_DATA.isVisitorOrExpelled(playerId)) {
            return CommandResult.NO_PERMISSIONS;
        }

        if (toggleLockMode(playerId.toString())) {
            sender.sendMessage(Component.text("Locking is now enabled.", NamedTextColor.YELLOW));
        } else {
            sender.sendMessage(Component.text("Locking is now disabled.", NamedTextColor.YELLOW));
        }

        return CommandResult.SUCCESS;
    }

    boolean toggleLockMode(String playerId) {
        if (_PrivateReserve.RELATIONAL_DATA.contains(playerId, "NO-LOCK")) {
            _PrivateReserve.RELATIONAL_DATA.remove(playerId, "NO-LOCK");
            return true;
        }
        _PrivateReserve.RELATIONAL_DATA.put(playerId, "NO-LOCK", true);
        return false;
    }
}
