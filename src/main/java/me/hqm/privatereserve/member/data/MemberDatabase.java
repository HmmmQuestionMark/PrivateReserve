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

public interface MemberDatabase extends DocumentDatabase<Member> {
    String NAME = "players";

    @Deprecated
    default Optional<Member> fromName(final String name) {
        return getRawData().values().stream().
                filter(model -> model.getLastKnownName().equalsIgnoreCase(name)).findFirst();
    }

    @Deprecated
    default Optional<Member> fromPlayer(OfflinePlayer player) {
        return this.fromId(player.getUniqueId().toString());
    }

    default Optional<Member> fromId(UUID id) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
        return fromPlayer(player);
    }

    default Optional<Member> fromId(String id) {
        return fromId(UUID.fromString(id));
    }

    @Deprecated
    default Set<OfflinePlayer> getOfflinePlayers() {
        return getRawData().values().stream().map(Member::getOfflinePlayer).collect(Collectors.toSet());
    }

    default Set<String> getPlayerNames() {
        return getRawData().values().stream().map(Member::getLastKnownName).collect(Collectors.toSet());
    }

    @Override
    default Member createDocument(String stringKey, Document data) {
        return new Member(stringKey, data);
    }

    default Member invite(OfflinePlayer player, Player inviteFrom) {
        return invite(player, inviteFrom.getUniqueId().toString(), null);
    }

    default Member invite(OfflinePlayer player, String inviteFrom) {
        Member model = new Member(player, inviteFrom, null);
        Member invite = this.fromId(inviteFrom).get();
        invite.addInvited(model.getId());
        write(model);
        return model;
    }

    default Member invite(OfflinePlayer player, Player inviteFrom, String primaryAcoount) {
        return invite(player, inviteFrom.getUniqueId().toString(), primaryAcoount);
    }

    default Member invite(OfflinePlayer player, String inviteFrom, String primaryAcoount) {
        Member model = new Member(player, inviteFrom, primaryAcoount);
        Member invite = this.fromId(inviteFrom).get();
        invite.addInvited(model.getId());
        write(model);
        return model;
    }

    default Member inviteConsole(OfflinePlayer player) {
        Member model = new Member(player, true);
        write(model);
        return model;
    }

    default Member inviteConsole(OfflinePlayer player, String primaryAccount) {
        Member model = new Member(player, true, false, primaryAccount);
        write(model);
        return model;
    }

    default Member inviteConsole(OfflinePlayer player, boolean trusted) {
        Member model = new Member(player, true, trusted);
        write(model);
        return model;
    }

    default Member inviteSelf(Player player) {
        Member model = new Member(player, false);
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
        Optional<Member> oModel = fromId(player);
        return oModel.isPresent() && oModel.get().isTrusted();
    }

    default boolean isTrusted(String player) {
        Optional<Member> oModel = fromId(player);
        return oModel.isPresent() && oModel.get().isTrusted();
    }

    @Deprecated
    default List<String> getInvitedManually(Member model) {
        return getRawData().values().stream().filter(
                playerModel -> model.getId().equals(playerModel.getInvitedFrom())).map(
                Member::getId).collect(Collectors.toList());
    }

    default int getInvitedCount(UUID player) {
        String playerId = player.toString();
        return (int) getRawData().values().stream().filter(
                playerModel -> playerId.equals(playerModel.getInvitedFrom())).count();
    }
}
