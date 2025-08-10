package me.hqm.privatereserve.member.command;

import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import me.hqm.privatereserve.member.region.Regions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class VisitingCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("visiting")) {
            if (sender instanceof ConsoleCommandSender) {
                return CommandResult.PLAYER_ONLY;
            }

            ((Player) sender).teleport(Regions.visitingLocation());
            sender.sendMessage(Component.text("Warped to visiting spawn.", NamedTextColor.YELLOW));
        }
        return CommandResult.SUCCESS;
    }
}
