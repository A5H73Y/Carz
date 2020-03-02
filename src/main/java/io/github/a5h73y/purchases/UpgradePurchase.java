package io.github.a5h73y.purchases;

import io.github.a5h73y.Carz;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.entity.Player;

public class UpgradePurchase implements Purchasable {

	private static final String FROM_SPEED_PLACEHOLDER = "%FROM%";
	private static final String TO_SPEED_PLACEHOLDER = "%TO%";

	private double previousTopSpeed;
	private double newTopSpeed;

	public UpgradePurchase(Car currentCar) {
		double upgradeAmount = Carz.getInstance().getConfig().getDouble("Speed.Upgrade.Increment");
		previousTopSpeed = currentCar.getMaxSpeed();
		newTopSpeed = currentCar.getMaxSpeed() + upgradeAmount;
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Upgrade")
				.replace(FROM_SPEED_PLACEHOLDER, String.valueOf(previousTopSpeed))
				.replace(TO_SPEED_PLACEHOLDER, String.valueOf(newTopSpeed));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		Carz.getInstance().getCarController().upgradeCarSpeed(player);
		TranslationUtils.sendTranslation("Purchase.Success.Upgrade", player);
	}

	@Override
	public double getCost() {
		return Carz.getInstance().getConfig().getDouble("Other.Vault.Cost.Upgrade");
	}
}
