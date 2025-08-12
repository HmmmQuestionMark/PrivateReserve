package me.hqm.privatereserve.lockedblock.data;

import me.hqm.document.Document;
import me.hqm.document.DocumentDatabase;
import me.hqm.privatereserve.Locations;
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

public interface LockedBlockDatabase extends DocumentDatabase<LockedBlock> {
    String NAME = "locked_blocks";

    static List<Block> getSurroundingBlocks(Block block, boolean y) {
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
            for (Block found : getSurroundingBlocks(block, false)) {
                if (found.getType().equals(Material.CHEST)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isBisected(Block block) {
        if (block.getBlockData() instanceof Bisected) {
            for (Block found : getSurroundingBlocks(block, true)) {
                if (found.getBlockData() instanceof Bisected) {
                    return true;
                }
            }
        }
        return false;
    }

    static List<Block> getDoubleChest(Block block) {
        return getSurroundingBlocks(block, false).stream().filter(found -> found.getType().equals(Material.CHEST)).
                collect(Collectors.toList());
    }

    static List<Block> getBisected(Block block) {
        return getSurroundingBlocks(block, true).stream().filter(found -> found.getBlockData() instanceof Bisected).
                collect(Collectors.toList());
    }

    default Optional<LockedBlock> fromLocation(Location location) {
        return fromId(Locations.stringFromLocation(location));
    }

    default boolean isLockable(Block block) {
        return switch (block.getType()) {
            case BEEHIVE, CAMPFIRE, SOUL_CAMPFIRE -> true;
            default -> block.getState() instanceof Container ||
                    block.getState().getBlockData() instanceof Openable ||
                    block.getState().getBlockData() instanceof Switch;
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
        return fromId(Locations.stringFromLocation(block.getLocation())).isPresent();
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
        Optional<LockedBlock> opModel = fromId(Locations.stringFromLocation(block.getLocation()));
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
        Optional<LockedBlock> opModel = fromId(Locations.stringFromLocation(block.getLocation()));
        if (opModel.isPresent()) {
            LockedBlock model = opModel.get();
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
            write(new LockedBlock(block.getLocation(), player.getUniqueId().toString()));
            return true;
        }
        return false;
    }

    default void delete(Block block) {
        Optional<LockedBlock> opModel = fromId(Locations.stringFromLocation(block.getLocation()));
        opModel.ifPresent(lockedBlock -> remove(lockedBlock.getId()));
    }

    @Override
    default LockedBlock createDocument(String stringKey, Document data) {
        return new LockedBlock(stringKey, data);
    }

    enum LockState {
        LOCKED, UNLOCKED, UNCHANGED, NO_LOCK
    }
}
