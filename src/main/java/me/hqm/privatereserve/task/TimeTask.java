package me.hqm.privatereserve.task;

import me.hqm.privatereserve.Settings;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

// Based on https://github.com/magnusulf/LongerDays

public class TimeTask extends BukkitRunnable {

    public static final long DAY_LENGTH_TICKS = 24000;
    // The amount of moon phases minus one
    private final static int MOON_PHASES = 7;
    private int counter = 0;

    public static int getMultiplier(World world) {
        long time = world.getTime();
        if (isDay(time)) {
            return Settings.DAYLIGHT_MULTIPLIER.getInteger();
        }
        return Settings.NIGHT_MULTIPLIER.getInteger();
    }

    public static boolean isDay(long time) {
        return time < (DAY_LENGTH_TICKS / 2);
    }

    @Override
    public void run() {
        counter++;
        for (World w : Bukkit.getServer().getWorlds()) {
            this.worldChangeTime(w);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void worldChangeTime(World world) {
        // Not all worlds needs to get time changed
        if (Settings.TIME_MULTIPLIER_WORLDS.getStringList().contains(world.getName())) {
            if (world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) {

                // Here the counter is used.
                // If the multiplier is 3, the time would NOT be set back
                // one out of three times. Thus making the day three times longer
                if (counter % getMultiplier(world) != 0) {
                    // Here we actually change the time
                    world.setTime(world.getTime() - 1);

                    // To keep the moon phase in sync, we have to change the time a few times.
                    // We can't change it a whole day (24000 ticks). Because then the moon phase is not changed.
                    for (int i = 0; i < MOON_PHASES; i++) {
                        world.setTime(world.getTime() - 1);
                    }

                    // Because the time was changed slightly in the above code, keeping track of the moon phase.
                    // We have to change it back
                    world.setTime(world.getTime() + MOON_PHASES);
                }
            }
        }
    }
}
