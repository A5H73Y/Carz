package io.github.a5h73y.vehiclez.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.purchases.Purchasable;
import io.github.a5h73y.vehiclez.utility.PluginUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import java.util.HashMap;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * {@link Economy} integration.
 * When the EconomyAPI class is initialised, an attempt is made to connect to Vault / Economy.
 * If the outcome succeeds and a provider is found, economy will be enabled.
 * If Vehiclez does not link to a Economy plugin, all attempted purchases will be successful.
 */
public class EconomyApi extends PluginWrapper {

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

		if (isEnabled()) {
			RegisteredServiceProvider<Economy> economyProvider =
					getServer().getServicesManager().getRegistration(Economy.class);

			if (economyProvider == null) {
				PluginUtils.log("[Economy] Failed to connect to Vault's Economy service. Disabling Economy.", 2);
				setEnabled(false);
				return;
			}

			economy = economyProvider.getProvider();
		}
	}

	/**
	 * Check to see if the player is able to purchase the parameter.
	 * If Economy is disabled this will return true.
	 * If Economy is enabled it will query if the player has sufficient funds.
	 * Player will be sent a message if purchase would be unsuccessful.
	 *
	 * @param player requesting player
	 * @param cost cost of purchase
	 * @return player can purchase
	 */
	public boolean canPurchase(Player player, double cost) {
		return canPurchase(player, cost, true);
	}

	/**
	 * Check to see if the player is able to purchase the parameter.
	 * If Economy is disabled this will return true.
	 * If Economy is enabled it will query if the player has sufficient funds.
	 *
	 * @param player requesting player
	 * @param cost cost of purchase
	 * @param message display failure message
	 * @return player can purchase
	 */
	public boolean canPurchase(Player player, double cost, boolean message) {
		boolean success = true;

		if (isEnabled() && !economy.has(player, cost)) {
			success = false;

			if (message) {
				String currencyName = economy.currencyNamePlural() == null
						? "" : " " + economy.currencyNamePlural();

				TranslationUtils.sendValueTranslation("Error.PurchaseFailed",
						cost + currencyName, player);
			}
		}

		return success;
	}

	/**
	 * Request to make a purchase.
	 * Economy does not have to be enabled, each payment request will go into this flow.
	 * If confirmation is required, an entry will be made in the `purchasing` Map,
	 * requiring user action to confirm purchase.
	 *
	 * @param player requesting player
	 * @param purchasable {@link Purchasable}
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
		if (isEnabled() && Vehiclez.getDefaultConfig().getBoolean("Vault.ConfirmPurchases")) {
			purchasable.sendConfirmationMessage(player);
			purchasing.put(player.getName(), purchasable);

		} else {
			if (processPurchase(player, purchasable.getCost())) {
				purchasable.performPurchase(player);
			}
		}
	}

	/**
	 * Find the matching {@link Purchasable} for the player.
	 *
	 * @param player requesting player
	 * @return {@link Purchasable}
	 */
	public Purchasable getPurchasing(Player player) {
		return purchasing.get(player.getName());
	}

	/**
	 * Check to see if the player has an outstanding {@link Purchasable}.
	 *
	 * @param player requesting player
	 * @return player has outstanding {@link Purchasable}
	 */
	public boolean isPurchasing(Player player) {
		return purchasing.containsKey(player.getName());
	}

	/**
	 * Attempt to make the purchase of the parameter type for the player.
	 * If the validation check fails, no attempt to deduct the money will be made.
	 * If the attempt is unsuccessful, the amount of money required is displayed to the user.
	 *
	 * @param player requesting player
	 * @param price total payable amount
	 * @return purchase successful
	 */
	public boolean processPurchase(Player player, double price) {
		return purchase(player, price);
	}

	/**
	 * Get the currency name based on amount.
	 *
	 * @param amount requested amount
	 * @return matching currency name
	 */
	public String getCurrencyName(double amount) {
		String value = null;

		if (isEnabled()) {
			if (amount == 1.0) {
				value = economy.currencyNameSingular();
			} else {
				value = economy.currencyNamePlural();
			}
		}

		return value == null || value.isEmpty() ? "" : " " + value;
	}

	/**
	 * Process the purchase attempt.
	 * If economy is disabled, the purchase will succeed.
	 * If the player passes validation checks, an attempt will be made to withdraw the cost
	 * from the players bank.
	 *
	 * @param player requesting player
	 * @param cost total payable amount
	 * @return purchase successful
	 */
	private boolean purchase(Player player, double cost) {
		if (!isEnabled()) {
			return true;
		}

		if (!canPurchase(player, cost)) {
			return false;
		}

		EconomyResponse response = economy.withdrawPlayer(player, cost);
		return response.transactionSuccess();
	}

	/**
	 * Calculate the cost of refueling.
	 * If the settings enable cost scaling, use the remaining Car's fuel to determine the cost to fully refuel.
	 *
	 * @param remainingFuel car's remaining fuel
	 * @return refuel cost
	 */
	public double getRefuelCost(double remainingFuel) {
		double cost = Vehiclez.getDefaultConfig().getDouble("Vault.Cost.Refuel");

		if (Vehiclez.getDefaultConfig().isFuelScaleCost()) {
			cost *= Vehiclez.getInstance().getFuelController().determineScaleOfCostMultiplier(remainingFuel);
		}

		return cost;
	}

	/**
	 * Remove the player's current purchase request.
	 *
	 * @param player requesting player
	 */
	public void removePurchase(Player player) {
		purchasing.remove(player.getName());
	}

	/**
	 * Send the player a summary of the Economy information.
	 *
	 * @param sender requesting sender
	 */
	public void sendEconomyInformation(CommandSender sender) {
		TranslationUtils.sendHeading("Economy Details", sender);
		sender.sendMessage("Enabled: " + isEnabled());

		if (isEnabled()) {
			FileConfiguration config = Vehiclez.getDefaultConfig();
			sender.sendMessage("Economy: " + economy.getName());
			sender.sendMessage("Purchase Confirmation: " + config.getBoolean("Vault.ConfirmPurchases"));
			sender.sendMessage("Upgrade Cost: " + config.getDouble("Vault.Cost.Upgrade"));
			sender.sendMessage("Refuel Cost: " + config.getDouble("Vault.Cost.Refuel"));

			sender.sendMessage("CarTypes:");
			for (String carType : Vehiclez.getInstance().getCarController().getCarTypes().keySet()) {
				sender.sendMessage(carType + " cost: "
						+ config.getDouble("CarTypes." + carType + ".Cost"));
			}
		}
	}
}
