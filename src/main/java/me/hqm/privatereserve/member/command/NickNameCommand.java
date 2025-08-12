package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.command.CommandResult;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.Member;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Optional;

public class NickNameCommand {
    private NickNameCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralCommandNode<CommandSourceStack> setNode = Commands.literal("set")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(NickNameCommand::runSetSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires(ctx -> canRun(ctx, "privatereserve.admin"))
                                .executes(NickNameCommand::runSetTarget)))
                .build();

        return Commands.literal("nickname")
                .requires(NickNameCommand::canRun)
                .then(Commands.argument("name", StringArgumentType.word()).redirect(setNode))
                .then(setNode)
                .then(Commands.literal("clear")
                        .executes(NickNameCommand::runClearSelf)
                        .then(Commands.argument("player", StringArgumentType.word())
                                .requires(ctx -> canRun(ctx, "privatereserve.admin"))
                                .executes(NickNameCommand::runClearTarget)))
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

    private static boolean isValidNick(String s) {
        return s != null && !s.contains("[") && !s.contains("]") && s.length() <= 25;
    }

    private static void setNickName(Member model, String nickName) {
        model.setNickName(nickName);
        model.buildNameTag();
    }

    private static void clearNickName(Member model, String defaultName) {
        model.setNickName(defaultName);
        model.buildNameTag();
    }

    private static int runSetSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String nick = StringArgumentType.getString(ctx, "name");
        Optional<Member> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        if (!isValidNick(nick)) {
            player.sendMessage(Component.text("That is not an allowed nickname. Please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        setNickName(maybe.get(), nick);
        player.sendMessage(Component.text("Nickname set.", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetTarget(CommandContext<CommandSourceStack> ctx) {
        Player executor = (Player) ctx.getSource().getSender();
        String nick = StringArgumentType.getString(ctx, "name");
        String targetName = StringArgumentType.getString(ctx, "player");
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            executor.sendMessage(Component.text("That player does not exist, please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        if (!isValidNick(nick)) {
            executor.sendMessage(Component.text("Nickname is invalid or too long, please try again.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        Optional<Member> maybe = Members.data().fromPlayer(target);
        if (maybe.isEmpty()) {
            executor.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        setNickName(maybe.get(), nick);
        executor.sendMessage(Component.text("Nickname set for " + target.getName(), NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int runClearSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        Optional<Member> maybe = Members.data().fromId(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        clearNickName(maybe.get(), player.getName());
        player.sendMessage(Component.text("Nickname cleared.", NamedTextColor.GREEN));
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
        Optional<Member> maybe = Members.data().fromPlayer(target);
        if (maybe.isEmpty()) {
            executor.sendMessage(Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        clearNickName(maybe.get(), target.getName());
        executor.sendMessage(Component.text("Nickname cleared for " + target.getName(), NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
