package io.github.a5h73y.carz.event;

import io.github.a5h73y.carz.purchases.CarPurchase;
import org.bukkit.entity.Player;

public class PurchaseCarEvent extends CarzEvent {

    private final CarPurchase purchase;

    public PurchaseCarEvent(Player player, CarPurchase purchase) {
        super(player);
        this.purchase = purchase;
    }

    public CarPurchase getPurchase() {
        return purchase;
    }
}
