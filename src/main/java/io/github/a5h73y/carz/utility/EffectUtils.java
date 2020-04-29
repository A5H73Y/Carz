package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * {@link Effect} related utility methods.
 */
public class EffectUtils {

	/**
	 * Create an effect at the given location.
	 * Repeat the effect the amount of times specified.
	 *
	 * @param location target location
	 * @param effect the {@link Effect}
	 * @param repeat number of repetitions
	 */
	public static void createEffect(Location location, Effect effect, final int repeat) {
		if (!Carz.getInstance().getConfig().getBoolean("Other.UseEffects")) {
			return;
		}

		new BukkitRunnable() {
			int amount = Math.max(0, repeat);
			@Override
			public void run() {
				if (amount == 0) {
					cancel();
				}
				location.getWorld().playEffect(location, effect, 4);
				amount--;
			}
		}.runTaskTimer(Carz.getInstance(), 0, 10);
	}

	/**
	 * Create a SMOKE effect at the given location.
	 * Meant to look like damage has happened.
	 *
	 * @param car car to display effect on
	 */
	public static void createDamageEffect(Vehicle car) {
		createEffect(car.getLocation(), Effect.SMOKE, 2);
	}

	/**
	 * Create a MOBSPAWNER_FLAMES effect at the given location.
	 * Meant to look like the car has received an upgrade.
	 *
	 * @param car car to display effect on
	 */
	public static void createUpgradeEffect(Vehicle car) {
		createEffect(car.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
	}

	/**
	 * Play the requested Effect at the player's location.
	 *
	 * @param player target player
	 * @param effect the {@link Effect}
	 */
	public static void playEffect(Player player, Effect effect) {
		player.playEffect(player.getLocation(), effect, null);
	}
}
