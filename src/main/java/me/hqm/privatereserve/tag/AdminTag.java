package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.DefaultPlayerTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class AdminTag extends DefaultPlayerTag {
    public AdminTag() {
        super("admin-tag", "reservechat.admin", admin(), 5);
    }

    static Component admin() {
        ComponentBuilder<TextComponent, TextComponent.Builder> admin = Component.text();
        admin.append(Component.text("[", NamedTextColor.DARK_GRAY));
        admin.append(Component.text("A", NamedTextColor.DARK_RED));
        admin.append(Component.text("]", NamedTextColor.DARK_GRAY));
        admin.hoverEvent(HoverEvent.showText(Component.text("Administrator", NamedTextColor.DARK_RED)));
        admin.clickEvent(ClickEvent.runCommand("/memberhelp ADMIN"));
        return admin.build();
    }
}
