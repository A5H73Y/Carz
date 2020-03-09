package io.github.a5h73y.plugin;

import java.util.HashMap;
import java.util.Map;

import io.github.a5h73y.Carz;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.other.PluginUtils;
import io.github.a5h73y.purchases.Purchasable;
import io.github.a5h73y.utility.StringUtils;
import io.github.a5h73y.utility.TranslationUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

/**
 * When the EconomyAPI class is initialised, an attempt is made to connect to Vault / Economy.
 * If the outcome succeeds and a provider is found, economy will be enabled.
 * If Carz does not link to a Economy plugin, all attempted purchases will be successful.
 */
public class EconomyAPI extends PluginWrapper {

	private Economy economy;
	// player name to purchasable
	private final Map<String, Purchasable> purchasing = new HashMap<>();

	@Override
	public String getPluginName() {
		return "Vault";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (enabled) {
			RegisteredServiceProvider<Economy> economyProvider =
					getServer().getServicesManager().getRegistration(Economy.class);

			if (economyProvider == null) {
				PluginUtils.log("[Economy] Carz failed to connect to Economy service. Disabling Economy.", 2);
				enabled = false;
				return;
			}

			economy = economyProvider.getProvider();
		}
	}

	/**
	 * Check to see if the player is able to purchase the parameter.
	 * If Economy is disabled this will return true
	 * If Economy is enabled it will query if the player has sufficient funds
	 * @param player
	 * @param cost
	 * @return boolean
	 */
	public boolean canPurchase(Player player, double cost) {
		return canPurchase(player, cost, false);
	}

	public boolean canPurchase(Player player, double cost, boolean message) {
		boolean success = true;

		if (enabled && !economy.has(player, cost)) {
			success = false;

			if (message) {
				String currencyName = economy.currencyNamePlural() == null
						? "" : " " + economy.currencyNamePlural();

				player.sendMessage(
						TranslationUtils.getTranslation("Error.PurchaseFailed")
								.replace("%COST%", cost + currencyName));
			}
		}

		return success;
	}

	/**
	 * Request to make a purchase.
	 * Economy does not have to be enabled, each payment request will go into this flow.
	 * If confirmation is required, an entry will be made in the `purchasing` Map, requiring user action to confirm purchase.
	 *
	 * @param player
	 * @param purchasable
	 */
	public void requestPurchase(Player player, Purchasable purchasable) {
		if (!canPurchase(player, purchasable.getCost())) {
			return;
		}

		if (isPurchasing(player)) {
			TranslationUtils.sendTranslation("Error.PurchaseOutstanding", player);
			TranslationUtils.sendTranslation("Purchase.Confirm.Purchase", player);
			return;
		}

		// if the user has to confirm their purchases
		if (enabled && Carz.getInstance().getConfig().getBoolean("Other.Vault.ConfirmPurchases")) {
			purchasable.sendConfirmationMessage(player);
			purchasing.put(player.getName(), purchasable);

		} else {
			if (processPurchase(player, purchasable.getCost())) {
				purchasable.performPurchase(player);
			}
		}
	}

	public Purchasable getPurchasing(Player player) {
		return purchasing.get(player.getName());
	}

	public boolean isPurchasing(Player player) {
		return purchasing.containsKey(player.getName());
	}

	/**
	 * Attempt to make the purchase of the parameter type for the player.
	 * If the validation check fails, no attempt to deduct the money will be made.
	 * If the attempt is unsuccessful, the amount of money required is displayed to the user.
	 * @param player
	 * @param price
	 * @return purchase successful
	 */
	public boolean processPurchase(Player player, double price) {
		return purchase(player, price);
	}

	/**
	 * Get the currency name based on amount.
	 * @param amount
	 * @return currency name
	 */
	public String getCurrencyName(double amount) {
		String value;

		if (amount == 1.0) {
			value = economy.currencyNameSingular();
		} else {
			value = economy.currencyNamePlural();
		}

		return value == null ? "" : " " + value;
	}

	/**
	 * Process the purchase attempt.
	 * If economy is disabled, the purchase will succeed
	 * If the player passes validation checks, an attempt will be made to withdraw the cost
	 * from the players bank.
	 * @param player
	 * @param cost
	 * @return purchase successful
	 */
	private boolean purchase(Player player, double cost) {
		if (!enabled) {
			return true;
		}

		if (!canPurchase(player, cost, true)) {
			return false;
		}

		EconomyResponse response = economy.withdrawPlayer(player, cost);
		return response.transactionSuccess();
	}

	/**
	 * Calculate the cost of refueling.
	 * If the settings enable cost scaling, use the remaining Car's fuel to determine the cost to fully refuel.
	 * @param car
	 * @return refuel cost
	 */
	public double getRefuelCost(Car car) {
		double cost = Carz.getInstance().getConfig().getDouble("Other.Vault.Cost.Refuel");

		if (Carz.getInstance().getSettings().isFuelScaleCost()) {
			cost *= Carz.getInstance().getFuelController().determineScaleOfCostMultiplier(car.getCurrentFuel());
		}

		return cost;
	}

	/**
	 * Remove the player from the purchasing map.
	 * @param player
	 */
	public void removePurchase(Player player) {
		purchasing.remove(player.getName());
	}

	public void sendInformation(Player player) {
		player.sendMessage(StringUtils.getStandardHeading("Economy Details"));
		player.sendMessage("Enabled: " + enabled);

		if (enabled) {
			FileConfiguration config = Carz.getInstance().getConfig();
			player.sendMessage("Economy: " + economy.getName());
			player.sendMessage("Purchase Confirmation: " + config.getBoolean("Other.Vault.ConfirmPurchases"));
			player.sendMessage("Upgrade Cost: " + config.getDouble("Other.Vault.Cost.Upgrade"));
			player.sendMessage("Refuel Cost: " + config.getDouble("Other.Vault.Cost.Refuel"));

			player.sendMessage("CarTypes:");
			for (String carType : Carz.getInstance().getCarController().getCarTypes().keySet()) {
				player.sendMessage(carType + " cost: " + config.getDouble("CarTypes." + carType + ".Cost"));
			}
		}
	}
}
