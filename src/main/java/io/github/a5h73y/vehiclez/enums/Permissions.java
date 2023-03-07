package io.github.a5h73y.vehiclez.enums;

/**
 * All Vehiclez related permissions.
 */
public enum Permissions {

    ALL("Vehiclez.*"),
    ADMIN("Vehiclez.Admin"),

    PURCHASE("Vehiclez.Purchase"),
    UPGRADE("Vehiclez.Upgrade"),
    PLACE("Vehiclez.Place"),
    START("Vehiclez.Start"),
    GIVE("Vehiclez.Give"),

    CREATE_SIGN("Vehiclez.CreateSign"),
    BYPASS_OWNER("Vehiclez.BypassOwner");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
