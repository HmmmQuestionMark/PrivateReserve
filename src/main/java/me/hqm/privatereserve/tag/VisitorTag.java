package me.hqm.privatereserve.tag;

import com.demigodsrpg.chitchat.tag.PlayerTag;
import me.hqm.privatereserve.PrivateReserve;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

public class VisitorTag extends PlayerTag {
    private TextComponent visitor;

    public VisitorTag() {
        visitor = new TextComponent("[");
        visitor.setColor(ChatColor.DARK_GRAY);
        TextComponent middle = new TextComponent("V");
        middle.setColor(ChatColor.GREEN);
        visitor.addExtra(middle);
        visitor.addExtra("]");
        visitor.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Visitor").color(ChatColor.GREEN).create()));
        visitor.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/memberhelp VISITING"));
    }

    @Override
    public TextComponent getComponentFor(Player player) {
        if (PrivateReserve.PLAYER_R.isVisitorOrExpelled(player.getUniqueId())) {
            return visitor;
        }
        return ChatTag.EMPTY;
    }

    @Override
    public String getName() {
        return "visitor";
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
