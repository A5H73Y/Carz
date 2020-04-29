package io.github.a5h73y.carz.other;

import java.util.HashMap;
import java.util.Map;

import io.github.a5h73y.carz.Carz;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Delay sensitive tasks.
 */
public enum DelayTasks {

    INSTANCE;

    private final Map<String, Long> delays = new HashMap<>();

    DelayTasks() {
        initialiseCleanup();
    }

    public static DelayTasks getInstance() {
        return INSTANCE;
    }

    /**
     * Delay an event from firing several times.
     *
     * @param player target player
     * @return player able to perform event
     */
    public boolean delayPlayer(Player player, int secondsDelay) {
        if (!delays.containsKey(player.getName())) {
            delays.put(player.getName(), System.currentTimeMillis());
            return true;
        }

        long lastAction = delays.get(player.getName());
        int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

        if (secondsElapsed >= secondsDelay) {
            delays.put(player.getName(), System.currentTimeMillis());
            return true;
        }

        return false;
    }

    /**
     * Clear the cleanup cache every hour.
     * To keep the size of the map relatively small.
     */
    private void initialiseCleanup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                delays.clear();
            }
        }.runTaskTimer(Carz.getInstance(), 0, 3600000);
    }
}
