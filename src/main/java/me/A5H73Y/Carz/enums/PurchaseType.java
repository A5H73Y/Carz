package me.A5H73Y.Carz.enums;

import me.A5H73Y.Carz.Carz;

public enum PurchaseType {
    CAR("Purchase"),
    UPGRADE("Upgrade"),
    FUEL("Refuel");

    String purchaseKey;

    PurchaseType(String purchaseKey) {
        this.purchaseKey = purchaseKey;
    }

    public String getPurchaseKey() {
        return this.purchaseKey;
    }

    public double getCost() {
        return Carz.getInstance().getConfig().getDouble("Economy.Cost." + this.purchaseKey, 0);
    }
}
