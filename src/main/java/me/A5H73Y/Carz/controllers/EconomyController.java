package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.PurchaseType;
import me.A5H73Y.Carz.other.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

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

    public boolean canPurchase(Player player, PurchaseType type) {
        return !USE_ECONOMY || economy.has(player, type.getCost());
    }

    public boolean processPurchase(Player player, PurchaseType type) {
        boolean success = purchase(player, type);

        if (!success) {
            player.sendMessage(Utils.getTranslation("Error.PurchaseFailed")
                    .replace("%COST%", String.valueOf(type.getCost())));
        }

        return success;
    }

    private boolean purchase(Player player, PurchaseType type) {
        if (!USE_ECONOMY)
            return true;

        if (!canPurchase(player, type))
            return false;

        EconomyResponse response = economy.withdrawPlayer(player, type.getCost());
        return response.transactionSuccess();
    }

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
