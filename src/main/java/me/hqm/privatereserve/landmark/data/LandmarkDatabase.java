package me.hqm.privatereserve.landmark.data;

import me.hqm.document.DocumentMap;
import me.hqm.document.Database;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.member.data.MemberDocument;

import java.util.*;
import java.util.stream.Collectors;

public interface LandmarkDatabase extends Database<LandmarkDocument> {
    String NAME = "landmarks";

    @Override
    default LandmarkDocument fromDataSection(String stringKey, DocumentMap data) {
        return new LandmarkDocument(stringKey, data);
    }

    default Optional<LandmarkDocument> fromName(String name) {
        return fromKey(name);
    }

    default List<LandmarkDocument> fromOwner(MemberDocument model) {
        return getRawData().values().stream().
                filter(landmarkModel -> landmarkModel.getOwner().equals(model.getKey())).
                collect(Collectors.toList());
    }

    default int landmarksOwned(MemberDocument model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<LandmarkDocument> fromOwnerName(String owner) {
        List<LandmarkDocument> landmarks = new ArrayList<>();
        Optional<MemberDocument> maybe = _PrivateReserve.MEMBER_DATA.fromId(owner);
        if (maybe.isPresent()) {
            for (LandmarkDocument landmark : getRawData().values()) {
                if (landmark.getOwner().equals(maybe.get().getKey())) {
                    landmarks.add(landmark);
                }
            }
        }
        return landmarks;
    }

    default Map<String, Integer> landmarkOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (LandmarkDocument landmark : getRawData().values()) {
            Optional<MemberDocument> maybe = _PrivateReserve.MEMBER_DATA.fromId(landmark.getOwner());
            if (maybe.isPresent()) {
                String lastKnownName = maybe.get().getLastKnownName();
                nameAndCount.merge(lastKnownName, 1, Integer::sum);
            }
        }
        return nameAndCount;
    }
}
