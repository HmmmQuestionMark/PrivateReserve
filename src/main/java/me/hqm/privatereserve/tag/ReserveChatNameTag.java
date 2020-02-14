package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.ReserveChat;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ReserveChatNameTag extends PlayerTag {
    // -- GETTERS -- //

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public TextComponent getComponentFor(Player tagSource) {
        return ReserveChat.PLAYER_R.fromPlayer(tagSource).getNameTag();
    }

    @Override
    public int getPriority() {
        return 999;
    }
}
