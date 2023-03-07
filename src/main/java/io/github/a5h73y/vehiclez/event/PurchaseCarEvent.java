package io.github.a5h73y.vehiclez.event;

import io.github.a5h73y.vehiclez.purchases.CarPurchase;
import org.bukkit.entity.Player;

public class PurchaseCarEvent extends VehiclezEvent {

    private final CarPurchase purchase;

    public PurchaseCarEvent(Player player, CarPurchase purchase) {
        super(player);
        this.purchase = purchase;
    }

    public CarPurchase getPurchase() {
        return purchase;
    }
}
