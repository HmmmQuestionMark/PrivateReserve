package me.hqm.privatereserve.chat;

import com.demigodsrpg.chitchat.ChatTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class WorldTypeTag extends ChatTag {
    private final Component nether;
    private final Component the_end;

    public WorldTypeTag() {
        nether = ChatTags.buildComponent('N', "Nether", NamedTextColor.GOLD, null);
        the_end = ChatTags.buildComponent('E', "The End", NamedTextColor.DARK_PURPLE, null);
    }

    @Override
    public Component getComponentFor(Player player) {
        switch (player.getWorld().getEnvironment()) {
            case NETHER -> {
                return nether;
            }
            case THE_END -> {
                return the_end;
            }
            default -> {
                return Component.empty();
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
