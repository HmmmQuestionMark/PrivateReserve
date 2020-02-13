package me.hqm.reservechat.tag;

import com.demigodsrpg.chitchat.tag.DefaultPlayerTag;
import net.md_5.bungee.api.chat.*;

public class AdminTag extends DefaultPlayerTag {
    public AdminTag() {
        super("admin-tag", "reservechat.admin", admin(), 3);
    }

    static TextComponent admin() {
        TextComponent admin = new TextComponent("[A]");
        admin.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        admin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Administrator").
                color(net.md_5.bungee.api.ChatColor.DARK_RED).create()));
        return admin;
    }
}
