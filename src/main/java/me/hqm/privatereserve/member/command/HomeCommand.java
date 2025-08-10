package me.hqm.privatereserve.member.command;

import me.hqm.basecommand.BaseCommand;
import me.hqm.basecommand.CommandResult;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class HomeCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (command.getName().equals("home")) {
            if (sender instanceof ConsoleCommandSender) {
                return CommandResult.PLAYER_ONLY;
            }

            if (_PrivateReserve.MEMBER_DATA.isVisitorOrExpelled(((Player) sender).getUniqueId())) {
                sender.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW).
                        append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
                        append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
                return CommandResult.QUIET_ERROR;
            }

            MemberDocument target;
            if (args.length <= 1) {
                target = _PrivateReserve.MEMBER_DATA.fromPlayer((Player) sender).get();
            } else {
                Optional<MemberDocument> maybe = _PrivateReserve.MEMBER_DATA.fromName(args[1]);
                if (maybe.isEmpty()) {
                    sender.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
                    return CommandResult.QUIET_ERROR;
                } else if (maybe.get().isExpelled()) {
                    sender.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
                    return CommandResult.QUIET_ERROR;
                }
                target = maybe.get();
            }

            String whatDo = args.length < 1 ? "GO" : args[0].toUpperCase();
            switch (whatDo) {
                case "GO": {
                    return goHome(sender, target);
                }
                case "SET": {
                    Location senderLoc = ((Player) sender).getLocation();
                    return setHome(sender, target, senderLoc);
                }
                case "CLEAR": {
                    return clearHome(sender, target);
                }
            }
        }

        return CommandResult.INVALID_SYNTAX;
    }

    private CommandResult goHome(CommandSender sender, MemberDocument homeOwner) {
        if (sender.getName().equals(homeOwner.getLastKnownName())) {
            Location homeLoc = homeOwner.getHomeLoc();
            if (homeLoc != null) {
                ((Player) sender).teleport(homeLoc);
                sender.sendMessage(Component.text("Warped home.", NamedTextColor.YELLOW));
            } else {
                sender.sendMessage(Component.text("You need to set a home first, silly.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
        } else if (!sender.hasPermission("privatereserve.admin")) {
            return CommandResult.NO_PERMISSIONS;
        } else {
            Location homeLoc = homeOwner.getHomeLoc();
            if (homeLoc != null) {
                ((Player) sender).teleport(homeLoc);
                sender.sendMessage(Component.text("Warped to ", NamedTextColor.YELLOW).
                        append(homeOwner.getNameTag()).
                        append(Component.text("'s home.", NamedTextColor.YELLOW)));
            } else {
                sender.sendMessage(homeOwner.getNameTag().
                        append(Component.text(" hasn't set a home yet.", NamedTextColor.RED)));
                return CommandResult.QUIET_ERROR;
            }
        }
        return CommandResult.SUCCESS;
    }

    private CommandResult setHome(CommandSender sender, MemberDocument homeOwner, Location homeLoc) {
        if (sender.getName().equals(homeOwner.getLastKnownName())) {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(homeLoc);
                sender.sendMessage(Component.text("Set home.", NamedTextColor.YELLOW));
            } else {
                sender.sendMessage(Component.text("Invalid location.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
        } else if (!sender.hasPermission("privatereserve.admin")) {
            return CommandResult.NO_PERMISSIONS;
        } else {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(homeLoc);
                sender.sendMessage(Component.text("Set ", NamedTextColor.YELLOW).
                        append(homeOwner.getNameTag()).
                        append(Component.text("'s home.", NamedTextColor.YELLOW)));
            } else {
                sender.sendMessage(Component.text("Invalid location.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
        }
        return CommandResult.SUCCESS;
    }

    private CommandResult clearHome(CommandSender sender, MemberDocument homeOwner) {
        Location homeLoc = homeOwner.getHomeLoc();
        if (sender.getName().equals(homeOwner.getLastKnownName())) {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(null);
                sender.sendMessage(Component.text("Cleared home.", NamedTextColor.YELLOW));
            } else {
                sender.sendMessage(Component.text("You need to set a home first, silly.", NamedTextColor.RED));
                return CommandResult.QUIET_ERROR;
            }
        } else if (!sender.hasPermission("privatereserve.admin")) {
            return CommandResult.NO_PERMISSIONS;
        } else {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(null);
                sender.sendMessage(Component.text("Cleared ", NamedTextColor.YELLOW).
                        append(homeOwner.getNameTag()).
                        append(Component.text("'s home.", NamedTextColor.YELLOW)));
            } else {
                sender.sendMessage(homeOwner.getNameTag().
                        append(Component.text(" hasn't set a home yet.", NamedTextColor.RED)));
                return CommandResult.QUIET_ERROR;
            }
        }
        return CommandResult.SUCCESS;
    }
}
