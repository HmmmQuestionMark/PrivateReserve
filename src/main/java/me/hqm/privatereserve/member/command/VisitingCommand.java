package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.member.region.Regions;
import me.hqm.privatereserve.task.TeleportTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class VisitingCommand {
    private VisitingCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("visiting")
                .requires(stack ->  stack.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    new TeleportTask(player, Regions.visitingLocation(), true, PlayerTeleportEvent.TeleportCause.COMMAND).runTaskLater(PrivateReserve.plugin(), 1);
                    player.sendMessage(Component.text("Warped to visiting spawn.", NamedTextColor.YELLOW));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
