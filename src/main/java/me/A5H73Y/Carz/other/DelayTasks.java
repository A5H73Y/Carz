package me.A5H73Y.Carz.other;

import java.util.HashMap;
import java.util.Map;

import me.A5H73Y.Carz.Carz;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayTasks {

    private static DelayTasks instance = null;

    private Map<String, Long> delays = new HashMap<>();

    public static DelayTasks getInstance() {
        if (instance == null) {
            instance = new DelayTasks();
            instance.runCleanup();
        }

        return instance;
    }

    private DelayTasks() {
    }

    /**
     * Delay an event from firing several times
     * @param player
     * @return
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

    private void runCleanup() {
        // Tidy up the map every hour
        new BukkitRunnable() {
            @Override
            public void run() {
                delays.clear();
            }
        }.runTaskTimer(Carz.getInstance(), 0, 3600000);
    }
}
