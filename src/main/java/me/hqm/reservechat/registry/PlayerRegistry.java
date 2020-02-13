package me.hqm.reservechat.registry;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.reservechat.model.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public interface PlayerRegistry extends Registry<PlayerModel> {
    String NAME = "players";

    @Deprecated
    default PlayerModel fromName(final String name) {
        Optional<PlayerModel> player =
                getRegisteredData().values().stream().filter(model -> model.getLastKnownName().equalsIgnoreCase(name))
                        .findFirst();
        return player.orElse(null);
    }

    default PlayerModel fromPlayer(OfflinePlayer player) {
        Optional<PlayerModel> found = fromKey(player.getUniqueId().toString());
        if (!found.isPresent()) {
            PlayerModel model = new PlayerModel(player);
            return register(model);
        }
        return found.get();
    }

    @Deprecated
    default PlayerModel fromId(UUID id) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(id);
        return fromPlayer(player);
    }

    @Deprecated
    default PlayerModel fromId(String id) {
        return fromId(UUID.fromString(id));
    }

    default Set<OfflinePlayer> getOfflinePlayers() {
        return getRegisteredData().values().stream().map(PlayerModel::getOfflinePlayer).collect(Collectors.toSet());
    }

    default List<String> getNameStartsWith(final String name) {
        return getRegisteredData().values().stream()
                .filter(model -> TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - model.getLastLoginTime()) < 3
                        && model.getLastKnownName().toLowerCase().startsWith(name.toLowerCase()))
                .map(PlayerModel::getLastKnownName).collect(Collectors.toList());
    }

    @Override
    default PlayerModel fromDataSection(String stringKey, DataSection data) {
        return new PlayerModel(stringKey, data);
    }
}
