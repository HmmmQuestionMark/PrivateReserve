package me.hqm.privatereserve.member.data;

import me.hqm.privatereserve.Locations;
import me.hqm.document.DocumentMap;
import me.hqm.document.Document;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.chat.ChatTags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class MemberDocument implements Document {

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

    // -- LOCATION DATA -- //

    private String homeLoc;

    // -- NAME TAG TEXT -- //

    private transient Component nameTagText;

    // -- CONSTRUCTORS -- //

    public MemberDocument(OfflinePlayer player, boolean console) {
        this(player, console, !console);
    }

    public MemberDocument(OfflinePlayer player, boolean console, boolean trusted) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString(), null);
        this.trusted = trusted;
    }

    public MemberDocument(OfflinePlayer player, boolean console, boolean trusted, String primaryAccount) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString(), primaryAccount);
        this.trusted = trusted;
    }

    public MemberDocument(OfflinePlayer player, String invitedFrom, String primaryAccount) {
        mojangId = player.getUniqueId().toString();
        lastKnownName = player.getName();
        this.invitedFrom = invitedFrom;
        nickName = LegacyComponentSerializer.legacyAmpersand().
                serialize(Component.text(lastKnownName, NamedTextColor.GRAY));
        trusted = false;
        expelled = false;
        this.primaryAccount = primaryAccount;
        timeInvited = System.currentTimeMillis();
        invited = new ArrayList<>();
        buildNameTag();
    }

    public MemberDocument(String mojangId, DocumentMap data) {
        this.mojangId = mojangId;
        lastKnownName = data.getString("last_known_name");

        nickName = data.getString("nickname");
        pronouns = data.getStringNullable("pronouns");

        trusted = data.getBoolean("trusted", false);
        expelled = data.getBoolean("expelled", false);

        primaryAccount = data.getStringNullable("primaryAccount"); // If specified, this is an alt account

        timeInvited = data.getLong("timeInvited", System.currentTimeMillis());
        invitedFrom = data.getString("invitedFrom", "d5133464-b1ef-42b4-9ad4-8cac217d40f0"); // Default to HQM
        invited = data.getStringList("invited");

        homeLoc = data.getStringNullable("homeLoc");

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

        if (homeLoc != null) {
            data.put("homeLoc", homeLoc);
        }
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

    public Component getNickName() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(nickName);
    }

    public String getPronouns() {
        return pronouns;
    }

    public @Nullable Location getHomeLoc() {
        if (homeLoc != null) {
            return Locations.locationFromString(homeLoc);
        }
        return null;
    }

    public Component getNameTag() {
        return nameTagText;
    }

    public long getTimeInvited() {
        return timeInvited;
    }

    public boolean isTrusted() {
        return isAlternate() ? _PrivateReserve.MEMBER_DATA.isTrusted(primaryAccount) : trusted;
    }

    public boolean isExpelled() {
        return isAlternate() ? _PrivateReserve.MEMBER_DATA.isExpelled(primaryAccount) : expelled;
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
        buildNameTag();
        register();
    }

    public void setHomeLoc(Location homeLoc) {
        if (homeLoc != null) {
            this.homeLoc = Locations.stringFromLocation(homeLoc);
        } else {
            this.homeLoc = null;
        }
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
        buildNameTag();
        register();
    }

    // -- UTIL -- //

    public void buildNameTag() {
        String primaryAccountName = null;
        if (isAlternate()) {
            Optional<MemberDocument> primary = _PrivateReserve.MEMBER_DATA.fromId(primaryAccount);
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
            player.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(nickName));
            player.playerListName(LegacyComponentSerializer.legacyAmpersand().deserialize(nickName));
        }
    }

    private Component buildNameTag0(String nickName, String lastKnownName, String primaryAccountName,
                                    String pronouns, List<String> invited) {
        // Define blank component
        ComponentBuilder<TextComponent, TextComponent.Builder> nameTagText = Component.text();

        // Build from legacy text
        nameTagText.append(LegacyComponentSerializer.legacyAmpersand().deserialize(nickName));

        // Begin hover text
        ComponentBuilder<TextComponent, TextComponent.Builder> hover = Component.text();

        // Give last known username
        Component username = Component.text("Username: " + lastKnownName, NamedTextColor.DARK_GRAY);
        hover.append(username);

        if (primaryAccountName != null) {
            // Give last known username for the primary account
            Component primaryUsername = Component.text("Primary Account: " +
                    primaryAccountName, NamedTextColor.DARK_GRAY);
            hover.append(ChatTags.NEW_LINE);
            hover.append(primaryUsername);
        }

        // Set pronouns
        if (pronouns != null) {
            Component pronounsComp = Component.text("Pronouns: " + pronouns, NamedTextColor.DARK_GRAY);
            hover.append(ChatTags.NEW_LINE);
            hover.append(pronounsComp);
        }

        // Set invited amount
        if (!invited.isEmpty()) {
            Component countText = Component.text("Invited: " + invited.size() + " members", NamedTextColor.DARK_GRAY);
            hover.append(ChatTags.NEW_LINE);
            hover.append(countText);
        }

        // Set hover text
        nameTagText.hoverEvent(hover.build());

        return nameTagText.build();
    }

    @Override
    public void register() {
        _PrivateReserve.MEMBER_DATA.register(this);
    }
}
