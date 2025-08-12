package me.hqm.privatereserve.chat;

import com.demigodsrpg.chitchat.ChatTag;
import com.demigodsrpg.chitchat.PlayerTag;
import me.hqm.privatereserve.member.Members;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ChatTags {
    public static final PlayerTag ADMIN_TAG = buildTag("admin", "reservechat.admin", 5, 'A', "Admin", NamedTextColor.DARK_RED, "/memberhelp ADMIN");
    public static final PlayerTag TRUSTED_TAG = buildTag("trusted", (player -> Members.data().isTrusted(player.getUniqueId())), 2, 'T', "Trusted", NamedTextColor.DARK_AQUA, "/memberhelp TRUSTED");
    public static final PlayerTag VISITOR_TAG = buildTag("visitor", (player -> Members.data().isVisitor(player.getUniqueId())), 2, 'V', "Visitor", NamedTextColor.GREEN, "/memberhelp VISITING");
    public static final ChatTag WORLD_TYPE_TAG = new WorldTypeTag();
    public static final ChatTag NAME_TAG = new MemberNameTag();

    static Component buildComponent(char initial, String text, TextColor color, @Nullable String clickRun) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text("[", NamedTextColor.DARK_GRAY));
        builder.append(Component.text(initial, color));
        builder.append(Component.text("]", NamedTextColor.DARK_GRAY));
        builder.hoverEvent(HoverEvent.showText(Component.text(text, color)));
        if (clickRun != null) {
            builder.clickEvent(ClickEvent.runCommand(clickRun));
        }
        return builder.build();
    }

    static PlayerTag buildTag(String name, String permission, Component message, int priority) {
        return new PlayerTag(name, permission, message, priority);
    }

    static PlayerTag buildTag(String name, String permission, int priority, char initial, String text, TextColor color, @Nullable String clickRun) {
        return new PlayerTag(name, permission, buildComponent(initial, text, color, clickRun), priority);
    }

    static PlayerTag buildTag(String name, Predicate<Player> test, int priority, char initial, String text, TextColor color, @Nullable String clickRun) {
        return new PlayerTag(name, null, buildComponent(initial, text, color, clickRun), priority) {
            @Override
            public Component getComponentFor(Player tagSource) {
                return test.test(tagSource) ? getTagText() : null;
            }
        };
    }
}
