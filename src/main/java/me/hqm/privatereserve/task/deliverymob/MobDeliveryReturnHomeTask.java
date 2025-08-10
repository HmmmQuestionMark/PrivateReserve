package me.hqm.privatereserve.task.deliverymob;

import com.destroystokyo.paper.entity.Pathfinder;
import io.papermc.paper.entity.TeleportFlag;
import me.hqm.privatereserve.PrivateReserve;
import me.hqm.privatereserve.model.MobDeliveryModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class MobDeliveryReturnHomeTask extends MobDeliveryMovementTask {

    public MobDeliveryReturnHomeTask(MobDeliveryModel model) {
        super(GeneralDeliveryTaskType.RETURN_HOME, model, model.getCurrentLocation(), model.getHomeLocation());
    }

    @Override
    public void start() {
        if (getMob().getBukkitEntity() instanceof Mob mob) {
            Bukkit.getMobGoals().removeAllGoals(mob);
            finder = mob.getPathfinder();
            Bukkit.getServer().broadcast(Component.text("Moving: " + finder.moveTo(finish)));
            Pathfinder.PathResult result = finder.findPath(finish);

            if (result != null && result.canReachFinalPoint() && finish.distance(result.getFinalPoint()) >= 4) {
                getDelivery().addPath(result);
                Bukkit.getServer().broadcast(Component.text("Moving: " + finder.moveTo(finish)));
                runTaskTimer(PrivateReserve.PLUGIN, 20, 20);
            } else {
                getMob().getBukkitEntity().teleport(finish, TeleportFlag.EntityState.RETAIN_PASSENGERS);
                getDelivery().clear();
                getMob().setActive(false);
            }
        }

        // NO IDEA
    }

    @Override
    public void stop(boolean last) {
        // Notify owner their mob is home.
        if (owner.isOnline()) {
            ((Player) owner).sendMessage(getMob().getName().
                    append(Component.text(" has returned home.", NamedTextColor.YELLOW)));
        }
        super.stop(last);
    }

    @Override
    public void run() {
        getMob().getBukkitEntity().eject();
        Pathfinder.PathResult result = finder.getCurrentPath();
        if (result == null || !result.canReachFinalPoint()) {
            getMob().getBukkitEntity().teleport(finish, TeleportFlag.EntityState.RETAIN_PASSENGERS);
            stop(true);
        } else if (result.getNextPoint() == null) {
            stop(true);
        } else {
            finder.moveTo(result.getNextPoint());
        }
    }

    @Override
    public MobDeliveryTask nextTask() {
        return null;
    }
}
