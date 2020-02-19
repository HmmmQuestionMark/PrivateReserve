package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.PrivateReserve;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class AlternateTag extends PlayerTag {
    public static String DISPLAY_TAG = org.bukkit.ChatColor.DARK_GRAY + "[" + org.bukkit.ChatColor.GRAY +
            org.bukkit.ChatColor.ITALIC + "A" + org.bukkit.ChatColor.DARK_GRAY + "]";
    private TextComponent alternate;

    public AlternateTag() {
        alternate = new TextComponent("[");
        alternate.setColor(ChatColor.DARK_GRAY);
        TextComponent middle = new TextComponent("A");
        middle.setColor(ChatColor.GRAY);
        middle.setItalic(true);
        alternate.addExtra(middle);
        alternate.addExtra("]");
        alternate.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Alt Account").color(ChatColor.GRAY).create()));
    }

    @Override
    public TextComponent getComponentFor(Player player) {
        if (PrivateReserve.PLAYER_R.isAlternate(player.getUniqueId())) {
            return alternate;
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
