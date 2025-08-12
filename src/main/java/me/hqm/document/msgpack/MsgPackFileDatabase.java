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
import me.hqm.document.Document;
import me.hqm.document.DocumentCompatible;
import me.hqm.document.DocumentDatabase;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class MsgPackFileDatabase<D extends DocumentCompatible> implements DocumentDatabase<D> {
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

    public Optional<D> fromId(String id) {
        if (!REGISTERED_DATA.asMap().containsKey(id)) {
            load(id);
        }
        return Optional.ofNullable(REGISTERED_DATA.asMap().getOrDefault(id, null));
    }

    @Override
    public String name() {
        return "MESSAGEPACK";
    }

    @Override
    public byte[] toRaw(D document) {
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();

        try {
            serializeMap(packer, document.asMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packer.toMessageBuffer().array();
    }

    public void write(D value) {
        REGISTERED_DATA.put(value.getId(), value);
        save(value.getId());
    }

    @Override
    public D fromRaw(byte[] raw) {
        return (D) new MsgPackFileDocumentMap(raw);
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
            File file = new File(FOLDER.getPath() + "/" + key + ".mpk");
            if (!(file.exists())) {
                createFile(file);
            }
            try {
                byte[] byteArray = toRaw(REGISTERED_DATA.asMap().get(key));
                ByteSink byteSink = Files.asByteSink(file);
                byteSink.write(byteArray);
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }
    }

    public void load(String key) {
        try {
            File file = new File(FOLDER.getPath() + "/" + key + ".mpk");
            if (file.exists()) {
                ByteSource source = Files.asByteSource(file);
                D document = fromRaw(source.read());
                REGISTERED_DATA.put(key, createDocument(key, (Document) document));
            }
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void loadAll() {
        for (File file : FOLDER.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".mpk")) {
                String key = file.getName().replace(".mpk", "");
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
        File file = new File(FOLDER.getPath() + "/" + key + ".mpk");
        if (file.exists()) {
            file.delete();
        }
    }

    void serializeList(MessageBufferPacker packer, List<Object> list) throws IOException {
        packer.packArrayHeader(list.size());
        for (Object value : list) {
            serializeValue(packer, value);
        }
    }

    void serializeMap(MessageBufferPacker packer, Map<String, Object> map) throws IOException {
        packer.packMapHeader(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            packer.packString(entry.getKey());
            serializeValue(packer, entry.getValue());
        }
    }

    void serializeValue(MessageBufferPacker packer, Object value) throws IOException {
        switch (value) {
            case null -> packer.packNil();
            case String s -> packer.packString(s);
            case Integer i -> packer.packInt(i);
            case Boolean b -> packer.packBoolean(b);
            case Double d -> packer.packDouble(d);
            case Float f -> packer.packFloat(f);
            case Long l -> packer.packLong(l);
            case Short s -> packer.packShort(s);
            case Byte b -> packer.packByte(b);
            case List list -> serializeList(packer, (List<Object>) value);
            case Map map -> serializeMap(packer, (Map<String, Object>) value);
            default -> {
            }
        }
    }
}
