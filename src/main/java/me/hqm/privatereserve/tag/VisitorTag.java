package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.PrivateReserve;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class VisitorTag extends PlayerTag {
    private Component visitor;

    public VisitorTag() {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text("[", NamedTextColor.DARK_GRAY));
        builder.append(Component.text("V", NamedTextColor.GREEN));
        builder.append(Component.text("]", NamedTextColor.DARK_GRAY));
        builder.hoverEvent(HoverEvent.showText(Component.text("Visotor", NamedTextColor.GREEN)));
        builder.clickEvent(ClickEvent.runCommand("/memberhelp VISITING"));
        visitor = builder.build();
    }

    @Override
    public Component getComponentFor(Player player) {
        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(player.getUniqueId())) {
            return visitor;
        }
        return ChatTag.EMPTY;
    }

    @Override
    public String getName() {
        return "visitor";
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
