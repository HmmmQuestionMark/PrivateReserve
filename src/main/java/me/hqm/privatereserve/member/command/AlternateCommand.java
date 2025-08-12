package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.Member;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AlternateCommand {
    private static final String ADMIN_PERM = "privatereserve.admin";

    private AlternateCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("alternate")
                .requires(stack ->
                        !(stack.getSender() instanceof Player p) || p.hasPermission(ADMIN_PERM))
                .then(Commands.argument("player", StringArgumentType.word())
                        .then(Commands.argument("primary", StringArgumentType.word())
                                .executes(AlternateCommand::runAlternate)))
                .build();
    }

    private static int runAlternate(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        String playerName = StringArgumentType.getString(ctx, "player");
        String primaryName = StringArgumentType.getString(ctx, "primary");

        Optional<Member> model = Members.data().fromName(playerName);
        if (model.isEmpty()) {
            // If the alt doesn't exist yet, run the invite with primary link
            Bukkit.getServer().dispatchCommand(sender, "invite " + playerName + " " + primaryName);
            return Command.SINGLE_SUCCESS;
        } else if (model.get().isExpelled()) {
            sender.sendMessage(Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        Optional<Member> primary = Members.data().fromName(primaryName);
        if (primary.isPresent()) {
            model.get().setPrimaryAccount(primary.get().getId());
        } else {
            sender.sendMessage(Component.text("The provided primary account does not exist.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        sender.sendMessage(Component.text(model.get().getLastKnownName() + " has been set as an alternate account.", NamedTextColor.YELLOW));
        return Command.SINGLE_SUCCESS;
    }
}
