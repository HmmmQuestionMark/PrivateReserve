package me.hqm.privatereserve.registry;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.privatereserve.model.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public interface PlayerRegistry extends Registry<PlayerModel> {
    String NAME = "players";

    @Deprecated
    default Optional<PlayerModel> fromName(final String name) {
        Optional<PlayerModel> player =
                getRegisteredData().values().stream().filter(model -> model.getLastKnownName().equalsIgnoreCase(name))
                        .findFirst();
        return player;
    }

    default Optional<PlayerModel> fromPlayer(OfflinePlayer player) {
        return fromKey(player.getUniqueId().toString());
    }

    @Deprecated
    default Optional<PlayerModel> fromId(UUID id) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
        return fromPlayer(player);
    }

    @Deprecated
    default Optional<PlayerModel> fromId(String id) {
        return fromId(UUID.fromString(id));
    }

    default Set<OfflinePlayer> getOfflinePlayers() {
        return getRegisteredData().values().stream().map(PlayerModel::getOfflinePlayer).collect(Collectors.toSet());
    }

    @Override
    default PlayerModel fromDataSection(String stringKey, DataSection data) {
        return new PlayerModel(stringKey, data);
    }

    default PlayerModel invite(OfflinePlayer player, Player inviteFrom) {
        return invite(player, inviteFrom.getUniqueId().toString());
    }

    default PlayerModel invite(OfflinePlayer player, String inviteFrom) {
        PlayerModel model = new PlayerModel(player, inviteFrom);
        PlayerModel invite = fromKey(inviteFrom).get();
        invite.addInvited(model.getKey());
        register(model);
        return model;
    }

    default PlayerModel inviteConsole(OfflinePlayer player) {
        PlayerModel model = new PlayerModel(player, true);
        register(model);
        return model;
    }

    default PlayerModel inviteConsole(OfflinePlayer player, boolean trusted) {
        PlayerModel model = new PlayerModel(player, true, trusted);
        register(model);
        return model;
    }

    default PlayerModel inviteSelf(Player player) {
        PlayerModel model = new PlayerModel(player, false);
        register(model);
        return model;
    }

    default boolean isVisitor(OfflinePlayer player) {
        return !fromPlayer(player).isPresent();
    }

    default boolean isExpelled(OfflinePlayer player) {
        return fromPlayer(player).isPresent() && fromPlayer(player).get().isExpelled();
    }

    default boolean isVisitorOrExpelled(OfflinePlayer player) {
        return isVisitor(player) || isExpelled(player);
    }

    default boolean isTrusted(OfflinePlayer player) {
        Optional<PlayerModel> oModel = fromPlayer(player);
        return oModel.isPresent() && oModel.get().isTrusted();
    }

    @Deprecated
    default List<String> getInvitedManually(PlayerModel model) {
        return getRegisteredData().values().stream().filter(
                playerModel -> model.getKey().equals(playerModel.getInvitedFrom())).map(
                PlayerModel::getKey).collect(Collectors.toList());
    }

    default int getInvitedCount(OfflinePlayer player) {
        String playerId = player.getUniqueId().toString();
        return (int) getRegisteredData().values().stream().filter(
                playerModel -> playerId.equals(playerModel.getInvitedFrom())).count();
    }
}
