package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.MemberDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class TrustCommand {
    private TrustCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("trust")
                .requires(stack ->
                        (stack.getSender() instanceof Player p) && p.hasPermission("privatereserve.admin"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(TrustCommand::runTrust))
                .build();
    }

    private static int runTrust(CommandContext<CommandSourceStack> ctx) {
        Object sender = ctx.getSource().getSender();
        String name = StringArgumentType.getString(ctx, "player");

        Optional<MemberDocument> model = Members.data().fromName(name);
        if (model.isEmpty()) {
            send(sender, Component.text("Player is still a visitor, please try again later.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        MemberDocument doc = model.get();
        if (doc.isExpelled()) {
            send(sender, Component.text("Player is expelled, please try a different name.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        doc.setTrusted(true);
        var invitee = doc.getOfflinePlayer();
        if (invitee.isOnline()) {
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
            Title title = Title.title(Component.text("Celebrate!", NamedTextColor.YELLOW),
                    Component.text("You are now trusted!", NamedTextColor.GREEN), times);
            invitee.getPlayer().showTitle(title);
        }
        send(sender, Component.text(invitee.getName() + " has been trusted.", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static void send(Object sender, Component message) {
        if (sender instanceof Player p) p.sendMessage(message);
        else org.bukkit.Bukkit.getConsoleSender().sendMessage(message);
    }
}
