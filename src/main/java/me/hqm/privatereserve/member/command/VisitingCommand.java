package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.command.CommandResult;
import me.hqm.privatereserve.member.region.Regions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class VisitingCommand {
    private VisitingCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("visiting")
                .requires(stack -> {
                    if (!(stack.getSender() instanceof Player)) {
                        CommandResult.PLAYER_ONLY.send(stack.getSender());
                        return false;
                    }
                    return true;
                })
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    player.teleport(Regions.visitingLocation());
                    player.sendMessage(Component.text("Warped to visiting spawn.", NamedTextColor.YELLOW));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
