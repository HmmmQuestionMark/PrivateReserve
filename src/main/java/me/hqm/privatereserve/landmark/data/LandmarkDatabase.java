package me.hqm.privatereserve.landmark.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentDatabase;
import me.hqm.privatereserve.member.Members;
import me.hqm.privatereserve.member.data.Member;

import java.util.*;
import java.util.stream.Collectors;

public interface LandmarkDatabase extends DocumentDatabase<Landmark> {
    String NAME = "landmarks";

    @Override
    default Landmark createDocument(String stringKey, Document data) {
        return new Landmark(stringKey, data);
    }

    default Optional<Landmark> fromName(String name) {
        return fromId(name);
    }

    default List<Landmark> fromOwner(Member model) {
        return getRawData().values().stream().
                filter(landmarkModel -> landmarkModel.getOwner().equals(model.getId())).
                collect(Collectors.toList());
    }

    default int landmarksOwned(Member model) {
        return fromOwner(model).size();
    }

    @Deprecated
    default List<Landmark> fromOwnerName(String owner) {
        List<Landmark> landmarks = new ArrayList<>();
        Optional<Member> maybe = Members.data().fromId(owner);
        if (maybe.isPresent()) {
            for (Landmark landmark : getRawData().values()) {
                if (landmark.getOwner().equals(maybe.get().getId())) {
                    landmarks.add(landmark);
                }
            }
        }
        return landmarks;
    }

    default List<String> landmarkNames() {
        return getRawData().values().stream().map(Landmark::getName).toList();
    }

    default Map<String, Integer> landmarkOwnerNames() {
        Map<String, Integer> nameAndCount = new HashMap<>();
        for (Landmark landmark : getRawData().values()) {
            Optional<Member> maybe = Members.data().fromId(landmark.getOwner());
            if (maybe.isPresent()) {
                String lastKnownName = maybe.get().getLastKnownName();
                nameAndCount.merge(lastKnownName, 1, Integer::sum);
            }
        }
        return nameAndCount;
    }
}
