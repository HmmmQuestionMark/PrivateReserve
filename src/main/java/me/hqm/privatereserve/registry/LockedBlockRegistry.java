package me.hqm.privatereserve.registry;

import com.demigodsrpg.util.LocationUtil;
import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.privatereserve.model.LockedBlockModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public interface LockedBlockRegistry extends Registry<LockedBlockModel> {
    String NAME = "locked_blocks";

    enum LockState {
        LOCKED, UNLOCKED, UNCHANGED, NO_LOCK
    }

    default Optional<LockedBlockModel> fromLocation(Location location) {
        return fromKey(LocationUtil.stringFromLocation(location));
    }

    default boolean isLockable(Block block) {
        switch (block.getType()) {
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case DARK_OAK_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case IRON_DOOR:
            case OAK_DOOR:
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case IRON_TRAPDOOR:
            case DISPENSER:
            case FURNACE:
            case BLAST_FURNACE:
            case FURNACE_MINECART:
            case CHEST:
            case TRAPPED_CHEST:
            case HOPPER:
            case HOPPER_MINECART:
            case CHEST_MINECART:
            case STONE_BUTTON:
            case BIRCH_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case LEVER:
                return true;
        }
        return false;
    }

    default boolean isRegistered(Block block) {
        if (block == null) {
            return false;
        }
        if (isDoubleChest(block)) {
            return isRegisteredDoubleChest(getDoubleChest(block));
        }
        return isRegistered0(block);
    }

    default boolean isRegistered0(Block block) {
        return fromKey(LocationUtil.stringFromLocation(block.getLocation())).isPresent();
    }

    default boolean isRegisteredDoubleChest(List<Block> chests) {
        boolean registered = false;
        for (Block chest : chests) {
            if (isRegistered0(chest)) {
                registered = true;
            }
        }
        return registered;
    }

    default LockState getLockState(Block block) {
        if (block == null) {
            return LockState.UNLOCKED;
        }
        if (isDoubleChest(block)) {
            return getDoubleChestLockState(getDoubleChest(block));
        }
        return getLockState0(block);
    }

    default LockState getLockState0(Block block) {
        Optional<LockedBlockModel> opModel = fromKey(LocationUtil.stringFromLocation(block.getLocation()));
        return opModel.isPresent() && opModel.get().isLocked() ? LockState.LOCKED : LockState.UNLOCKED;
    }

    default LockState getDoubleChestLockState(List<Block> chests) {
        for (Block chest : chests) {
            if (getLockState0(chest) == LockState.LOCKED) {
                return LockState.LOCKED;
            }
        }
        return LockState.UNLOCKED;
    }

    default LockState lockUnlock(Block block, Player player) {
        if (isDoubleChest(block)) {
            return doubleChestLockUnlock(getDoubleChest(block), player);
        }
        return lockUnlock0(block, player);
    }

    default LockState lockUnlock0(Block block, Player player) {
        Optional<LockedBlockModel> opModel = fromKey(LocationUtil.stringFromLocation(block.getLocation()));
        if (opModel.isPresent()) {
            LockedBlockModel model = opModel.get();
            if ((!isLockable(block) || model.getOwner().equals(player.getUniqueId().toString()) ||
                    player.hasPermission("seasons.bypasslock"))) {
                return model.setLocked(!model.isLocked()) ? LockState.LOCKED : LockState.UNLOCKED;
            } else {
                return LockState.UNCHANGED;
            }
        }
        return LockState.NO_LOCK;
    }

    default LockState doubleChestLockUnlock(List<Block> chests, Player player) {
        boolean unlocked = false;
        for (Block chest : chests) {
            LockState state = lockUnlock0(chest, player);
            if (state == LockState.LOCKED) {
                return LockState.LOCKED;
            } else if (state == LockState.UNCHANGED) {
                return LockState.UNCHANGED;
            } else if (state == LockState.UNLOCKED) {
                unlocked = true;
            }
        }
        return unlocked ? LockState.UNLOCKED : LockState.NO_LOCK;
    }

    default boolean create(Block block, Player player) {
        if (isLockable(block) && !isRegistered(block)) {
            register(new LockedBlockModel(block.getLocation(), player.getUniqueId().toString()));
            return true;
        }
        return false;
    }

    default void delete(Block block) {
        Optional<LockedBlockModel> opModel = fromKey(LocationUtil.stringFromLocation(block.getLocation()));
        if (opModel.isPresent()) {
            remove(opModel.get().getKey());
        }
    }

    static List<Block> getSuroundingBlocks(Block block) {
        List<Block> ret = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block loc = block.getRelative(x, y, z);
                    ret.add(loc);
                }
            }
        }
        return ret;
    }

    static boolean isDoubleChest(Block block) {
        if (block.getType().equals(Material.CHEST)) {
            for (Block found : getSuroundingBlocks(block)) {
                if (found.getType().equals(Material.CHEST)) {
                    return true;
                }
            }
        }
        return false;
    }

    static List<Block> getDoubleChest(Block block) {
        return getSuroundingBlocks(block).stream().filter(found -> found.getType().equals(Material.CHEST)).
                collect(Collectors.toList());
    }

    @Override
    default LockedBlockModel fromDataSection(String stringKey, DataSection data) {
        return new LockedBlockModel(stringKey, data);
    }
}
