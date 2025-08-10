package me.hqm.privatereserve.chat;

import com.demigodsrpg.chitchat.NameTag;
import me.hqm.privatereserve.member.Members;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class MemberNameTag extends NameTag {
    // -- GETTERS -- //

    @Override
    public Component getComponentFor(Player tagSource) {
        if (Members.data().isVisitorOrExpelled(tagSource.getUniqueId())) {
            return PlainTextComponentSerializer.plainText().deserialize(tagSource.getName());
        }
        return Members.data().fromPlayer(tagSource).get().getNameTag();
    }
}
