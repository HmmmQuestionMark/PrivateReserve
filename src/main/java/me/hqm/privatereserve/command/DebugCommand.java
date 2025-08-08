package me.hqm.privatereserve.command;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.dungeon.mob.DungeonMobs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(Component.text("Skeletor has been spawned.", NamedTextColor.YELLOW));
        } else if (select.toLowerCase().startsWith("evil")) {
            DungeonMobs.spawnDungeonMob(location, DungeonMobs.EVIL_SQUID);
            sender.sendMessage(Component.text("Evil squid has been spawned.", NamedTextColor.YELLOW));
        } else {
            sender.sendMessage(Component.text("Not a valid option.", NamedTextColor.RED));
            return CommandResult.QUIET_ERROR;
        }
        return CommandResult.SUCCESS;
    }
}
