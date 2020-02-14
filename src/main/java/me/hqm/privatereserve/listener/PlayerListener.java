package me.hqm.privatereserve.listener;

import com.demigodsrpg.chitchat.Chitchat;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.util.RegionUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PrivateReserve.PLAYER_R.isVisitor(player)) {
            Optional<PlayerModel> maybeThem = PrivateReserve.PLAYER_R.fromName(player.getName());
            if (maybeThem.isPresent()) {
                PrivateReserve.PLAYER_R.remove(maybeThem.get().getKey());
                PrivateReserve.PLAYER_R.invite(player, maybeThem.get().getInvitedFrom());
                player.teleport(RegionUtil.spawnLocation());
                Chitchat.sendTitle(player, 10, 80, 10,
                        ChatColor.YELLOW + "Celebrate!", ChatColor.GREEN + "You were invited! Have fun!");
                return;
            }
            if (player.hasPermission("privatereserve.admin") || player.isWhitelisted()) {
                PrivateReserve.PLAYER_R.inviteSelf(player);
                player.kickPlayer(ChatColor.GREEN + "Sorry, you weren't invited yet. Please rejoin.");
                return;
            }
            if (!RegionUtil.visitingContains(player.getLocation())) {
                try {
                    player.teleport(RegionUtil.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
            player.sendMessage(ChatColor.YELLOW + "Currently you are just a " + ChatColor.GRAY + ChatColor.ITALIC +
                    "visitor" + ChatColor.YELLOW + ", ask for an invite on Discord!");
        } else {
            Optional<PlayerModel> maybeThem = PrivateReserve.PLAYER_R.fromPlayer(player);
            if (maybeThem.isPresent()) {
                PlayerModel model = maybeThem.get();
                model.setLastKnownName(player.getName());
                model.buildNameTag();
            }
            if (RegionUtil.spawnContains(player.getLocation()) || RegionUtil.visitingContains(player.getLocation())) {
                try {
                    player.teleport(RegionUtil.spawnLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(player)) {
            if (!RegionUtil.visitingContains(event.getTo())) {
                Chitchat.sendTitle(player, 10, 60, 10, ChatColor.GREEN + "Sorry!",
                        ChatColor.RED + "Only invited members are allowed there.");
                try {
                    player.teleport(RegionUtil.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }
}
