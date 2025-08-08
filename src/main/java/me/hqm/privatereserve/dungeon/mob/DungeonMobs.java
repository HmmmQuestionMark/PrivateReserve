package me.hqm.privatereserve.dungeon.mob;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import me.hqm.privatereserve.dungeon.mob.boss.Skeletor;
import me.hqm.privatereserve.dungeon.mob.easy.EvilSquid;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonMobs {

    // -- EVIL MOBS -- //

    public static final EvilSquid EVIL_SQUID = new EvilSquid();

    // -- BOSS MOBS -- //

    public static final Skeletor SKELETOR = new Skeletor();

    // -- MOB TRACKING MAP -- //

    static final Multimap<String, String> trackedMobs = Multimaps.newListMultimap(new ConcurrentHashMap<>(),
            ArrayList::new);

    // -- MOB LIST -- //

    private static final DungeonMob[] mobList = new DungeonMob[]{
            EVIL_SQUID, SKELETOR
    };

    // -- PRIVATE CONSTRUCTOR -- //

    private DungeonMobs() {
    }

    // -- HELPER METHODS -- //

    public static DungeonMob[] values() {
        return mobList;
    }

    public static DungeonMob valueOf(final String name) {
        if (name != null) {
            for (DungeonMob mob : mobList) {
                if (PlainTextComponentSerializer.plainText().serialize(mob.getName()).equalsIgnoreCase(name)) {
                    return mob;
                }
            }
        }
        return null;
    }

    public static List<LivingEntity> getMobs(DungeonMob type) {
        List<LivingEntity> mobs = new ArrayList<>();
        for (String id : trackedMobs.get(PlainTextComponentSerializer.plainText().serialize(type.getName()))) {
            for (World world : Bukkit.getWorlds()) {
                Optional<Entity> maybeEntity = world.getEntities().stream().filter(entity -> entity.getUniqueId().
                        toString().equals(id)).findAny();
                maybeEntity.ifPresent(entity -> mobs.add((LivingEntity) entity));
            }
        }
        return mobs;
    }

    public static Optional<DungeonMob> getType(Entity entity) {
        String entityId = entity.getUniqueId().toString();
        if (trackedMobs.containsValue(entityId)) {
            for (String type : trackedMobs.keySet()) {
                if (trackedMobs.get(type).contains(entityId)) {
                    return Optional.ofNullable(valueOf(type));
                }
            }
        }
        return Optional.empty();
    }

    public static boolean isTracked(Entity entity) {
        return trackedMobs.containsValue(entity.getUniqueId().toString());
    }

    public static LivingEntity spawnDungeonMob(Location location, DungeonMob mobType) {
        LivingEntity entity = mobType.spawnRaw(location);
        trackedMobs.put(PlainTextComponentSerializer.plainText().serialize(mobType.getName()), entity.getUniqueId().toString());
        return entity;
    }
}
