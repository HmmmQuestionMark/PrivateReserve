package me.hqm.privatereserve.member.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface MemberDatabase extends DocumentDatabase<MemberDocument> {
    String NAME = "players";

    @Deprecated
    default Optional<MemberDocument> fromName(final String name) {
        return getRawData().values().stream().
                filter(model -> model.getLastKnownName().equalsIgnoreCase(name)).findFirst();
    }

    @Deprecated
    default Optional<MemberDocument> fromPlayer(OfflinePlayer player) {
        return this.fromId(player.getUniqueId().toString());
    }

    default Optional<MemberDocument> fromId(UUID id) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
        return fromPlayer(player);
    }

    default Optional<MemberDocument> fromId(String id) {
        return fromId(UUID.fromString(id));
    }

    @Deprecated
    default Set<OfflinePlayer> getOfflinePlayers() {
        return getRawData().values().stream().map(MemberDocument::getOfflinePlayer).collect(Collectors.toSet());
    }

    default Set<String> getPlayerNames() {
        return getRawData().values().stream().map(MemberDocument::getLastKnownName).collect(Collectors.toSet());
    }

    @Override
    default MemberDocument createDocument(String stringKey, Document data) {
        return new MemberDocument(stringKey, data);
    }

    default MemberDocument invite(OfflinePlayer player, Player inviteFrom) {
        return invite(player, inviteFrom.getUniqueId().toString(), null);
    }

    default MemberDocument invite(OfflinePlayer player, String inviteFrom) {
        MemberDocument model = new MemberDocument(player, inviteFrom, null);
        MemberDocument invite = this.fromId(inviteFrom).get();
        invite.addInvited(model.getId());
        write(model);
        return model;
    }

    default MemberDocument invite(OfflinePlayer player, Player inviteFrom, String primaryAcoount) {
        return invite(player, inviteFrom.getUniqueId().toString(), primaryAcoount);
    }

    default MemberDocument invite(OfflinePlayer player, String inviteFrom, String primaryAcoount) {
        MemberDocument model = new MemberDocument(player, inviteFrom, primaryAcoount);
        MemberDocument invite = this.fromId(inviteFrom).get();
        invite.addInvited(model.getId());
        write(model);
        return model;
    }

    default MemberDocument inviteConsole(OfflinePlayer player) {
        MemberDocument model = new MemberDocument(player, true);
        write(model);
        return model;
    }

    default MemberDocument inviteConsole(OfflinePlayer player, String primaryAccount) {
        MemberDocument model = new MemberDocument(player, true, false, primaryAccount);
        write(model);
        return model;
    }

    default MemberDocument inviteConsole(OfflinePlayer player, boolean trusted) {
        MemberDocument model = new MemberDocument(player, true, trusted);
        write(model);
        return model;
    }

    default MemberDocument inviteSelf(Player player) {
        MemberDocument model = new MemberDocument(player, false);
        write(model);
        return model;
    }

    default boolean isVisitor(UUID player) {
        return fromId(player).isEmpty();
    }

    default boolean isExpelled(UUID player) {
        return fromId(player).isPresent() && fromId(player).get().isExpelled();
    }

    default boolean isExpelled(String player) {
        return fromId(player).isPresent() && fromId(player).get().isExpelled();
    }

    default boolean isAlternate(UUID player) {
        return fromId(player).isPresent() && fromId(player).get().isAlternate();
    }

    default boolean isVisitorOrExpelled(UUID player) {
        return isVisitor(player) || isExpelled(player);
    }

    default boolean isTrusted(UUID player) {
        Optional<MemberDocument> oModel = fromId(player);
        return oModel.isPresent() && oModel.get().isTrusted();
    }

    default boolean isTrusted(String player) {
        Optional<MemberDocument> oModel = fromId(player);
        return oModel.isPresent() && oModel.get().isTrusted();
    }

    @Deprecated
    default List<String> getInvitedManually(MemberDocument model) {
        return getRawData().values().stream().filter(
                playerModel -> model.getId().equals(playerModel.getInvitedFrom())).map(
                MemberDocument::getId).collect(Collectors.toList());
    }

    default int getInvitedCount(UUID player) {
        String playerId = player.toString();
        return (int) getRawData().values().stream().filter(
                playerModel -> playerId.equals(playerModel.getInvitedFrom())).count();
    }
}
