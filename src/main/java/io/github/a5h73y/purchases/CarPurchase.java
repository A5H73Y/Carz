package io.github.a5h73y.purchases;

import io.github.a5h73y.Carz;
import io.github.a5h73y.utility.CarUtils;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.entity.Player;

public class CarPurchase implements Purchasable {

	private static final String CAR_TYPE_PLACEHOLDER = "%TYPE%";

	private String carType;

	public CarPurchase(String carType) {
		this.carType = carType.toLowerCase();
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		assert Carz.getInstance().getCarController().doesCarTypeExist(carType);

		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Car")
				.replace(CAR_TYPE_PLACEHOLDER, carType)
				.replace(COST_PLACEHOLDER, String.valueOf(getCost()));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		CarUtils.givePlayerOwnedCar(player, carType);

		String successMessage = TranslationUtils.getTranslation("Purchase.Success.Car")
				.replace(CAR_TYPE_PLACEHOLDER, carType);

		player.sendMessage(successMessage);
	}

	@Override
	public double getCost() {
		return Carz.getInstance().getConfig().getDouble("CarTypes." + carType + ".Cost");
	}
}
