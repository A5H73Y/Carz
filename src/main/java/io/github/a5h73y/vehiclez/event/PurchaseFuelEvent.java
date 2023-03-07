package io.github.a5h73y.vehiclez.event;

import io.github.a5h73y.vehiclez.purchases.RefuelPurchase;
import org.bukkit.entity.Player;

public class PurchaseFuelEvent extends VehiclezEvent {

    private final RefuelPurchase purchase;

    public PurchaseFuelEvent(Player player, RefuelPurchase purchase) {
        super(player);
        this.purchase = purchase;
    }

    public RefuelPurchase getPurchase() {
        return purchase;
    }
}
