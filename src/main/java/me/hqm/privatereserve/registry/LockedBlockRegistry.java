package me.hqm.privatereserve.registry;

import com.demigodsrpg.util.LocationUtil;
import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Registry;
import me.hqm.privatereserve.model.LockedBlockModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        return switch (block.getType()) {
            case BEEHIVE, CAMPFIRE, SOUL_CAMPFIRE -> true;
            default -> block instanceof Container || block.getBlockData() instanceof Openable ||
                    block.getBlockData() instanceof Switch;
        };
    }

    default boolean isRegistered(Block block) {
        if (block == null) {
            return false;
        }
        if (isDoubleChest(block)) {
            return isRegisteredBisected(getDoubleChest(block));
        }
        if (isBisected(block)) {
            return isRegisteredBisected(getBisected(block));
        }
        return isRegistered0(block);
    }

    default boolean isRegistered0(Block block) {
        return fromKey(LocationUtil.stringFromLocation(block.getLocation())).isPresent();
    }

    default boolean isRegisteredBisected(List<Block> block) {
        boolean registered = false;
        for (Block block0 : block) {
            if (isRegistered0(block0)) {
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
            return getBisectedLockState(getDoubleChest(block));
        }
        if (isBisected(block)) {
            return getBisectedLockState(getBisected(block));
        }
        return getLockState0(block);
    }

    default LockState getLockState0(Block block) {
        Optional<LockedBlockModel> opModel = fromKey(LocationUtil.stringFromLocation(block.getLocation()));
        return opModel.isPresent() && opModel.get().isLocked() ? LockState.LOCKED : LockState.UNLOCKED;
    }

    default LockState getBisectedLockState(List<Block> blocks) {
        for (Block block : blocks) {
            if (getLockState0(block) == LockState.LOCKED) {
                return LockState.LOCKED;
            }
        }
        return LockState.UNLOCKED;
    }

    default LockState lockUnlock(Block block, Player player) {
        if (isDoubleChest(block)) {
            return bisectedLockUnlock(getDoubleChest(block), player);
        }
        if (isBisected(block)) {
            return bisectedLockUnlock(getBisected(block), player);
        }
        return lockUnlock0(block, player);
    }

    default LockState lockUnlock0(Block block, Player player) {
        Optional<LockedBlockModel> opModel = fromKey(LocationUtil.stringFromLocation(block.getLocation()));
        if (opModel.isPresent()) {
            LockedBlockModel model = opModel.get();
            if ((!isLockable(block) || model.getOwner().equals(player.getUniqueId().toString()) ||
                    player.hasPermission("privatereserve.bypasslock"))) {
                return model.setLocked(!model.isLocked()) ? LockState.LOCKED : LockState.UNLOCKED;
            } else {
                return LockState.UNCHANGED;
            }
        }
        return LockState.NO_LOCK;
    }

    default LockState bisectedLockUnlock(List<Block> blocks, Player player) {
        boolean unlocked = false;
        for (Block block : blocks) {
            LockState state = lockUnlock0(block, player);
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

    static List<Block> getSuroundingBlocks(Block block, boolean y) {
        List<Block> ret = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (y) {
                    for (int y0 = -1; y0 <= 1; y0++) {
                        ret.add(block.getRelative(x, y0, z));
                    }
                } else {
                    ret.add(block.getRelative(x, 0, z));
                }
            }
        }
        return ret;
    }

    static boolean isDoubleChest(Block block) {
        if (block.getType().equals(Material.CHEST)) {
            for (Block found : getSuroundingBlocks(block, false)) {
                if (found.getType().equals(Material.CHEST)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isBisected(Block block) {
        if (block.getBlockData() instanceof Bisected) {
            for (Block found : getSuroundingBlocks(block, true)) {
                if (found.getBlockData() instanceof Bisected) {
                    return true;
                }
            }
        }
        return false;
    }

    static List<Block> getDoubleChest(Block block) {
        return getSuroundingBlocks(block, false).stream().filter(found -> found.getType().equals(Material.CHEST)).
                collect(Collectors.toList());
    }

    static List<Block> getBisected(Block block) {
        return getSuroundingBlocks(block, true).stream().filter(found -> found.getBlockData() instanceof Bisected).
                collect(Collectors.toList());
    }

    @Override
    default LockedBlockModel fromDataSection(String stringKey, DataSection data) {
        return new LockedBlockModel(stringKey, data);
    }
}
