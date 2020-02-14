package me.hqm.privatereserve.model;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Model;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.tag.ChatTag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerModel implements Model {

    // -- META DATA -- //

    private final String mojangId;
    private String lastKnownName;

    // -- INTERESTING DATA -- //

    private String nickName;
    private String pronouns;

    // -- GREYLIST DATA -- //

    boolean trusted;
    boolean expelled;
    long timeInvited;
    String invitedFrom;
    List<String> invited;

    // -- NAME TAG TEXT -- //

    private transient TextComponent nameTagText;

    // -- CONSTRUCTORS -- //

    public PlayerModel(OfflinePlayer player, boolean console) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString());
        trusted = !console;
    }

    public PlayerModel(OfflinePlayer player, String invitedFrom) {
        mojangId = player.getUniqueId().toString();
        lastKnownName = player.getName();
        nickName = lastKnownName;
        trusted = false;
        expelled = false;
        timeInvited = System.currentTimeMillis();
        buildNameTag();
    }

    public PlayerModel (String mojangId, DataSection data) {
        this.mojangId = mojangId;
        lastKnownName = data.getString("last_known_name");

        nickName = data.getString("nickname");
        pronouns = data.isString("pronouns") ? data.getString("pronouns") : null;

        trusted = data.getBoolean("trusted", false);
        expelled = data.getBoolean("expelled", false);

        timeInvited = data.getLong("timeInvited", System.currentTimeMillis());
        invitedFrom = data.getString("invitedFrom");
        invited = data.getStringList("invited");
        buildNameTag();
    }

    // -- GETTERS -- //

    @Override
    public String getKey() {
        return mojangId;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("last_known_name", lastKnownName);

        data.put("nickname", nickName != null ? nickName : lastKnownName);
        if (pronouns != null) {
            data.put("pronouns", pronouns);
        }

        data.put("trusted", trusted);
        data.put("expelled", expelled);

        data.put("timeInvited", timeInvited);
        data.put("invitedFrom", invitedFrom);
        data.put("invited", invited);
        return data;
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

    public String getRawNickName() {
        return nickName;
    }

    public String getNickName() {
        return ChatColor.translateAlternateColorCodes('&', nickName);
    }

    public String getPronouns() {
        return pronouns;
    }

    public TextComponent getNameTag() {
        return nameTagText;
    }

    public long getTimeInvited() {
        return timeInvited;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public boolean isExpelled() {
        return expelled;
    }

    public String getInvitedFrom() {
        return invitedFrom;
    }

    public List<String> getInvited() {
        return invited;
    }

    // -- MUTATORS -- //

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
        register();
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        register();
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
        register();
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
        register();
    }

    public void setExpelled(boolean expelled) {
        this.expelled = expelled;
        register();
    }

    public void setInvitedFrom(String invitedFrom) {
        this.invitedFrom = invitedFrom;
        this.timeInvited = System.currentTimeMillis();
        register();
    }

    // -- UTIL -- //

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
        PrivateReserve.PLAYER_R.register(this);
    }
}
