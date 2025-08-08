package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.PrivateReserve;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class AlternateTag extends PlayerTag {
    public static Component DISPLAY_TAG = Component.text("[", NamedTextColor.DARK_GRAY).
            append(Component.text("A", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)).
            append(Component.text("]", NamedTextColor.DARK_GRAY)).
            hoverEvent(HoverEvent.showText(Component.text("Alt Account", NamedTextColor.GRAY)));

    @Override
    public Component getComponentFor(Player player) {
        if (PrivateReserve.PLAYER_R.isAlternate(player.getUniqueId())) {
            return DISPLAY_TAG;
        }
        return ChatTag.EMPTY;
    }

    @Override
    public String getName() {
        return "alternate";
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
