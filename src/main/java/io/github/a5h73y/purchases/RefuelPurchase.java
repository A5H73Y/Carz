package io.github.a5h73y.purchases;

import io.github.a5h73y.Carz;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.entity.Player;

public class RefuelPurchase implements Purchasable {

	private static final String PERCENT_PLACEHOLDER = "%PERCENT%";

	private Car currentCar;

	public RefuelPurchase(Car currentCar) {
		this.currentCar = currentCar;
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		double fillPercent = Carz.getInstance().getFuelController().determineScaleOfCostMultiplier(currentCar.getCurrentFuel());
		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Refuel")
				.replace(PERCENT_PLACEHOLDER, String.valueOf(fillPercent))
				.replace(COST_PLACEHOLDER, String.valueOf(getCost()));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		Carz.getInstance().getFuelController().refuel(currentCar, player);
		TranslationUtils.sendTranslation("Purchase.Success.Refuel", player);
	}

	@Override
	public double getCost() {
		return Carz.getInstance().getEconomyAPI().getRefuelCost(currentCar);
	}
}
