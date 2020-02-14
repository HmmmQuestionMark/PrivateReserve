package me.hqm.privatereserve.tag;


import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatTag {

    public static final TextComponent NEW_LINE = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));
    public static final TextComponent EMPTY = new TextComponent();

    public static final AdminTag ADMIN_TAG = new AdminTag();
    public static final TrustedTag TRUSTED_TAG = new TrustedTag();
    public static final VisitorTag VISITOR_TAG = new VisitorTag();
    public static final ReserveChatNameTag NAME_TAG = new ReserveChatNameTag();
}
