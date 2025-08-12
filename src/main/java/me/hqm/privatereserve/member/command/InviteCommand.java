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
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

public class InviteCommand {
    private InviteCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("invite")
                // Allow anyone (player or console) to enter; trust/visitor checks are handled in execute to match legacy behavior
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(InviteCommand::runInvite)
                        .then(Commands.argument("primary", StringArgumentType.word())
                                .executes(InviteCommand::runInviteWithPrimary)))
                .build();
    }

    private static int runInvite(CommandContext<CommandSourceStack> ctx) {
        return doInvite(ctx, /*withPrimary*/ false);
    }

    private static int runInviteWithPrimary(CommandContext<CommandSourceStack> ctx) {
        return doInvite(ctx, /*withPrimary*/ true);
    }

    private static int doInvite(CommandContext<CommandSourceStack> ctx, boolean withPrimary) {
        var sender = ctx.getSource().getSender();
        String targetName = StringArgumentType.getString(ctx, "player");
        String primaryName = withPrimary ? StringArgumentType.getString(ctx, "primary") : null;

        OfflinePlayer invitee = Bukkit.getOfflinePlayer(targetName);

        // Already invited
        if (!Members.data().isVisitor(invitee.getUniqueId())) {
            send(sender, Component.text("That player is already invited.", NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }

        // If previously expelled, un-expel and record inviter; bypass trust check (legacy behavior)
        if (Members.data().isExpelled(invitee.getUniqueId())) {
            send(sender, Component.text("That player was previously expelled, please be cautious of them.", NamedTextColor.RED));
            Optional<MemberDocument> opModel = Members.data().fromPlayer(invitee);
            opModel.ifPresent(expelled -> {
                expelled.setExpelled(false);
                if (sender instanceof ConsoleCommandSender) {
                    expelled.setInvitedFrom("CONSOLE");
                } else if (sender instanceof Player p) {
                    expelled.setInvitedFrom(p.getUniqueId().toString());
                }
            });
        } else {
            // Not expelled: enforce trusted player rule when sender is a player
            if (sender instanceof Player p && !Members.data().isTrusted(p.getUniqueId())) {
                send(sender, Component.text("Sorry, you aren't (yet) a trusted player.", NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
        }

        // Perform invite
        if (sender instanceof ConsoleCommandSender) {
            Members.data().inviteConsole(invitee);
        } else if (sender instanceof Player p) {
            if (withPrimary) {
                Optional<MemberDocument> primary = Members.data().fromName(primaryName);
                if (primary.isEmpty()) {
                    send(sender, Component.text("The provided primary account does not exist.", NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                Members.data().invite(invitee, p, primary.get().getId());
            } else {
                Members.data().invite(invitee, p);
            }
        }

        // Let the invitee know
        if (invitee.isOnline()) {
            invitee.getPlayer().teleport(Regions.spawnLocation());
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
            Title title = Title.title(Component.text("Celebrate!", NamedTextColor.YELLOW),
                    Component.text("You were invited! Have fun!", NamedTextColor.GREEN), times);
            invitee.getPlayer().showTitle(title);
        }

        // Success message
        send(sender, Component.text(invitee.getName() + " has been invited.", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static void send(Object sender, Component message) {
        if (sender instanceof Player p) p.sendMessage(message);
        else Bukkit.getConsoleSender().sendMessage(message);
    }
}
