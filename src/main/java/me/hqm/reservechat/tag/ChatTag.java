package me.hqm.reservechat.tag;


import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatTag {

    public static final TextComponent NEW_LINE = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));

    public static final AdminTag ADMIN_TAG = new AdminTag();
    public static final ReserveChatNameTag NAME_TAG = new ReserveChatNameTag();
}
