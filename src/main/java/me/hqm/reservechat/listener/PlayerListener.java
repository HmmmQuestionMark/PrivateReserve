package me.hqm.reservechat.listener;

import me.hqm.reservechat.ReserveChat;
import me.hqm.reservechat.model.PlayerModel;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerModel model = ReserveChat.PLAYER_R.fromPlayer(event.getPlayer());
        model.buildNameTag();
    }
}
