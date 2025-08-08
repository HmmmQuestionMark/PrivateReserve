package me.hqm.privatereserve.command.location;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import me.hqm.privatereserve.util.RegionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class VisitingCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("visiting")) {
            if (sender instanceof ConsoleCommandSender) {
                return CommandResult.PLAYER_ONLY;
            }

            ((Player) sender).teleport(RegionUtil.visitingLocation());
            sender.sendMessage(Component.text("Warped to visiting spawn.", NamedTextColor.YELLOW));
        }
        return CommandResult.SUCCESS;
    }
}
