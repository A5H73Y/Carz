package io.github.a5h73y.utility;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {

	/**
	 * Made because < 1.8
	 * @param player
	 * @return ItemStack
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemStackInPlayersHand(Player player) {
		ItemStack stack;

		try {
			stack = player.getInventory().getItemInMainHand();
		} catch (NoSuchMethodError ex) {
			stack = player.getItemInHand();
		}

		return stack;
	}

	/**
	 * Get the Material in the player's hand.
	 * @param player
	 * @return Material
	 */
	public static Material getMaterialInPlayersHand(Player player) {
		return getItemStackInPlayersHand(player).getType();
	}

	/**
	 * Reduce the number of item in hand by 1.
	 * @param player
	 */
	public static void reduceItemStackInPlayersHand(Player player) {
		getItemStackInPlayersHand(player).setAmount(getItemStackInPlayersHand(player).getAmount() - 1);
	}

}
