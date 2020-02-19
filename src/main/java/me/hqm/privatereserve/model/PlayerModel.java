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
    private String primaryAccount;

    // -- INTERESTING DATA -- //

    private String nickName;
    private String pronouns;

    // -- GREYLIST DATA -- //

    private boolean trusted;
    private boolean expelled;
    private long timeInvited;
    private String invitedFrom;
    private List<String> invited;

    // -- NAME TAG TEXT -- //

    private transient TextComponent nameTagText;

    // -- CONSTRUCTORS -- //

    public PlayerModel(OfflinePlayer player, boolean console) {
        this(player, console, !console);
    }

    public PlayerModel(OfflinePlayer player, boolean console, boolean trusted) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString(), null);
        this.trusted = trusted;
    }

    public PlayerModel(OfflinePlayer player, boolean console, boolean trusted, String primaryAccount) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString(), primaryAccount);
        this.trusted = trusted;
    }

    public PlayerModel(OfflinePlayer player, String invitedFrom, String primaryAccount) {
        mojangId = player.getUniqueId().toString();
        lastKnownName = player.getName();
        this.invitedFrom = invitedFrom;
        nickName = lastKnownName;
        trusted = false;
        expelled = false;
        this.primaryAccount = primaryAccount;
        timeInvited = System.currentTimeMillis();
        invited = new ArrayList<>();
        buildNameTag();
    }

    public PlayerModel (String mojangId, DataSection data) {
        this.mojangId = mojangId;
        lastKnownName = data.getString("last_known_name");

        nickName = data.getString("nickname");
        pronouns = data.isString("pronouns") ? data.getString("pronouns") : null;

        trusted = data.getBoolean("trusted", false);
        expelled = data.getBoolean("expelled", false);

        primaryAccount = data.getStringNullable("primaryAccount"); // If specified, this is an alt account

        timeInvited = data.getLong("timeInvited", System.currentTimeMillis());
        invitedFrom = data.getString("invitedFrom", "d5133464-b1ef-42b4-9ad4-8cac217d40f0"); // Default to HQM
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

        if (primaryAccount != null) {
            data.put("primaryAccount", primaryAccount);
        }

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

    public String getPrimaryAccount() {
        return primaryAccount;
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
        return isAlternate() ? PrivateReserve.PLAYER_R.isTrusted(primaryAccount) : trusted;
    }

    public boolean isExpelled() {
        return isAlternate() ? PrivateReserve.PLAYER_R.isExpelled(primaryAccount) : expelled;
    }

    public boolean isAlternate() {
        return primaryAccount != null;
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

    public void setPrimaryAccount(String primaryAccount) {
        this.primaryAccount = primaryAccount;
        buildNameTag();
        register();
    }

    public void setInvitedFrom(String invitedFrom) {
        this.invitedFrom = invitedFrom;
        this.timeInvited = System.currentTimeMillis();
        register();
    }

    @Deprecated
    public void setInvited(List<String> invited) {
        this.invited = invited;
        register();
    }

    public void addInvited(String invitee) {
        this.invited.add(invitee);
        register();
    }

    // -- UTIL -- //

    public void buildNameTag() {
        String primaryAccountName = null;
        if (isAlternate()) {
            Optional<PlayerModel> primary = PrivateReserve.PLAYER_R.fromId(primaryAccount);
            if (primary.isPresent()) {
                primaryAccountName = primary.get().getLastKnownName();
            } else {
                setPrimaryAccount(null);
                register();
            }
        }

        nameTagText = buildNameTag0(nickName, lastKnownName, primaryAccountName, pronouns, invited);

        // Set display name in Bukkit/Spigot
        if (getOnline()) {
            Player player = (Player) getOfflinePlayer();
            player.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', nickName));
            player.setPlayerListName(org.bukkit.ChatColor.translateAlternateColorCodes('&', nickName));
        }
    }

    private TextComponent buildNameTag0(String nickName, String lastKnownName, String primaryAccountName,
                                        String pronouns, List<String> invited) {
        // Define blank component
        TextComponent nameTagText = new TextComponent();

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

        if (primaryAccountName != null) {
            // Give last known username for the primary account
            TextComponent primaryUsername = new TextComponent(new ComponentBuilder("Primary Account: " +
                    primaryAccountName).color(ChatColor.DARK_GRAY).create());
            hover.addExtra(ChatTag.NEW_LINE);
            hover.addExtra(primaryUsername);
        }

        // Set pronouns
        if (pronouns != null) {
            TextComponent pronounsComp = new TextComponent(new ComponentBuilder("Pronouns: " + pronouns).
                    color(ChatColor.DARK_GRAY).create());
            hover.addExtra(ChatTag.NEW_LINE);
            hover.addExtra(pronounsComp);
        }

        // Set invited amount
        if (invited.size() > 0) {
            TextComponent countText =
                    new TextComponent(new ComponentBuilder("Invited: " + invited.size() + " members").
                            color(ChatColor.DARK_GRAY).create());
            hover.addExtra(ChatTag.NEW_LINE);
            hover.addExtra(countText);
        }

        // Set hover text
        BaseComponent[] hoverText = Collections.singleton(hover).toArray(new BaseComponent[1]);
        nameTagText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

        return nameTagText;
    }

    @Override
    public void register() {
        PrivateReserve.PLAYER_R.register(this);
    }
}
