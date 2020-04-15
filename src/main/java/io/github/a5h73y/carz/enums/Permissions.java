package io.github.a5h73y.carz.enums;

/**
 * All Carz related permissions.
 */
public enum Permissions {

    ALL("Carz.*"),
    ADMIN("Carz.Admin"),

    PURCHASE("Carz.Purchase"),
    UPGRADE("Carz.Upgrade"),
    PLACE("Carz.Place"),

    CREATE_SIGN("Carz.CreateSign"),
    BYPASS_OWNER("Carz.BypassOwner");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
