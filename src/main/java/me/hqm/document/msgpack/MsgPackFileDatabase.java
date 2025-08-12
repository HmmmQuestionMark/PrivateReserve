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

package me.hqm.document.msgpack;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import me.hqm.document.DocumentCompatible;
import me.hqm.document.DocumentDatabase;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
public abstract class MsgPackFileDatabase<D extends DocumentCompatible> implements DocumentDatabase<D>, MsgPackFormat {
    protected final Cache<String, D> REGISTERED_DATA;

    // -- FILE -- //
    private final File FOLDER;

    public MsgPackFileDatabase(String path, String folder, int expireMins) {
        if (expireMins > 0) {
            REGISTERED_DATA =
                    CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(expireMins, TimeUnit.MINUTES)
                            .build();
        } else {
            REGISTERED_DATA = CacheBuilder.newBuilder().concurrencyLevel(4).build();
        }
        FOLDER = new File(path + "/" + folder + "/");
        FOLDER.mkdirs();
    }

    @Override
    public Optional<D> fromId(String id) {
        if (!REGISTERED_DATA.asMap().containsKey(id)) {
            load(id);
        }
        return Optional.ofNullable(REGISTERED_DATA.asMap().getOrDefault(id, null));
    }

    @Override
    public void write(D value) {
        REGISTERED_DATA.put(value.getId(), value);
        save(value.getId());
    }

    @Override
    public void add(D value) {
        REGISTERED_DATA.put(value.getId(), value);
        save(value.getId());
    }

    @Override
    public void remove(String key) {
        REGISTERED_DATA.asMap().remove(key);
        removeFile(key);
    }

    @Override
    public void save(String key) {
        if (REGISTERED_DATA.asMap().containsKey(key)) {
            File file = new File(FOLDER.getPath() + "/" + key + "." + format().getExt());
            if (!(file.exists())) {
                createFile(file);
            }
            try {
                byte[] byteArray = toRaw(REGISTERED_DATA.asMap().get(key).asMap());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                SnappyOutputStream snappyOs = new SnappyOutputStream(os);
                snappyOs.write(byteArray, 0, byteArray.length);
                snappyOs.close();
                ByteSink byteSink = Files.asByteSink(file);
                byteSink.write(os.toByteArray());
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }
    }

    @Override
    public void load(String key) {
        try {
            File file = new File(FOLDER.getPath() + "/" + key + "." + format().getExt());
            if (file.exists()) {
                ByteSource source = Files.asByteSource(file);
                ByteArrayInputStream is = new ByteArrayInputStream(source.read());
                SnappyInputStream snappyIs = new SnappyInputStream(is);
                Map<String, Object> document = fromRaw(snappyIs.readAllBytes());
                REGISTERED_DATA.put(key, createDocument(key, new MsgPackFileDocumentMap(document)));
            }
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void loadAll() {
        for (File file : FOLDER.listFiles()) {
            if (file.isFile() && file.getName().endsWith("." + format().getExt())) {
                String key = file.getName().replace("." + format().getExt(), "");
                load(key);
            }
        }
        REGISTERED_DATA.asMap();
    }

    @Override
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
        File file = new File(FOLDER.getPath() + "/" + key + "." + format().getExt());
        if (file.exists()) {
            file.delete();
        }
    }
}
