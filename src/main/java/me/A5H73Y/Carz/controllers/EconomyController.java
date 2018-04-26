package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.PurchaseType;
import me.A5H73Y.Carz.other.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Everything related to using an Economy plugin
 * If Carz does not link to a Economy plugin, all attempted purchases will be successful.
 */
public class EconomyController {

    private final Carz carz;
    private Economy economy;

    public boolean USE_ECONOMY = false;

    public EconomyController(Carz carz) {
        this.carz = carz;

        if (!carz.getConfig().getBoolean("Economy.Use"))
            return;

        USE_ECONOMY = canRegisterEconomy();
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
        return !USE_ECONOMY || economy.has(player, type.getCost());
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
            String currencyName = economy.currencyNamePlural() == null ?
                    "" : " " + economy.currencyNamePlural();

            player.sendMessage(Utils.getTranslation("Error.PurchaseFailed")
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
        if (!USE_ECONOMY)
            return true;

        if (!canPurchase(player, type))
            return false;

        EconomyResponse response = economy.withdrawPlayer(player, type.getCost());
        return response.transactionSuccess();
    }

    /**
     * When the EconomyController class is initialised, an attempt is made to connect to Economy
     * If the outcome succeeds and a provider is found, economy will be enabled
     * @return boolean
     */
    private boolean canRegisterEconomy() {
        Plugin vault = carz.getServer().getPluginManager().getPlugin("Vault");

        if (vault == null || !vault.isEnabled())
            return false;

        RegisteredServiceProvider<Economy> economyProvider =
                carz.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (economyProvider == null)
            return false;

        economy = economyProvider.getProvider();
        return economy != null;
    }
}
