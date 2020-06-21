package io.github.a5h73y.carz.purchases;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.event.PurchaseFuelEvent;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RefuelPurchase extends Purchasable {

	private static final String PERCENT_PLACEHOLDER = "%PERCENT%";

	private final Car currentCar;

	/**
	 * Refuel Car Purchase Request.
	 * Details are calculated based on the {@link Car} passed in.
	 *
	 * @param currentCar car
	 */
	public RefuelPurchase(Car currentCar) {
		this.currentCar = currentCar;
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		double fillPercent = Carz.getInstance().getFuelController()
				.determineScaleOfCostMultiplier(currentCar.getCurrentFuel());

		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Refuel")
				.replace(PERCENT_PLACEHOLDER, fillPercent * 100 + "%")
				.replace(COST_PLACEHOLDER, String.valueOf(getCost()))
				.replace(CURRENCY_PLACEHOLDER, Carz.getInstance().getEconomyApi()
						.getCurrencyName(getCost()));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		Carz.getInstance().getFuelController().refuel(currentCar);
		TranslationUtils.sendTranslation("Purchase.Success.Refuel", player);
		Bukkit.getServer().getPluginManager().callEvent(new PurchaseFuelEvent(player, this));
	}

	@Override
	protected double getDefaultCost() {
		return Carz.getInstance().getEconomyApi().getRefuelCost(currentCar.getCurrentFuel());
	}
}
