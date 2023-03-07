package io.github.a5h73y.vehiclez.purchases;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.event.PurchaseUpgradeEvent;
import io.github.a5h73y.vehiclez.model.Car;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UpgradePurchase extends Purchasable {

	private static final String FROM_SPEED_PLACEHOLDER = "%FROM%";
	private static final String TO_SPEED_PLACEHOLDER = "%TO%";

	private final double previousTopSpeed;
	private final double newTopSpeed;

	/**
	 * Upgrade Car Purchase Request.
	 * Details are calculated based on the {@link Car} passed in.
	 *
	 * @param currentCar car
	 */
	public UpgradePurchase(Car currentCar) {
		double upgradeAmount = Vehiclez.getDefaultConfig().getUpgradeIncrement();
		previousTopSpeed = currentCar.getMaxSpeed();
		newTopSpeed = currentCar.getMaxSpeed() + upgradeAmount;
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Upgrade")
				.replace(FROM_SPEED_PLACEHOLDER, String.valueOf(previousTopSpeed))
				.replace(TO_SPEED_PLACEHOLDER, String.valueOf(newTopSpeed))
				.replace(COST_PLACEHOLDER, String.valueOf(getCost()))
				.replace(CURRENCY_PLACEHOLDER, Vehiclez.getInstance().getEconomyApi()
						.getCurrencyName(getCost()));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		Vehiclez.getInstance().getCarController().upgradeCarSpeed(player);
		TranslationUtils.sendTranslation("Purchase.Success.Upgrade", player);
		Bukkit.getServer().getPluginManager().callEvent(new PurchaseUpgradeEvent(player, this));
	}

	@Override
	protected double getDefaultCost() {
		return Vehiclez.getDefaultConfig().getDouble("Vault.Cost.Upgrade");
	}
}
