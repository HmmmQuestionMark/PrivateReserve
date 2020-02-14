package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.PrivateReserve;
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
        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(tagSource)) {
            return new TextComponent(TextComponent.fromLegacyText(tagSource.getName()));
        }
        return PrivateReserve.PLAYER_R.fromPlayer(tagSource).get().getNameTag();
    }

    @Override
    public int getPriority() {
        return 999;
    }
}
