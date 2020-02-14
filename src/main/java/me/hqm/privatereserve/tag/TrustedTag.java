package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class TrustedTag extends PlayerTag {
    private TextComponent trusted;

    public TrustedTag() {
        trusted = new TextComponent("[");
        trusted.setColor(ChatColor.DARK_GRAY);
        TextComponent middle = new TextComponent("T");
        middle.setColor(ChatColor.DARK_AQUA);
        trusted.addExtra(middle);
        trusted.addExtra("]");
        trusted.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Trusted").color(ChatColor.DARK_AQUA).create()));
        trusted.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/memberhelp TRUSTED"));
    }

    @Override
    public TextComponent getComponentFor(Player player) {
        return trusted;
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
