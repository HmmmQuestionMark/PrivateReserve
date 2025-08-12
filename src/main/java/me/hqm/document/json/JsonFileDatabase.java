/*
 * Copyright (c) 2015 Demigods RPG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.hqm.document.json;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.document.DocumentDatabase;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class JsonFileDatabase<D extends DocumentCompatible> implements DocumentDatabase<D> {
    protected final Cache<String, D> REGISTERED_DATA;

    // -- FILE -- //
    private final File FOLDER;
    private final boolean PRETTY;

    public JsonFileDatabase(String path, String folder, boolean pretty, int expireMins) {
        if (expireMins > 0) {
            REGISTERED_DATA =
                    CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(expireMins, TimeUnit.MINUTES)
                            .build();
        } else {
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).build();
        }
        FOLDER = new File(path + "/" + folder + "/");
        FOLDER.mkdirs();
        PRETTY = pretty;
    }

    public Optional<D> fromId(String id) {
        if (!REGISTERED_DATA.asMap().containsKey(id)) {
            load(id);
        }
        return Optional.ofNullable(REGISTERED_DATA.asMap().getOrDefault(id, null));
    }

    public void add(D value) {
        REGISTERED_DATA.put(value.getId(), value);
        save(value.getId());
    }

    public void remove(String key) {
        REGISTERED_DATA.asMap().remove(key);
        removeFile(key);
    }

    public void save(String key) {
        if (REGISTERED_DATA.asMap().containsKey(key)) {
            write(REGISTERED_DATA.asMap().get(key));
        }
    }

    @Override
    public void write(D document) {
        File file = new File(FOLDER.getPath() + "/" + document.getId() + ".json");
        if (!(file.exists())) {
            createFile(file);
        }
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print(toString(document));
            writer.close();
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void load(String key) {
        try {
            File file = new File(FOLDER.getPath() + "/" + key + ".json");
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream);
                D document = read(reader);
                REGISTERED_DATA.put(key, createDocument(key, (Document) document));
                reader.close();
            }
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void loadAll() {
        for (File file : FOLDER.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                String key = file.getName().replace(".json", "");
                load(key);
            }
        }
        REGISTERED_DATA.asMap();
    }

    public void purge() {
        loadAll();
        REGISTERED_DATA.asMap().keySet().forEach(this::removeFile);
        REGISTERED_DATA.asMap().clear();
    }

    @Override
    public Map<String, D> getRawData() {
        return REGISTERED_DATA.asMap();
    }

    private void createFile(File file) {
        try {
            FOLDER.mkdirs();
            file.createNewFile();
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    public void removeFile(String key) {
        File file = new File(FOLDER.getPath() + "/" + key + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public String name() {
        return "JSON";
    }

    public String toString(D data) {
        Gson gson = PRETTY ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();
        return gson.toJson(data.asMap());
    }

    @Override
    public byte[] toRaw(D data) {
        return toString(data).getBytes();
    }

    public D read(Reader reader) {
        Gson gson = new GsonBuilder().create();
        return (D) new JsonFileDocumentMap(gson.fromJson(reader, Map.class));
    }

    public D fromString(String json) {
        StringReader reader = new StringReader(json);
        return read(reader);
    }

    @Override
    public D fromRaw(byte[] raw) {
        String rawString = new String(raw);
        StringReader reader = new StringReader(rawString);
        return read(reader);
    }
}
