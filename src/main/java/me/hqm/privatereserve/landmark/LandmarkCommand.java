package me.hqm.privatereserve.landmark;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.entity.TeleportFlag;
import me.hqm.command.CommandResult;
import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.Settings;
import me.hqm.privatereserve.landmark.data.Landmark;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.Member;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LandmarkCommand {
    private static final Set<String> RESERVED_LANDMARK_NAMES = Set.of("go", "set", "list", "clear", "limit");

    private LandmarkCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralCommandNode<CommandSourceStack> goNode = Commands.literal("go")
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(LandmarkCommand::getLandmarkNameSuggestions)
                        .executes(LandmarkCommand::runLandmarkGo))
                .build();

        return Commands.literal("landmark")
                .requires(LandmarkCommand::canRun)
                .executes(LandmarkCommand::runLandmarkRoot)
                .then(Commands.argument("name", StringArgumentType.word())
                        .redirect(goNode))
                .then(goNode)
                .then(Commands.literal("list")
                        .executes(LandmarkCommand::runLandmarkList)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(LandmarkCommand::getLandmarkListSuggestions)
                                .executes(LandmarkCommand::runLandmarkList)))
                .then(Commands.literal("set")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(LandmarkCommand::runLandmarkSet)))
                .then(Commands.literal("clear")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests(LandmarkCommand::getLandmarkNameSuggestions)
                                .executes(LandmarkCommand::runLandmarkClear)))
                .then(Commands.literal("limit")
                        .requires(stack -> LandmarkCommand.canRun(stack, "privatereserve.admin"))
                        .then(Commands.argument("limit", IntegerArgumentType.integer(0))
                                .executes(LandmarkCommand::runLandmarkLimit)))
                .build();
    }

    private static boolean canRun(CommandSourceStack stack) {
        return canRun(stack, null);
    }

    private static boolean canRun(CommandSourceStack stack, @Nullable String permission) {
        if (!(stack.getSender() instanceof Player player)) {
            CommandResult.PLAYER_ONLY.send(stack.getSender());
            return false;
        }

        if (Members.data().isVisitorOrExpelled(player.getUniqueId())) {
            player.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW).
                    append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
                    append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
            return false;
        }

        if (permission != null && !player.hasPermission(permission)) {
            CommandResult.NO_PERMISSIONS.send(player);
            return false;
        }

        return true;
    }

    private static int runLandmarkRoot(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Map<String, Integer> playerList = Landmarks.data().landmarkOwnerNames();
        if (playerList.isEmpty()) {
            player.sendMessage(Component.text("Nobody's made a landmark yet...", NamedTextColor.YELLOW));
        } else {
            player.sendMessage(Component.text("List of players with landmarks (max " + Settings.LANDMARK_LIMIT.getInteger() + " each):", NamedTextColor.YELLOW).decorate(TextDecoration.UNDERLINED));
            for (Map.Entry<String, Integer> entry : playerList.entrySet()) {
                player.sendMessage(Component.text(" - " + entry.getKey() + " :: ", NamedTextColor.YELLOW).
                        append(Component.text(entry.getValue(), NamedTextColor.GREEN)).
                        append(Component.text(" landmarks.", NamedTextColor.YELLOW)));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getLandmarkListSuggestions(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Landmarks.data().landmarkOwnerNames().keySet().stream()
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static int runLandmarkList(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Member> maybe = Members.data().fromName(StringArgumentType.getString(ctx, "player"));
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("That player is still a visitor or does not exist.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        } else if (maybe.get().isExpelled()) {
            player.sendMessage(Component.text("That player is expelled, please try a different name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Member owner = maybe.get();
        List<Landmark> landmarks = Landmarks.data().fromOwner(owner);
        if (landmarks.isEmpty()) {
            player.sendMessage(owner.getNickName().append(Component.text(" hasn't made a landmark yet...", NamedTextColor.YELLOW)));
        } else {
            player.sendMessage(owner.getNickName().decorate(TextDecoration.BOLD).
                    append(Component.text("'s' landmarks:", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
            for (Landmark landmark : landmarks) {
                Location location = landmark.getLocation();
                player.sendMessage(Component.text(" - ", NamedTextColor.YELLOW).
                        append(Component.text(landmark.getName(), NamedTextColor.GREEN)).
                        append(Component.text(" :: Location: ", NamedTextColor.YELLOW)).
                        append(Component.text(Locations.prettyLocation(location), NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC)));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int runLandmarkGo(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Landmark> maybe = Landmarks.data().fromName(StringArgumentType.getString(ctx, "name"));
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("That landmark does not exist.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Landmark landmark = maybe.get();
        Location location = landmark.getLocation();
        if (location != null) {
            player.teleportAsync(
                    location,
                    PlayerTeleportEvent.TeleportCause.COMMAND,
                    TeleportFlag.EntityState.RETAIN_VEHICLE
            );
            player.sendMessage(
                    Component.text("Warped to ", NamedTextColor.YELLOW).
                            append(Component.text(landmark.getName(), NamedTextColor.GREEN)).
                            append(Component.text(".", NamedTextColor.YELLOW))
            );
        } else {
            player.sendMessage(Component.text("That landmark is corrupted.", NamedTextColor.RED));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int runLandmarkSet(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Member> maybe = Members.data().fromId((player.getUniqueId()));
        if (Settings.LANDMARK_LIMIT.getInteger() <= Landmarks.data().landmarksOwned(maybe.get())) {
            player.sendMessage(Component.text("You've hit the landmark limit (max " + Settings.LANDMARK_LIMIT.getInteger() + ")!", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        String name = StringArgumentType.getString(ctx, "name");
        if (RESERVED_LANDMARK_NAMES.contains(name.toLowerCase())) {
            player.sendMessage(Component.text("You can't set a landmark by that name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        Optional<Landmark> maybeMark = Landmarks.data().fromName(name);
        if (maybeMark.isPresent()) {
            player.sendMessage(Component.text("That landmark already exists.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        Landmark landmark = new Landmark(name, player.getLocation(), player.getUniqueId().toString());
        player.sendMessage(Component.text("Landmark ", NamedTextColor.YELLOW).
                append(Component.text(landmark.getName(), NamedTextColor.GREEN)).
                append(Component.text(" created!", NamedTextColor.YELLOW)));

        return Command.SINGLE_SUCCESS;
    }

    private static int runLandmarkClear(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String name = StringArgumentType.getString(ctx, "name");
        Optional<Landmark> maybe = Landmarks.data().fromName(name);
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("That landmark does not exist.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        Landmark landmark = maybe.get();
        if (landmark.isOwnerOrAdmin(player)) {
            Landmarks.data().remove(landmark.getId());
            player.sendMessage(Component.text("Cleared landmark.", NamedTextColor.YELLOW));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int runLandmarkLimit(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        int limit = IntegerArgumentType.getInteger(ctx, "limit");
        Settings.set(Settings.LANDMARK_LIMIT.getPath(), limit);
        player.sendMessage(Component.text("Landmark limit set to " + limit + ".", NamedTextColor.YELLOW));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getLandmarkNameSuggestions(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Landmarks.data().landmarkNames().stream()
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
