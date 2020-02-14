package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.DefaultPlayerTag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class AdminTag extends DefaultPlayerTag {
    public AdminTag() {
        super("admin-tag", "reservechat.admin", admin(), 5);
    }

    static TextComponent admin() {
        TextComponent admin = new TextComponent("[");
        admin.setColor(ChatColor.DARK_GRAY);
        TextComponent middle = new TextComponent("A");
        middle.setColor(ChatColor.DARK_RED);
        admin.addExtra(middle);
        admin.addExtra("]");
        admin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Visiting").color(ChatColor.GREEN).create()));
        admin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Administrator").
                color(net.md_5.bungee.api.ChatColor.DARK_RED).create()));
        return admin;
    }
}
