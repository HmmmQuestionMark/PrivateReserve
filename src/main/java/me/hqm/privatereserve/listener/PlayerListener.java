package me.hqm.privatereserve.listener;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.PlayerModel;
import me.hqm.privatereserve.util.RegionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.Duration;
import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PrivateReserve.PLAYER_R.isVisitor(player.getUniqueId())) {
            Optional<PlayerModel> maybeThem = PrivateReserve.PLAYER_R.fromName(player.getName());
            if (maybeThem.isPresent()) {
                PrivateReserve.PLAYER_R.remove(maybeThem.get().getKey());
                PrivateReserve.PLAYER_R.invite(player, maybeThem.get().getInvitedFrom());
                player.teleport(RegionUtil.spawnLocation());
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Celebrate!", NamedTextColor.YELLOW), Component.text("You were invited! Have fun!", NamedTextColor.GREEN), times);
                player.showTitle(title);
                return;
            }
            if (player.hasPermission("privatereserve.admin") || player.isWhitelisted()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateReserve.PLUGIN, () -> {
                    PrivateReserve.PLAYER_R.inviteSelf(player);
                    player.kick(Component.text("Sorry, had reconfigure your permissions. Please rejoin.", NamedTextColor.GREEN));
                }, 20);
                return;
            }
            if (!RegionUtil.visitingContains(player.getLocation())) {
                try {
                    player.teleport(RegionUtil.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
            player.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW).
                    append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
                    append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
        } else {
            Optional<PlayerModel> maybeThem = PrivateReserve.PLAYER_R.fromPlayer(event.getPlayer());
            if (maybeThem.isPresent()) {
                PlayerModel model = maybeThem.get();
                model.setLastKnownName(event.getPlayer().getName());
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
        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(player.getUniqueId())) {
            if (!RegionUtil.visitingContains(event.getTo())) {
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Sorry!", NamedTextColor.RED), Component.text("Only invited members are allowed there.", NamedTextColor.RED), times);
                player.showTitle(title);
                try {
                    player.teleport(RegionUtil.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }
}
