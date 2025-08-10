package me.hqm.privatereserve.delivery.old;

import me.hqm.privatereserve.Locations;
import com.destroystokyo.paper.entity.Pathfinder;
import me.hqm.privatereserve._PrivateReserve;
import me.hqm.privatereserve.delivery.data.DeliveryDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class MobDeliveryMovementTask extends MobDeliveryTask {
    Pathfinder finder;
    Location start, finish;

    public MobDeliveryMovementTask(DeliveryTaskType type, DeliveryDocument model, Location start, Location finish) {
        super(type, model);
        this.start = start;
        this.finish = finish;
    }

    public void start() {
        if (getMob().getBukkitEntity() instanceof Mob mob) {
            Bukkit.getMobGoals().removeAllGoals(mob);
            finder = mob.getPathfinder();
            finder.setCanFloat(true);
            //Pathfinder.PathResult result = finder.findPath(finish);
            _PrivateReserve.PLUGIN.getLogger().severe(Locations.prettyLocation(finish));

            //if(result != null) {
            //    PrivateReserve.PLUGIN.getLogger().severe("NOT NULL");
            //    if(result.canReachFinalPoint()) {
            //        PrivateReserve.PLUGIN.getLogger().severe("CAN REACH");
            //        getDelivery().addPath(result);
            //        Bukkit.getServer().broadcast(Component.text("Moving: " + finder.moveTo(finish)));
            runTaskTimer(_PrivateReserve.PLUGIN, 20, 20);
            //    } else {
            //        PrivateReserve.PLUGIN.getLogger().severe("CAN'T REACH");
            //         abort(true);
            //    }
            // } else {
            //    PrivateReserve.PLUGIN.getLogger().severe("NULL");
            //     abort(true);
            // }
            // return;
        }

        // PrivateReserve.PLUGIN.getLogger().severe("NOT PATHFINDER");

        // abort(true);
    }

    public void abort(boolean warn) {
        if (warn && owner.isOnline()) {
            ((Player) owner).sendMessage(getMob().getName().
                    append(Component.text(" couldn't complete their delivery. Returning home.", NamedTextColor.YELLOW)));
        }

        // RETURN HOME
        new MobDeliveryReturnHomeTask(getMob()).start();
    }

    public void stop(boolean last) {
        cancel();
        if (last) {
            _PrivateReserve._DELIVERY_DATA.clearChunkTicketsForDelivery(getDelivery());
            getDelivery().clear();
            getMob().setActive(false);
        }
    }

    @Override
    public void run() {
        getMob().getBukkitEntity().eject();
        if (!getMob().isActive()) {
            getMob().setActive(true);
            abort(false);
            stop(false);
            return;
        }
        Pathfinder.PathResult result = finder.getCurrentPath();
        //if(result.getNextPoint() == null) {
        //    if (nextTask() != null) {
        //        stop(false);
        //        MobDeliveryTask next = nextTask();
        //        next.start();
        //    } else {
        //        stop(true);
        //    }
        //} else {
        if (getMob().getBukkitEntity() instanceof Mob mob) {
            Bukkit.getMobGoals().removeAllGoals(mob);
            finder = mob.getPathfinder();
            finder.setCanFloat(true);
            Bukkit.getServer().broadcast(Component.text("Moving: " + finder.moveTo(finish)));
        }
        //}
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("start", Locations.stringFromLocation(start));
        data.put("finish", Locations.stringFromLocation(finish));
        return data;
    }
}