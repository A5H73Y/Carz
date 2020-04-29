package io.github.a5h73y.carz.utility;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Player related utility methods.
 */
public class PlayerUtils {

	/**
	 * Get the Material in the player's hand.
	 *
	 * @param player target player
	 * @return Material
	 */
	public static Material getMaterialInPlayersHand(Player player) {
		return player.getInventory().getItemInMainHand().getType();
	}

	/**
	 * Reduce the number of item in hand by 1.
	 *
	 * @param player target player
	 */
	public static void reduceItemStackInPlayersHand(Player player) {
		player.getInventory().getItemInMainHand().setAmount(
				player.getInventory().getItemInMainHand().getAmount() - 1);
	}

}
