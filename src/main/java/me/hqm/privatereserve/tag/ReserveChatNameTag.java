package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.PrivateReserve;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class ReserveChatNameTag extends PlayerTag {
    // -- GETTERS -- //

    @Override
    public String getName() {
        return "name";
    }

    @Override
    public Component getComponentFor(Player tagSource) {
        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(tagSource.getUniqueId())) {
            return PlainTextComponentSerializer.plainText().deserialize(tagSource.getName());
        }
        return PrivateReserve.PLAYER_R.fromPlayer(tagSource).get().getNameTag();
    }

    @Override
    public int getPriority() {
        return 999;
    }
}
