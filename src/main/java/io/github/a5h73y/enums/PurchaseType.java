package io.github.a5h73y.enums;

import io.github.a5h73y.Carz;

/**
 * All available Purchase Types.
 * Includes their User-friendly display counter parts.
 */
public enum PurchaseType {

    CAR("Purchase"),
    UPGRADE("Upgrade"),
    FUEL("Refuel");

    private final String purchaseKey;

    PurchaseType(String purchaseKey) {
        this.purchaseKey = purchaseKey;
    }

    /**
     * Calculate the cost of the Purchase.
     * Value is stored in the config.yml
     * @return
     */
    public double getCost() {
        return Carz.getInstance().getConfig().getDouble("Other.Vault.Cost." + this.purchaseKey, 0.0);
    }

    /**
     * Find the matching Purchase Type based on name.
     * @param purchaseKey
     * @return
     */
    public static PurchaseType fromString(String purchaseKey) {
        for (PurchaseType type : PurchaseType.values()) {
            if (type.purchaseKey.equals(purchaseKey)) {
                return type;
            }
        }
        throw new IllegalArgumentException("PurchaseType of " + purchaseKey + " not found.");
    }
}
