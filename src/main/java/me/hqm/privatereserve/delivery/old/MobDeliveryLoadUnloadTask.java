package me.hqm.privatereserve.delivery.old;

import me.hqm.privatereserve.Locations;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.delivery.data.DeliveryMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Deprecated
@ApiStatus.Obsolete
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public abstract class MobDeliveryLoadUnloadTask extends MobDeliveryTask {

    final Long wait;
    final State state;
    final List<InventoryHolder> holders;

    public MobDeliveryLoadUnloadTask(DeliveryTaskType type, DeliveryMob model) {
        super(type, model);
        state = State.valueOf(type.name());
        wait = state == State.LOAD ? model.getLoadTicks() : model.getUnloadTicks();
        holders = model.getInventoryHolders();
    }

    public void start() {
        if (!holders.isEmpty()) {
            Location location = getMob().getCurrentLocation();
            Optional<Block> maybe = Locations.nearestOfType(location, 4, Material.HOPPER, Material.HOPPER_MINECART);
            if (maybe.isPresent()) {
                getMob().setFrozen(true);
                runTaskLater(PrivateReserve.plugin(), wait);
            }
        }

        abort(true);
    }

    public void abort(boolean warn) {
        if (warn && owner.isOnline()) {
            ((Player) owner).sendMessage(getMob().getName().
                    append(Component.text(" couldn't " + state.name().toLowerCase() + ", so they're moving on.", NamedTextColor.YELLOW)));
        }

        // Move on
        nextTask().start();
    }

    public void abortHome(boolean warn) {
        stop(false);
        if (warn && owner.isOnline()) {
            ((Player) owner).sendMessage(getMob().getName().
                    append(Component.text(" couldn't complete their delivery. Returning home.", NamedTextColor.YELLOW)));
        }

        // RETURN HOME
        new MobDeliveryReturnHomeTask(getMob()).start();
    }

    public void stop(boolean last) {
        getMob().setFrozen(false);
        if (last) {
            getMob().setActive(false);
        }
        cancel();
    }

    @Override
    public void run() {
        getMob().getBukkitEntity().eject();
        if (!getMob().isActive()) {
            getMob().setActive(true);
            abortHome(false);
            return;
        }
        if (nextTask() != null) {
            stop(false);
            MobDeliveryTask next = nextTask();
            next.start();
        } else {
            abortHome(true);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("wait", wait);
        data.put("state", state.name());
        return data;
    }

    enum State {
        LOAD, UNLOAD
    }
}