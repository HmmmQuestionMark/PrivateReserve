package me.hqm.privatereserve.command;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.dungeon.mob.DungeonMobs;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class DebugCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return CommandResult.PLAYER_ONLY;
        }
        if (!sender.hasPermission("privatereserve.admin")) {
            return CommandResult.NO_PERMISSIONS;
        }
        if (args.length < 1) {
            return CommandResult.INVALID_SYNTAX;
        }
        String select = args[0];
        Location location = ((Player) sender).getLocation();

        if (select.toLowerCase().startsWith("sk")) {
            DungeonMobs.spawnDungeonMob(location, DungeonMobs.SKELETOR);
            sender.sendMessage(ChatColor.YELLOW + "Skeletor has been spawned.");
        } else if (select.toLowerCase().startsWith("evil")) {
            DungeonMobs.spawnDungeonMob(location, DungeonMobs.EVIL_SQUID);
            sender.sendMessage(ChatColor.YELLOW + "Evil squid has been spawned.");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid option.");
            return CommandResult.QUIET_ERROR;
        }
        return CommandResult.SUCCESS;
    }
}
