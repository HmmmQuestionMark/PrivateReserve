package me.hqm.privatereserve.runnable;

import me.hqm.privatereserve.Setting;
import org.bukkit.*;

// Based on https://github.com/magnusulf/LongerDays

public class TimeRunnable implements Runnable {

    // The amount of moon phases minus one
    private final static int MOON_PHASES = 7;
    public static long DAY_LENGTH_TICKS = 24000;

    private int counter = 0;

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
        if (Setting.TIME_MULTIPLIER_WORLDS.contains(world.getName())) {
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

    public static int getMultiplier(World world) {
        long time = world.getTime();
        if (isDay(time)) {
            return Setting.DAYLIGHT_MULTIPLIER;
        }
        return Setting.NIGHT_MULTIPLIER;
    }

    public static boolean isDay(long time) {
        return time < (DAY_LENGTH_TICKS / 2);
    }
}
