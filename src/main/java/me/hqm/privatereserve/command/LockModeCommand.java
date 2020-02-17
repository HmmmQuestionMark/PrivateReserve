package me.hqm.privatereserve.command;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LockModeCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return CommandResult.PLAYER_ONLY;
        }
        UUID playerId = ((Player) sender).getUniqueId();

        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(playerId)) {
            return CommandResult.NO_PERMISSIONS;
        }

        if (toggleLockMode(playerId.toString())) {
            sender.sendMessage(ChatColor.YELLOW + "Locking is now enabled.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Locking is now disabled.");
        }

        return CommandResult.SUCCESS;
    }

    boolean toggleLockMode(String playerId) {
        if (PrivateReserve.RELATIONAL_R.contains(playerId, "NO-LOCK")) {
            PrivateReserve.RELATIONAL_R.remove(playerId, "NO-LOCK");
            return true;
        }
        PrivateReserve.RELATIONAL_R.put(playerId, "NO-LOCK", true);
        return false;
    }
}
