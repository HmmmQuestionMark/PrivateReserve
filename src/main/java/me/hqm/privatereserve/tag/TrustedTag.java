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

public class TrustedTag extends PlayerTag {
    private Component trusted;

    public TrustedTag() {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text("[", NamedTextColor.DARK_GRAY));
        builder.append(Component.text("T", NamedTextColor.DARK_AQUA));
        builder.append(Component.text("]", NamedTextColor.DARK_GRAY));
        builder.hoverEvent(HoverEvent.showText(Component.text("Trusted", NamedTextColor.DARK_AQUA)));
        builder.clickEvent(ClickEvent.runCommand("/memberhelp TRUSTED"));
        trusted = builder.build();
    }

    @Override
    public Component getComponentFor(Player player) {
        if (!PrivateReserve.PLAYER_R.isAlternate(player.getUniqueId()) &&
                PrivateReserve.PLAYER_R.isTrusted(player.getUniqueId())) {
            return trusted;
        }
        return ChatTag.EMPTY;
    }

    @Override
    public String getName() {
        return "trusted";
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
