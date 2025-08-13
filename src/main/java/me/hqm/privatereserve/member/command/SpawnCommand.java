package me.hqm.privatereserve.member.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.region.Regions;
import me.hqm.privatereserve.task.TeleportTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpawnCommand {
    private SpawnCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("spawn")
                .requires(stack -> {
                    if (!(stack.getSender() instanceof Player p)) {
                        return false;
                    }
                    if (Members.data().isVisitorOrExpelled(p.getUniqueId())) {
                        p.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW)
                                .append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                                .append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
                        return false;
                    }
                    return true;
                })
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    new TeleportTask(player, Regions.spawnLocation(), true, PlayerTeleportEvent.TeleportCause.COMMAND).runTaskLater(PrivateReserve.plugin(), 1);
                    player.sendMessage(Component.text("Warped to spawn.", NamedTextColor.YELLOW));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
