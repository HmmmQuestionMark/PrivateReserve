/*
 * Copyright 2015 Demigods RPG
 * Copyright 2015 Alexander Chauncey
 * Copyright 2015 Alex Bennett
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.hqm.privatereserve.relationaldata;

import me.hqm.document.DocumentMap;
import me.hqm.document.Database;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public interface RelationalDataDatabase extends Database<RelationalDataDocument> {
    String NAME = "data";

    default void put(String row, String column, String value) {
        put0(row, column, value);
    }

    default void put(String row, String column, boolean value) {
        put0(row, column, value);
    }

    default void put(String row, String column, Number value) {
        put0(row, column, value);
    }

    default void put0(String row, String column, Object value) {
        // Remove the value if it exists already
        remove(row, column);

        // Create and save the timed value
        RelationalDataDocument timedData = new RelationalDataDocument();
        timedData.generateId();
        timedData.setDataType(RelationalDataDocument.DataType.PERSISTENT);
        timedData.setRow(row);
        timedData.setColumn(column);
        timedData.setValue(value);
        register(timedData);
    }

    /*
     * Timed value
     */
    default void put(String row, String column, Object value, long time, TimeUnit unit) {
        // Remove the value if it exists already
        remove(row, column);

        // Create and save the timed value
        RelationalDataDocument timedData = new RelationalDataDocument();
        timedData.generateId();
        timedData.setDataType(RelationalDataDocument.DataType.TIMED);
        timedData.setRow(row);
        timedData.setColumn(column);
        timedData.setValue(value);
        timedData.setExpiration(unit, time);
        register(timedData);
    }

    default boolean contains(String row, String column) {
        return find(row, column) != null;
    }

    default Object get(String row, String column) {
        return find(row, column).getValue();
    }

    default long getExpiration(String row, String column) throws NullPointerException {
        return find(row, column).getExpiration();
    }

    default RelationalDataDocument find(String row, String column) {
        if (findByRow(row) == null) return null;

        for (RelationalDataDocument data : findByRow(row)) {
            if (data.getColumn().equals(column)) return data;
        }

        return null;
    }

    default Set<RelationalDataDocument> findByRow(final String row) {
        return getRawData().values().stream().filter(model -> model.getRow().equals(row))
                .collect(Collectors.toSet());
    }

    default void remove(String row, String column) {
        if (find(row, column) != null) remove(find(row, column).getKey());
    }

    /**
     * Clears all expired timed value.
     */
    default void clearExpired() {
        getRawData().values().stream()
                .filter(model -> RelationalDataDocument.DataType.TIMED.equals(model.getDataType()) &&
                        model.getExpiration() <= System.currentTimeMillis()).map(RelationalDataDocument::getKey).
                toList().forEach(this::remove);
    }

    @Override
    default RelationalDataDocument fromDataSection(String stringKey, DocumentMap data) {
        return new RelationalDataDocument(stringKey, data);
    }
}
