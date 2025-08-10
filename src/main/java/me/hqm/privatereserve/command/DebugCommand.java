package me.hqm.privatereserve.command;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

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

        /*if (sender instanceof Player player && player.isInsideVehicle() &&
                player.getVehicle() instanceof HappyGhast ghast) {
            Optional<MobDeliveryModel> maybe = PrivateReserve.MOB_DELIVERY_R.fromEntity(ghast);
            MobDeliveryModel mob = maybe.orElseGet(() -> new MobDeliveryModel(ghast, "DEBUG", player.getUniqueId().toString()));
            switch(args[0].toUpperCase()) {
                case "SETLOAD": {
                    mob.setLoadLocation(player.getLocation());
                    break;
                }
                case "SETUNLOAD": {
                    mob.setUnloadLocation(player.getLocation());
                    break;
                }
                case "SETHOME": {
                    mob.setHomeLocation(player.getLocation());
                    break;
                }
                case "START": {
                    mob.setActive(true);
                    double loadDistance = ghast.getLocation().distance(mob.getLoadLocation());
                    double unloadDistance = ghast.getLocation().distance(mob.getUnloadLocation());
                    if(loadDistance < unloadDistance) {
                        new GhastDeliveryMoveFromUnloadTask(mob).start();
                    } else {
                        new GhastDeliveryMoveFromLoadTask(mob).start();
                    }
                }
            }
        } else {
            PrivateReserve.MOB_DELIVERY_R.cancelAll();
        }*/

        return CommandResult.SUCCESS;
    }
}
