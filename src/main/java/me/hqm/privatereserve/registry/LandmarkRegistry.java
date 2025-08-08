package me.hqm.privatereserve.registry;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.LandmarkModel;
import me.hqm.privatereserve.model.PlayerModel;

import java.util.*;
import java.util.stream.Collectors;

public interface LandmarkRegistry extends Registry<LandmarkModel> {
    String NAME = "landmarks";

    @Override
    default LandmarkModel fromDataSection(String stringKey, DataSection data) {
        return new LandmarkModel(stringKey, data);
    }

    default Optional<LandmarkModel> fromName(String name) {
        return fromKey(name);
    }

    default List<LandmarkModel> fromOwner(PlayerModel model) {
        return getRegisteredData().values().stream().
                filter(landmarkModel -> landmarkModel.getOwner().equals(model.getKey())).
                collect(Collectors.toList());
    }

    default int landmarksOwned(PlayerModel model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<LandmarkModel> fromOwnerName(String owner) {
        List<LandmarkModel> landmarks = new ArrayList<>();
        Optional<PlayerModel> maybe = PrivateReserve.PLAYER_R.fromId(owner);
        if (maybe.isPresent()) {
            for (LandmarkModel landmark : getRegisteredData().values()) {
                if (landmark.getOwner().equals(maybe.get().getKey())) {
                    landmarks.add(landmark);
                }
            }
        }
        return landmarks;
    }

    default Map<String, Integer> landmarkOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (LandmarkModel landmark : getRegisteredData().values()) {
            Optional<PlayerModel> maybe = PrivateReserve.PLAYER_R.fromId(landmark.getOwner());
            if(maybe.isPresent()) {
                String lastKnownName = maybe.get().getLastKnownName();
                nameAndCount.merge(lastKnownName, 1, Integer::sum);
            }
        }
        return nameAndCount;
    }
}
