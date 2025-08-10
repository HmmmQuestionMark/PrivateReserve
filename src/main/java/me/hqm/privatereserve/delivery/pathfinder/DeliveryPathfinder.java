package me.hqm.privatereserve.delivery.pathfinder;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import me.hqm.privatereserve.delivery.old.data._DeliveryDocument;
import org.bukkit.entity.Mob;

import java.util.EnumSet;

public class DeliveryPathfinder implements Goal<Mob> {

    private Mob mob;
    private DeliveryAction currentAction;

    public _DeliveryDocument getMobModel()

    @Override
    public boolean shouldActivate() {
        return false;
    }

    @Override
    public GoalKey<Mob> getKey() {
        return null;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return null;
    }
}
