package io.github.a5h73y.carz.event;

import io.github.a5h73y.carz.purchases.UpgradePurchase;
import org.bukkit.entity.Player;

public class PurchaseUpgradeEvent extends CarzEvent {

    private final UpgradePurchase purchase;

    public PurchaseUpgradeEvent(Player player, UpgradePurchase purchase) {
        super(player);
        this.purchase = purchase;
    }

    public UpgradePurchase getPurchase() {
        return purchase;
    }
}
