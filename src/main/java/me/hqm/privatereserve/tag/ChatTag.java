package me.hqm.privatereserve.tag;

import net.kyori.adventure.text.Component;

public class ChatTag {

    public static final Component NEW_LINE = Component.newline();
    public static final Component EMPTY = Component.empty();

    public static final AdminTag ADMIN_TAG = new AdminTag();
    public static final TrustedTag TRUSTED_TAG = new TrustedTag();
    public static final AlternateTag ALTERNATE_TAG = new AlternateTag();
    public static final VisitorTag VISITOR_TAG = new VisitorTag();
    public static final WorldTypeTag WORLD_TYPE_TAG = new WorldTypeTag();
    public static final ReserveChatNameTag NAME_TAG = new ReserveChatNameTag();
}
