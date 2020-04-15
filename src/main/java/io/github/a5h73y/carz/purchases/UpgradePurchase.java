package io.github.a5h73y.carz.purchases;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.entity.Player;

public class UpgradePurchase extends Purchasable {

	private static final String FROM_SPEED_PLACEHOLDER = "%FROM%";
	private static final String TO_SPEED_PLACEHOLDER = "%TO%";

	private double previousTopSpeed;
	private double newTopSpeed;

	public UpgradePurchase(Car currentCar) {
		double upgradeAmount = Carz.getInstance().getSettings().getUpgradeIncrement();
		previousTopSpeed = currentCar.getMaxSpeed();
		newTopSpeed = currentCar.getMaxSpeed() + upgradeAmount;
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Upgrade")
				.replace(FROM_SPEED_PLACEHOLDER, String.valueOf(previousTopSpeed))
				.replace(TO_SPEED_PLACEHOLDER, String.valueOf(newTopSpeed))
				.replace(COST_PLACEHOLDER, String.valueOf(getCost()))
				.replace(CURRENCY_PLACEHOLDER,  Carz.getInstance().getEconomyAPI().getCurrencyName(getCost()));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		Carz.getInstance().getCarController().upgradeCarSpeed(player);
		TranslationUtils.sendTranslation("Purchase.Success.Upgrade", player);
	}

	@Override
	protected double getDefaultCost() {
		return Carz.getInstance().getConfig().getDouble("Vault.Cost.Upgrade");
	}
}
