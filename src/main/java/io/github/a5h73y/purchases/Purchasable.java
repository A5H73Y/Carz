package io.github.a5h73y.purchases;

import org.bukkit.entity.Player;

/**
 * Purchasable Item.
 * Currently includes a car, upgrade and refuel purchase.
 * Cost is calculated at the time of purchase request, and once again at the time of confirmation,
 * this ensures that the user still has the amount they had at the time of request and prevents cheating.
 * Even if Economy is disabled, or purchase request is disabled, the `performPurchase(player)` is invoked every time.
 */
public interface Purchasable {

	String COST_PLACEHOLDER = "%COST%";

	String CONFIRM_PURCHASE_MESSAGE = "Purchase.Confirm.Purchase";

	/**
	 * Send the player a summary of the purchase.
	 * This is contextual based on the type of purchase.
	 * This will only be invoked if purchase confirmation is enabled in the settings.
	 * @param player
	 */
	void sendConfirmationMessage(Player player);

	/**
	 * The player has confirmed the purchase or confirmation wasn't necessary.
	 * Purchase action is executed, which is contextual based on the type of purchase.
	 * This will be invoked regardless of settings.
	 * @param player player
	 */
	void performPurchase(Player player);

	/**
	 * Calculate the cost of the purchase.
	 * @return total cost
	 */
	double getCost();

}
