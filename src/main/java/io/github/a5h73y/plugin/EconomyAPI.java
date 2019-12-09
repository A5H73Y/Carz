package io.github.a5h73y.plugin;

import io.github.a5h73y.enums.PurchaseType;
import io.github.a5h73y.other.Utils;
import io.github.a5h73y.utility.TranslationUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
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

	@Override
	String getPluginName() {
		return "Vault";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (enabled) {
			RegisteredServiceProvider<Economy> economyProvider =
					getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

			if (economyProvider == null) {
				Utils.log("[Economy] Parkour failed to connect to Economy service. Disabling Economy.", 2);
				enabled = false;
				return;
			}

			economy = economyProvider.getProvider();
		}
	}

	/**
	 * Check to see if the player is able to purchase the parameter
	 * If Economy is disabled this will return true
	 * If Economy is enabled it will query if the player has sufficient funds
	 * @param player
	 * @param type
	 * @return boolean
	 */
	public boolean canPurchase(Player player, PurchaseType type) {
		return !enabled || economy.has(player, type.getCost());
	}

	/**
	 * Attempt to make the purchase of the parameter type for the player.
	 * If the validation check fails, no attempt to deduct the money will be made.
	 * If the attempt is unsuccessful, the amount of money required is displayed to the user.
	 * @param player
	 * @param type
	 * @return boolean
	 */
	public boolean processPurchase(Player player, PurchaseType type) {
		boolean success = purchase(player, type);

		if (!success) {
			String currencyName = economy.currencyNamePlural() == null
					? "" : " " + economy.currencyNamePlural();

			player.sendMessage(
					TranslationUtils.getTranslation("Error.PurchaseFailed")
							.replace("%COST%", type.getCost() + currencyName));
		}

		return success;
	}

	/**
	 * Process the purchase attempt
	 * If economy is disabled, the purchase will succeed
	 * If the player passes validation checks, an attempt will be made to withdraw the cost
	 * from the players bank.
	 * @param player
	 * @param type
	 * @return boolean
	 */
	private boolean purchase(Player player, PurchaseType type) {
		if (!enabled) {
			return true;
		}

		if (!canPurchase(player, type)) {
			return false;
		}

		EconomyResponse response = economy.withdrawPlayer(player, type.getCost());
		return response.transactionSuccess();
	}
}
