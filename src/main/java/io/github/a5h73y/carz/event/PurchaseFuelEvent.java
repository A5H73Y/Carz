package io.github.a5h73y.carz.event;

import io.github.a5h73y.carz.purchases.RefuelPurchase;
import org.bukkit.entity.Player;

public class PurchaseFuelEvent extends CarzEvent {

    private final RefuelPurchase purchase;

    public PurchaseFuelEvent(Player player, RefuelPurchase purchase) {
        super(player);
        this.purchase = purchase;
    }

    public RefuelPurchase getPurchase() {
        return purchase;
    }
}
