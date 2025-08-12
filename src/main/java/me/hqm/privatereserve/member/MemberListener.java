package me.hqm.privatereserve.member;

import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.member.data.Member;
import me.hqm.privatereserve.member.region.Regions;
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

public class MemberListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Members.data().isVisitor(player.getUniqueId())) {
            Optional<Member> maybeThem = Members.data().fromName(player.getName());
            if (maybeThem.isPresent()) {
                Members.data().remove(maybeThem.get().getId());
                Members.data().invite(player, maybeThem.get().getInvitedFrom());
                player.teleport(Regions.spawnLocation());
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Celebrate!", NamedTextColor.YELLOW), Component.text("You were invited! Have fun!", NamedTextColor.GREEN), times);
                player.showTitle(title);
                return;
            }
            if (player.hasPermission("privatereserve.admin") || player.isWhitelisted()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateReserve.plugin(), () -> {
                    Members.data().inviteSelf(player);
                    player.kick(Component.text("Sorry, had reconfigure your permissions. Please rejoin.", NamedTextColor.GREEN));
                }, 20);
                return;
            }
            if (!Regions.visitingContains(player.getLocation())) {
                try {
                    player.teleport(Regions.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
            player.sendMessage(Component.text("Currently you are just a ", NamedTextColor.YELLOW).
                    append(Component.text("visitor", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
                    append(Component.text(", ask for an invite on Discord!", NamedTextColor.YELLOW)));
        } else {
            Optional<Member> maybeThem = Members.data().fromPlayer(event.getPlayer());
            if (maybeThem.isPresent()) {
                Member model = maybeThem.get();
                model.setLastKnownName(event.getPlayer().getName());
                model.buildNameTag();
            }
            if (Regions.spawnContains(player.getLocation()) || Regions.visitingContains(player.getLocation())) {
                try {
                    player.teleport(Regions.spawnLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Members.data().isVisitorOrExpelled(player.getUniqueId())) {
            if (!Regions.visitingContains(event.getTo())) {
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
                Title title = Title.title(Component.text("Sorry!", NamedTextColor.RED), Component.text("Only invited members are allowed there.", NamedTextColor.RED), times);
                player.showTitle(title);
                try {
                    player.teleport(Regions.visitingLocation());
                } catch (NullPointerException oops) {
                    oops.printStackTrace();
                }
            }
        }
    }
}
