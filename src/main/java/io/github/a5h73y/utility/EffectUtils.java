package io.github.a5h73y.utility;

import io.github.a5h73y.Carz;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;

public class EffectUtils {

	public static void createEffect(Location location, Effect effect, final int repeat) {
		if (!Carz.getInstance().getConfig().getBoolean("Other.UseEffects")) {
			return;
		}

		new BukkitRunnable() {
			int amount = repeat;
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

	public static void createDamageEffect(Vehicle car) {
		createEffect(car.getLocation().add(0, 1, 0), Effect.SMOKE, 2);
	}

	public static void createUpgradeEffect(Vehicle car) {
		createEffect(car.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
	}

	public static void playEffect(Player player, Effect effect) {
		player.playEffect(player.getLocation(), effect, null);
	}

}
