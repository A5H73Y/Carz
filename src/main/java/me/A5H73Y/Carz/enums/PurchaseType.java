package me.A5H73Y.Carz.enums;

import me.A5H73Y.Carz.Carz;

public enum PurchaseType {

    CAR("Purchase"),
    UPGRADE("Upgrade"),
    FUEL("Refuel");

    private final String purchaseKey;

    PurchaseType(String purchaseKey) {
        this.purchaseKey = purchaseKey;
    }

    public double getCost() {
        return Carz.getInstance().getConfig().getDouble("Economy.Cost." + this.purchaseKey, 0.0);
    }

    public static PurchaseType fromString(String purchaseKey) {
        for (PurchaseType type : PurchaseType.values()) {
            if (type.purchaseKey.equals(purchaseKey)) {
                return type;
            }
        }
        throw new IllegalArgumentException("PurchaseType of " + purchaseKey + " not found.");
    }
}
