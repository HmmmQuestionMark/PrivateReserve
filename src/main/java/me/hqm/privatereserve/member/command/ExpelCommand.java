package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.MemberDocument;
import me.hqm.privatereserve.member.region.Regions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class ExpelCommand {
    private ExpelCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("expel")
                // Allow console. For players, block visitors/expelled from running admin commands at all.
                .requires(stack -> {
                    if (!(stack.getSender() instanceof Player p)) return false;
                    if (Members.data().isVisitorOrExpelled(p.getUniqueId())) {
                        p.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW)
                                .append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                                .append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
                        return false;
                    }
                    return true;
                })
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ExpelCommand::runExpel))
                .build();
    }

    private static int runExpel(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String name = StringArgumentType.getString(ctx, "player");

        Optional<MemberDocument> model = Members.data().fromName(name);
        if (model.isEmpty()) {
            player.sendMessage(Component.text("Player is still a visitor.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }
        MemberDocument doc = model.get();
        if (doc.isExpelled()) {
            player.sendMessage(Component.text("Player is already expelled.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        boolean isInviter = doc.getInvitedFrom() != null && doc.getInvitedFrom().equals(player.getUniqueId().toString());
        if (!isInviter && !player.hasPermission("privatereserve.admin")) {
            player.sendMessage(Component.text("Sorry, can't expel that person.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        doc.setExpelled(true);
        OfflinePlayer expelled = doc.getOfflinePlayer();
        if (expelled.isOnline()) {
            expelled.getPlayer().teleport(Regions.visitingLocation());
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
            Title title = Title.title(Component.text("Expelled.", NamedTextColor.RED),
                    Component.text("You were expelled, go away.", NamedTextColor.YELLOW), times);
            expelled.getPlayer().showTitle(title);
        }
        player.sendMessage(Component.text(expelled.getName() + " has been expelled.", NamedTextColor.RED));
        return Command.SINGLE_SUCCESS;
    }
}
