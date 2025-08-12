package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.command.CommandResult;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Optional;

public class PronounsCommand {
    private PronounsCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralCommandNode<CommandSourceStack> setNode = Commands.literal("set")
                .then(Commands.argument("pronouns", StringArgumentType.word())
                        .executes(PronounsCommand::runSetSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires(ctx -> canRun(ctx, "privatereserve.admin"))
                                .executes(PronounsCommand::runSetTarget)))
                .build();

        return Commands.literal("pronouns")
                .requires(PronounsCommand::canRun)
                .then(Commands.argument("pronouns", StringArgumentType.word()).redirect(setNode))
                .then(setNode)
                .then(Commands.literal("clear")
                        .executes(PronounsCommand::runClearSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires(ctx -> canRun(ctx, "privatereserve.admin"))
                                .executes(PronounsCommand::runClearTarget)))
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

    private static boolean isValidPronouns(String s) {
        return s != null && s.length() <= 16;
    }

    private static void applyPronouns(MemberDocument model, String pronouns) {
        model.setPronouns(pronouns);
        model.buildNameTag();
    }

    private static void clearPronouns(MemberDocument model) {
        model.setPronouns("");
        model.buildNameTag();
    }

    // ===== set =====
    private static int runSetSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String value = StringArgumentType.getString(ctx, "pronouns");
        Optional<MemberDocument> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        if (!isValidPronouns(value)) {
            player.sendMessage(Component.text("Pronouns are too long, please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        applyPronouns(maybe.get(), value);
        player.sendMessage(Component.text("Pronouns set.", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetTarget(CommandContext<CommandSourceStack> ctx) {
        Player executor = (Player) ctx.getSource().getSender();
        String value = StringArgumentType.getString(ctx, "pronouns");
        String targetName = StringArgumentType.getString(ctx, "player");
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            executor.sendMessage(Component.text("That player does not exist, please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        if (!isValidPronouns(value)) {
            executor.sendMessage(Component.text("Pronouns are too long, please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Optional<MemberDocument> maybe = Members.data().fromPlayer(target);
        if (maybe.isEmpty()) {
            executor.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        applyPronouns(maybe.get(), value);
        executor.sendMessage(Component.text("Pronouns set for " + target.getName(), NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    // ===== clear =====
    private static int runClearSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<MemberDocument> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        clearPronouns(maybe.get());
        player.sendMessage(Component.text("Pronouns cleared.", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int runClearTarget(CommandContext<CommandSourceStack> ctx) {
        Player executor = (Player) ctx.getSource().getSender();
        String targetName = StringArgumentType.getString(ctx, "player");
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            executor.sendMessage(Component.text("That player does not exist, please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Optional<MemberDocument> maybe = Members.data().fromPlayer(target);
        if (maybe.isEmpty()) {
            executor.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        clearPronouns(maybe.get());
        executor.sendMessage(Component.text("Pronouns cleared for " + target.getName(), NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
