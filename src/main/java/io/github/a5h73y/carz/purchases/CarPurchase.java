package io.github.a5h73y.carz.purchases;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.event.PurchaseCarEvent;
import io.github.a5h73y.carz.utility.CarUtils;
import io.github.a5h73y.carz.utility.StringUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CarPurchase extends Purchasable {

	private static final String CAR_TYPE_PLACEHOLDER = "%TYPE%";

	private final String carType;

	/**
	 * Car Purchase Request.
	 * Details are calculated based on the car type requested.
	 *
	 * @param carType car type
	 */
	public CarPurchase(String carType) {
		this.carType = carType.toLowerCase();
	}

	@Override
	public void sendConfirmationMessage(Player player) {
		assert Carz.getInstance().getCarController().doesCarTypeExist(carType);

		String purchaseMessage = TranslationUtils.getTranslation("Purchase.Confirm.Car")
				.replace(CAR_TYPE_PLACEHOLDER, carType)
				.replace(COST_PLACEHOLDER, String.valueOf(getCost()))
				.replace(CURRENCY_PLACEHOLDER, Carz.getInstance().getEconomyAPI()
						.getCurrencyName(getCost()));

		player.sendMessage(purchaseMessage);
		TranslationUtils.sendTranslation(CONFIRM_PURCHASE_MESSAGE, false, player);
	}

	@Override
	public void performPurchase(Player player) {
		CarUtils.givePlayerCar(player, carType, true);
		TranslationUtils.sendValueTranslation("Purchase.Success.Car",
				StringUtils.standardizeText(carType), true, player);
		Bukkit.getServer().getPluginManager().callEvent(new PurchaseCarEvent(player, this));
	}

	@Override
	protected double getDefaultCost() {
		return Carz.getDefaultConfig().getDouble("CarTypes." + carType + ".Cost");
	}
}
