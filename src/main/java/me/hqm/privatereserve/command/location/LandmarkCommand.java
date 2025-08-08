package me.hqm.privatereserve.command.location;

import com.demigodsrpg.command.BaseCommand;
import com.demigodsrpg.command.CommandResult;
import com.demigodsrpg.util.LocationUtil;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.Setting;
import me.hqm.privatereserve.model.LandmarkModel;
import me.hqm.privatereserve.model.PlayerModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LandmarkCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {

        // /landmark <go|set|clear> <landmark>

        if (command.getName().equals("landmark")) {
            if (sender instanceof ConsoleCommandSender) {
                return CommandResult.PLAYER_ONLY;
            }

            if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(((Player) sender).getUniqueId())) {
                sender.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW).
                        append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
                        append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
                return CommandResult.QUIET_ERROR;
            }

            String whatDo = "LIST";
            String landmarkName = "";
            switch (args.length) {
                case 0: {
                    break;
                }
                case 1: {
                    if (!args[0].equalsIgnoreCase("LIST")) {
                        whatDo = "GO";
                        landmarkName = args[0];
                        break;
                    }
                    break;
                }
                default: {
                    whatDo = args[0].toUpperCase();
                    landmarkName = args[1];
                    if (landmarkName.equalsIgnoreCase("GO") || landmarkName.equalsIgnoreCase("LIST")) {
                        return CommandResult.INVALID_SYNTAX;
                    }
                }
            }

            switch (whatDo) {
                case "LIST": {
                    if (args.length > 1) {
                        Optional<PlayerModel> maybe = PrivateReserve.PLAYER_R.fromName(landmarkName);
                        if (!maybe.isPresent()) {
                            sender.sendMessage(Component.text("That player is still a visitor or does not exist.", NamedTextColor.RED));
                            return CommandResult.QUIET_ERROR;
                        } else if (maybe.get().isExpelled()) {
                            sender.sendMessage(Component.text("That player is expelled, please try a different name.", NamedTextColor.RED));
                            return CommandResult.QUIET_ERROR;
                        }
                        return listLandmarks((Player) sender, maybe.get());
                    }
                    return listPlayers(sender);
                }
                case "GO": {
                    return goLandmark(sender, landmarkName);
                }
                case "SET": {
                    Optional<PlayerModel> maybe = PrivateReserve.PLAYER_R.fromId(((Player) sender).getUniqueId());
                    if(Setting.LANDMARK_LIMIT <= PrivateReserve.LANDMARK_R.landmarksOwned(maybe.get())) {
                        sender.sendMessage(Component.text("You've hit the landmark limit (max " + Setting.LANDMARK_LIMIT + ")!", NamedTextColor.RED));
                        return CommandResult.QUIET_ERROR;
                    }
                    return setLandmark((Player) sender, landmarkName);
                }
                case "CLEAR": {
                    return clearLandmark((Player) sender, landmarkName);
                }
            }
        }
        return CommandResult.INVALID_SYNTAX;
    }

    private CommandResult listPlayers(CommandSender sender) {
        Map<String, Integer> playerList = PrivateReserve.LANDMARK_R.landmarkOwnerNames();
        if (playerList.isEmpty()) {
            sender.sendMessage(Component.text("Nobody's made a landmark yet...", NamedTextColor.YELLOW));
        } else {
            sender.sendMessage(Component.text("List of players with landmarks (max " + Setting.LANDMARK_LIMIT + " each):", NamedTextColor.YELLOW).decorate(TextDecoration.UNDERLINED));
            for (Map.Entry<String, Integer> entry : playerList.entrySet()) {
                sender.sendMessage(Component.text(" - " + entry.getKey() + " :: ", NamedTextColor.YELLOW).
                        append(Component.text(entry.getValue(), NamedTextColor.GREEN)).
                        append(Component.text(" landmarks.", NamedTextColor.YELLOW)));
            }
        }
        return CommandResult.SUCCESS;
    }

    private CommandResult listLandmarks(Player sender, PlayerModel owner) {
        List<LandmarkModel> landmarks = PrivateReserve.LANDMARK_R.fromOwner(owner);
        if (landmarks.isEmpty()) {
            sender.sendMessage(owner.getNickName().append(Component.text(" hasn't made a landmark yet...", NamedTextColor.YELLOW)));
        } else {
            sender.sendMessage(owner.getNickName().decorate(TextDecoration.BOLD).
                    append(Component.text("'s' landmarks:", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
            for (LandmarkModel landmark : landmarks) {
                Location location = landmark.getLocation();
                sender.sendMessage(Component.text(" - ", NamedTextColor.YELLOW).
                        append(Component.text(landmark.getName(), NamedTextColor.GREEN)).
                        append(Component.text(" :: Distance: ", NamedTextColor.YELLOW)).
                        append(Component.text(LocationUtil.calculateDistance(sender.getLocation(), location), NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC)));
            }
        }
        return CommandResult.SUCCESS;
    }

    private CommandResult goLandmark(CommandSender sender, String landMarkName) {
        Optional<LandmarkModel> maybe = PrivateReserve.LANDMARK_R.fromName(landMarkName);
        if (!maybe.isPresent()) {
            sender.sendMessage(Component.text("That landmark does not exist.", NamedTextColor.RED));
            return CommandResult.QUIET_ERROR;
        }
        LandmarkModel landmark = maybe.get();
        Location location = landmark.getLocation();
        if (location != null) {
            ((Player) sender).teleport(location);
            sender.sendMessage(Component.text("Warped to ", NamedTextColor.YELLOW).
                    append(Component.text(landmark.getName(), NamedTextColor.GREEN)).
                    append(Component.text(".", NamedTextColor.YELLOW)));
        } else {
            sender.sendMessage(Component.text("That landmark is corrupted.", NamedTextColor.RED));
            return CommandResult.QUIET_ERROR;
        }
        return CommandResult.SUCCESS;
    }

    private CommandResult setLandmark(Player sender, String name) {
        Optional<LandmarkModel> maybe = PrivateReserve.LANDMARK_R.fromName(name);
        if (maybe.isPresent()) {
            sender.sendMessage(Component.text("That landmark already exists.", NamedTextColor.RED));
            return CommandResult.QUIET_ERROR;
        }

        LandmarkModel landmark = new LandmarkModel(name, sender.getLocation(), sender.getUniqueId().toString());
        sender.sendMessage(Component.text("Landmark ", NamedTextColor.YELLOW).
                append(Component.text(landmark.getName(), NamedTextColor.GREEN)).
                append(Component.text(" created!", NamedTextColor.YELLOW)));

        return CommandResult.SUCCESS;
    }

    private CommandResult clearLandmark(Player sender, String landmarkName) {
        Optional<LandmarkModel> maybe = PrivateReserve.LANDMARK_R.fromName(landmarkName);
        if (!maybe.isPresent()) {
            sender.sendMessage(Component.text("That landmark does not exist.", NamedTextColor.RED));
            return CommandResult.QUIET_ERROR;
        }

        LandmarkModel landmark = maybe.get();
        if(landmark.isOwnerOrAdmin(sender)) {
            PrivateReserve.LANDMARK_R.remove(landmark.getKey());
            sender.sendMessage(Component.text("Cleared landmark.", NamedTextColor.YELLOW));
        }

        return CommandResult.SUCCESS;
    }
}
