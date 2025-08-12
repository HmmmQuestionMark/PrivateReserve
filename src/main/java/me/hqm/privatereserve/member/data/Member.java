package me.hqm.privatereserve.member.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.member.Members;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;

public class Member implements DocumentCompatible {

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

    public Member(OfflinePlayer player, boolean console) {
        this(player, console, !console);
    }

    public Member(OfflinePlayer player, boolean console, boolean trusted) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString(), null);
        this.trusted = trusted;
    }

    public Member(OfflinePlayer player, boolean console, boolean trusted, String primaryAccount) {
        this(player, console ? "CONSOLE" : player.getUniqueId().toString(), primaryAccount);
        this.trusted = trusted;
    }

    public Member(OfflinePlayer player, String invitedFrom, String primaryAccount) {
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

    public Member(String mojangId, Document data) {
        this.mojangId = mojangId;
        lastKnownName = data.get("last_known_name", PersistentDataType.STRING);

        nickName = data.get("nickname", PersistentDataType.STRING);
        pronouns = data.get("pronouns", PersistentDataType.STRING);

        trusted = data.getOrDefault("trusted", PersistentDataType.BOOLEAN, false);
        expelled = data.getOrDefault("expelled", PersistentDataType.BOOLEAN, false);

        primaryAccount = data.get("primaryAccount", PersistentDataType.STRING); // If specified, this is an alt account

        timeInvited = data.getOrDefault("timeInvited", PersistentDataType.LONG, System.currentTimeMillis());
        invitedFrom = data.getOrDefault("invitedFrom", PersistentDataType.STRING, "d5133464-b1ef-42b4-9ad4-8cac217d40f0"); // Default to HQM
        invited = data.get("invited", PersistentDataType.LIST.strings());

        homeLoc = data.get("homeLoc", PersistentDataType.STRING);

        buildNameTag();
    }

    // -- GETTERS -- //

    @Override
    public String getId() {
        return mojangId;
    }

    @Override
    public Map<String, Object> asMap() {
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

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
        write();
    }

    public String getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(String primaryAccount) {
        this.primaryAccount = primaryAccount;
        buildNameTag();
        write();
    }

    public String getRawNickName() {
        return nickName;
    }

    public Component getNickName() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(nickName);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        write();
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
        buildNameTag();
        write();
    }

    public @Nullable Location getHomeLoc() {
        if (homeLoc != null) {
            return Locations.locationFromString(homeLoc);
        }
        return null;
    }

    public void setHomeLoc(Location homeLoc) {
        if (homeLoc != null) {
            this.homeLoc = Locations.stringFromLocation(homeLoc);
        } else {
            this.homeLoc = null;
        }
        write();
    }

    public Component getNameTag() {
        return nameTagText;
    }

    public long getTimeInvited() {
        return timeInvited;
    }

    // -- MUTATORS -- //

    public boolean isTrusted() {
        return isAlternate() ? Members.data().isTrusted(primaryAccount) : trusted;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
        write();
    }

    public boolean isExpelled() {
        return isAlternate() ? Members.data().isExpelled(primaryAccount) : expelled;
    }

    public void setExpelled(boolean expelled) {
        this.expelled = expelled;
        write();
    }

    public boolean isAlternate() {
        return primaryAccount != null;
    }

    public String getInvitedFrom() {
        return invitedFrom;
    }

    public void setInvitedFrom(String invitedFrom) {
        this.invitedFrom = invitedFrom;
        this.timeInvited = System.currentTimeMillis();
        write();
    }

    public List<String> getInvited() {
        return invited;
    }

    @Deprecated
    public void setInvited(List<String> invited) {
        this.invited = invited;
        write();
    }

    public void addInvited(String invitee) {
        this.invited.add(invitee);
        buildNameTag();
        write();
    }

    // -- UTIL -- //

    public void buildNameTag() {
        String primaryAccountName = null;
        if (isAlternate()) {
            Optional<Member> primary = Members.data().fromId(primaryAccount);
            if (primary.isPresent()) {
                primaryAccountName = primary.get().getLastKnownName();
            } else {
                setPrimaryAccount(null);
                write();
            }
        }

        nameTagText = buildNameTag0(nickName, lastKnownName, primaryAccountName, pronouns, invited);

        // Set display name
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
            hover.appendNewline();
            hover.append(primaryUsername);
        }

        // Set pronouns
        if (pronouns != null) {
            Component pronounsComp = Component.text("Pronouns: " + pronouns, NamedTextColor.DARK_GRAY);
            hover.appendNewline();
            hover.append(pronounsComp);
        }

        // Set invited amount
        if (!invited.isEmpty()) {
            Component countText = Component.text("Invited: " + invited.size() + " members", NamedTextColor.DARK_GRAY);
            hover.appendNewline();
            hover.append(countText);
        }

        // Set hover text
        nameTagText.hoverEvent(hover.build());

        return nameTagText.build();
    }

    // -- UTIL -- //

    public void write() {
        Members.data().add(this);
    }
}
