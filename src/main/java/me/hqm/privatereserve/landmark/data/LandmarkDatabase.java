package me.hqm.privatereserve.landmark.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentDatabase;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.MemberDocument;

import java.util.*;
import java.util.stream.Collectors;

public interface LandmarkDatabase extends DocumentDatabase<LandmarkDocument> {
    String NAME = "landmarks";

    @Override
    default LandmarkDocument createDocument(String stringKey, Document data) {
        return new LandmarkDocument(stringKey, data);
    }

    default Optional<LandmarkDocument> fromName(String name) {
        return fromId(name);
    }

    default List<LandmarkDocument> fromOwner(MemberDocument model) {
        return getRawData().values().stream().
                filter(landmarkModel -> landmarkModel.getOwner().equals(model.getId())).
                collect(Collectors.toList());
    }

    default int landmarksOwned(MemberDocument model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<LandmarkDocument> fromOwnerName(String owner) {
        List<LandmarkDocument> landmarks = new ArrayList<>();
        Optional<MemberDocument> maybe = Members.data().fromId(owner);
        if (maybe.isPresent()) {
            for (LandmarkDocument landmark : getRawData().values()) {
                if (landmark.getOwner().equals(maybe.get().getId())) {
                    landmarks.add(landmark);
                }
            }
        }
        return landmarks;
    }

    default List<String> landmarkNames() {
        return getRawData().values().stream().map(LandmarkDocument::getName).toList();
    }

    default Map<String, Integer> landmarkOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (LandmarkDocument landmark : getRawData().values()) {
            Optional<MemberDocument> maybe = Members.data().fromId(landmark.getOwner());
            if (maybe.isPresent()) {
                String lastKnownName = maybe.get().getLastKnownName();
                nameAndCount.merge(lastKnownName, 1, Integer::sum);
            }
        }
        return nameAndCount;
    }
}
