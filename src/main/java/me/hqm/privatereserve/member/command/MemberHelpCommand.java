package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class MemberHelpCommand {
    private MemberHelpCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("memberhelp")
                .then(Commands.argument("topic", StringArgumentType.word())
                        .suggests(MemberHelpCommand::suggestTopics)
                        .executes(MemberHelpCommand::runHelp))
                .build();
    }

    private static CompletableFuture<Suggestions> suggestTopics(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder b) {
        String r = b.getRemainingLowerCase();
        if ("trusted".startsWith(r)) b.suggest("trusted");
        if ("visiting".startsWith(r)) b.suggest("visiting");
        if ("admin".startsWith(r)) b.suggest("admin");
        return b.buildFuture();
    }

    private static int runHelp(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        String topic = StringArgumentType.getString(ctx, "topic").toLowerCase();
        switch (topic) {
            case "trusted":
                sender.sendMessage(Component.text("Trusted", NamedTextColor.DARK_AQUA)
                        .append(Component.text(" players are trusted members of the community.", NamedTextColor.YELLOW)));
                sender.sendMessage(Component.text("Once a member is trusted, they can invite new members.", NamedTextColor.YELLOW));
                break;
            case "visiting":
                sender.sendMessage(Component.text("Visiting", NamedTextColor.GREEN)
                        .append(Component.text(" players are cannot leave their spawn.", NamedTextColor.YELLOW)));
                sender.sendMessage(Component.text("Players need to be invited to play on this server.", NamedTextColor.YELLOW));
                break;
            case "admin":
                sender.sendMessage(Component.text("Admins", NamedTextColor.DARK_RED)
                        .append(Component.text(" are staff who administrate the entire server.", NamedTextColor.YELLOW)));
                sender.sendMessage(Component.text("Development and server maintenance are handled by them too.", NamedTextColor.YELLOW));
                break;
            default:
                sender.sendMessage(Component.text("Unknown topic. Try: trusted, visiting, admin", NamedTextColor.RED));
                break;
        }
        return Command.SINGLE_SUCCESS;
    }
}

