package io.github.a5h73y.carz.utility;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Player related utility methods.
 */
public class PlayerUtils {

	/**
	 * Get the Material in the player's hand.
	 *
	 * @param player target player
	 * @return {@link Material}
	 */
	public static Material getMaterialInPlayersHand(Player player) {
		return player.getInventory().getItemInMainHand().getType();
	}

	/**
	 * Transfer ItemStack from Player's main hand to target Player.
	 *
	 * @param from from Player
	 * @param to to Player
	 */
	public static void transferItemStackToDifferentPlayer(Player from, Player to) {
		ItemStack itemStack = from.getInventory().getItemInMainHand().clone();
		to.getInventory().addItem(itemStack);
		from.getInventory().getItemInMainHand().setAmount(0);
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
