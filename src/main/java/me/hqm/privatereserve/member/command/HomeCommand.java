package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.entity.TeleportFlag;
import me.hqm.command.CommandResult;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.Member;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HomeCommand {
    private HomeCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("home")
                .requires(HomeCommand::canRun)
                .executes(HomeCommand::runGoSelf)
                .then(Commands.literal("go")
                        .executes(HomeCommand::runGoSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires((ctx) -> canRun(ctx, "privatereserve.admin"))
                                .suggests(HomeCommand::getMemberSuggestions)
                                .executes(HomeCommand::runGoTarget)
                        )
                )
                .then(Commands.literal("set")
                        .executes(HomeCommand::runSetSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires((ctx) -> canRun(ctx, "privatereserve.admin"))
                                .suggests(HomeCommand::getMemberSuggestions)
                                .executes(HomeCommand::runSetTarget)
                        )
                )
                // /home clear [player]
                .then(Commands.literal("clear")
                        .executes(HomeCommand::runClearSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires((ctx) -> canRun(ctx, "privatereserve.admin"))
                                .suggests(HomeCommand::getMemberSuggestions)
                                .executes(HomeCommand::runClearTarget)
                        )
                )
                .build();
    }

    private static CompletableFuture<Suggestions> getMemberSuggestions(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Members.data().getPlayerNames().stream()
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
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
            player.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW)
                    .append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    .append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
            return false;
        }

        if (permission != null && !player.hasPermission(permission)) {
            CommandResult.NO_PERMISSIONS.send(player);
            return false;
        }

        return true;
    }

    private static int runGoSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Member> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        return goHome(player, maybe.get());
    }

    private static int runGoTarget(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String targetName = StringArgumentType.getString(ctx, "player");
        Optional<Member> maybe = Members.data().fromName(targetName);
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        } else if (maybe.get().isExpelled()) {
            player.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        return goHome(player, maybe.get());
    }

    private static int goHome(Player executor, Member homeOwner) {
        Location homeLoc = homeOwner.getHomeLoc();
        if (executor.getName().equals(homeOwner.getLastKnownName())) {
            if (homeLoc != null) {
                executor.teleportAsync(
                        homeLoc,
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        TeleportFlag.EntityState.RETAIN_VEHICLE
                );
                executor.sendMessage(Component.text("Warped home.", NamedTextColor.YELLOW));
            } else {
                executor.sendMessage(Component.text("You need to set a home first, silly.", NamedTextColor.RED));
            }
        } else {
            if (homeLoc != null) {
                executor.teleportAsync(
                        homeLoc,
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        TeleportFlag.EntityState.RETAIN_VEHICLE
                );
                executor.sendMessage(Component.text("Warped to ", NamedTextColor.YELLOW)
                        .append(homeOwner.getNameTag())
                        .append(Component.text("'s home.", NamedTextColor.YELLOW)));
            } else {
                executor.sendMessage(homeOwner.getNameTag()
                        .append(Component.text(" hasn't set a home yet.", NamedTextColor.RED)));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Member> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        return setHome(player, maybe.get(), player.getLocation());
    }

    private static int runSetTarget(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String targetName = StringArgumentType.getString(ctx, "player");
        Optional<Member> maybe = Members.data().fromName(targetName);
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        } else if (maybe.get().isExpelled()) {
            player.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        return setHome(player, maybe.get(), player.getLocation());
    }

    private static int setHome(Player executor, Member homeOwner, Location homeLoc) {
        if (executor.getName().equals(homeOwner.getLastKnownName())) {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(homeLoc);
                executor.sendMessage(Component.text("Set home.", NamedTextColor.YELLOW));
            } else {
                executor.sendMessage(Component.text("Invalid location.", NamedTextColor.RED));
            }
        } else {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(homeLoc);
                executor.sendMessage(Component.text("Set ", NamedTextColor.YELLOW)
                        .append(homeOwner.getNameTag())
                        .append(Component.text("'s home.", NamedTextColor.YELLOW)));
            } else {
                executor.sendMessage(Component.text("Invalid location.", NamedTextColor.RED));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    // ===== CLEAR =====
    private static int runClearSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Member> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        return clearHome(player, maybe.get());
    }

    private static int runClearTarget(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String targetName = StringArgumentType.getString(ctx, "player");
        Optional<Member> maybe = Members.data().fromName(targetName);
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        } else if (maybe.get().isExpelled()) {
            player.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        return clearHome(player, maybe.get());
    }

    private static int clearHome(Player executor, Member homeOwner) {
        Location homeLoc = homeOwner.getHomeLoc();
        if (executor.getName().equals(homeOwner.getLastKnownName())) {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(null);
                executor.sendMessage(Component.text("Cleared home.", NamedTextColor.YELLOW));
            } else {
                executor.sendMessage(Component.text("You need to set a home first, silly.", NamedTextColor.RED));
            }
        } else {
            if (homeLoc != null) {
                homeOwner.setHomeLoc(null);
                executor.sendMessage(Component.text("Cleared ", NamedTextColor.YELLOW)
                        .append(homeOwner.getNameTag())
                        .append(Component.text("'s home.", NamedTextColor.YELLOW)));
            } else {
                executor.sendMessage(homeOwner.getNameTag()
                        .append(Component.text(" hasn't set a home yet.", NamedTextColor.RED)));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
