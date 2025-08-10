package me.hqm.privatereserve.relationaldata;

import org.bukkit.scheduler.BukkitRunnable;

public class ExpiredTask extends BukkitRunnable {
    @Override
    public void run() {
        RelationalData.RELATIONAL_DATA.clearExpired();
    }
}