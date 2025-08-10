package me.hqm.privatereserve.member.command;

import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.member.region.Regions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("spawn")) {
            if (sender instanceof ConsoleCommandSender) {
                return CommandResult.PLAYER_ONLY;
            }
            if (_PrivateReserve.MEMBER_DATA.isVisitorOrExpelled(((Player) sender).getUniqueId())) {
                sender.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW).
                        append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
                        append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
                return CommandResult.QUIET_ERROR;
            }

            ((Player) sender).teleport(Regions.spawnLocation());
            sender.sendMessage(Component.text("Warped to spawn.", NamedTextColor.YELLOW));
        }
        return CommandResult.SUCCESS;
    }
}
