package io.github.a5h73y.vehiclez.event;

import io.github.a5h73y.vehiclez.purchases.UpgradePurchase;
import org.bukkit.entity.Player;

public class PurchaseUpgradeEvent extends VehiclezEvent {

    private final UpgradePurchase purchase;

    public PurchaseUpgradeEvent(Player player, UpgradePurchase purchase) {
        super(player);
        this.purchase = purchase;
    }

    public UpgradePurchase getPurchase() {
        return purchase;
    }
}
