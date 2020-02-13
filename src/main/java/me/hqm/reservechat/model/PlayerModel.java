package me.hqm.reservechat.model;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.reservechat.ReserveChat;
import me.hqm.reservechat.tag.ChatTag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerModel implements Model {
    private final String mojangId;
    private String lastKnownName;

    private long lastLoginTime;

    // -- INTERESTING DATA -- //

    private String nickName;
    private String pronouns;

    // -- NAME TAG TEXT -- //

    private transient TextComponent nameTagText;

    // -- CONSTRUCTORS -- //

    public PlayerModel(OfflinePlayer player) {
        mojangId = player.getUniqueId().toString();
        lastKnownName = player.getName();
        lastLoginTime = System.currentTimeMillis();
        nickName = lastKnownName;
        buildNameTag();
    }

    public PlayerModel (String mojangId, DataSection data) {
        this.mojangId = mojangId;
        lastKnownName = data.getString("last_known_name");
        lastLoginTime = data.getLong("last_login_time");
        nickName = data.getString("nickname");
        pronouns = data.isString("pronouns") ? data.getString("pronouns") : null;
        buildNameTag();
    }

    @Override
    public String getKey() {
        return mojangId;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("last_known_name", lastKnownName);
        data.put("last_login_time", lastLoginTime);
        data.put("nickname", nickName != null ? nickName : lastKnownName);
        if (pronouns != null) {
            data.put("pronouns", pronouns);
        }
        return data;
    }

    public void buildNameTag() {
        // Define blank component
        nameTagText = new TextComponent();

        // Build from legacy text
        for (BaseComponent component : TextComponent.fromLegacyText(
                org.bukkit.ChatColor.translateAlternateColorCodes('&', nickName))) {
            nameTagText.addExtra(component);
        }

        // Begin hover text
        TextComponent hover = new TextComponent();

        // Give last known username
        TextComponent username = new TextComponent(new ComponentBuilder("Username: " + lastKnownName).
                color(ChatColor.DARK_GRAY).create());
        hover.addExtra(username);

        // Set pronouns
        if (this.pronouns != null) {
            TextComponent pronouns = new TextComponent(new ComponentBuilder("Pronouns: " + this.pronouns).
                    color(ChatColor.DARK_GRAY).create());
            hover.addExtra(ChatTag.NEW_LINE);
            hover.addExtra(pronouns);
        }

        // Set hover text
        BaseComponent[] hoverText = Collections.singleton(hover).toArray(new BaseComponent[1]);
        nameTagText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

        // Set display name in Bukkit/Spigot
        if(getOnline()) {
            Player player = (Player) getOfflinePlayer();
            player.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', nickName));
            player.setPlayerListName(org.bukkit.ChatColor.translateAlternateColorCodes('&', nickName));
        }
    }

    @Override
    public void register() {
        ReserveChat.PLAYER_R.register(this);
    }

    public boolean getOnline() {
        return getOfflinePlayer().isOnline();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(mojangId));
    }

    public Location getLocation() {
        if (getOnline()) {
            return getOfflinePlayer().getPlayer().getLocation();
        }
        throw new UnsupportedOperationException("We don't support finding locations for players who aren't online.");
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
        register();
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        register();
    }

    public String getRawNickName() {
        return nickName;
    }

    public String getNickName() {
        return ChatColor.translateAlternateColorCodes('&', nickName);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        register();
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
        register();
    }

    public TextComponent getNameTag() {
        return nameTagText;
    }
}
