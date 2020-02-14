package me.hqm.privatereserve.command.member;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.util.RegionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("spawn")) {
            if (sender instanceof ConsoleCommandSender) {
                return CommandResult.PLAYER_ONLY;
            }
            if (PrivateReserve.PLAYER_R.isVisitorOrExpelled((Player) sender)) {
                sender.sendMessage(ChatColor.YELLOW + "Currently you are just a " + ChatColor.GRAY + ChatColor.ITALIC +
                        "visitor" + ChatColor.YELLOW + ", ask for an invite on Discord!");
                return CommandResult.QUIET_ERROR;
            }

            ((Player) sender).teleport(RegionUtil.spawnLocation());
            sender.sendMessage(ChatColor.YELLOW + "Warped to spawn.");
        }
        return CommandResult.SUCCESS;
    }
}
