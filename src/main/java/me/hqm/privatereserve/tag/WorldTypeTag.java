package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class WorldTypeTag extends PlayerTag {
    private final Component nether;
    private final Component the_end;

    public WorldTypeTag() {
        nether = build('N', "Nether", NamedTextColor.GOLD);
        the_end = build('E', "The End", NamedTextColor.DARK_PURPLE);
    }

    private Component build(char initial, String text, TextColor color) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text("[", NamedTextColor.DARK_GRAY));
        builder.append(Component.text(initial, color));
        builder.append(Component.text("]", NamedTextColor.DARK_GRAY));
        builder.hoverEvent(HoverEvent.showText(Component.text(text, color)));
        return builder.build();
    }

    @Override
    public Component getComponentFor(Player player) {
        switch(player.getWorld().getEnvironment()) {
            case NETHER -> {
                return nether;
            }
            case THE_END -> {
                return the_end;
            }
            default ->  {
                return ChatTag.EMPTY;
            }
        }
    }

    @Override
    public String getName() {
        return "world_type";
    }

    @Override
    public int getPriority() {
        return 6;
    }
}
