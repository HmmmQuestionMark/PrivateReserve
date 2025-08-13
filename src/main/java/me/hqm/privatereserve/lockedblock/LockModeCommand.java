package me.hqm.privatereserve.lockedblock;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.member.Members;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

public class LockModeCommand {
    private LockModeCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("lockmode")
                .requires(LockModeCommand::canRun)
                .executes(LockModeCommand::runToggleLogic)
                .build();
    }

    private static boolean canRun(CommandSourceStack stack) {
        return canRun(stack, null);
    }

    private static boolean canRun(CommandSourceStack stack, @Nullable String permission) {
        if (!(stack.getSender() instanceof Player player)) {
            return false;
        }

        if (Members.data().isVisitorOrExpelled(player.getUniqueId())) {
            player.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW)
                    .append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    .append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
            return false;
        }

        return permission == null || player.hasPermission(permission);
    }

    private static int runToggleLogic(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        boolean enabled = toggleLockMode(player);
        if (enabled) {
            player.sendMessage(Component.text("Locking is now enabled.", NamedTextColor.YELLOW));
        } else {
            player.sendMessage(Component.text("Locking is now disabled.", NamedTextColor.YELLOW));
        }
        return Command.SINGLE_SUCCESS;
    }

    static boolean toggleLockMode(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey nsk = new NamespacedKey(PrivateReserve.plugin().namespace(), "no_lock");
        if (dataContainer.has(nsk)) {
            dataContainer.remove(nsk);
            return true;
        }
        dataContainer.set(nsk, PersistentDataType.BOOLEAN, true);
        return false;
    }
}
