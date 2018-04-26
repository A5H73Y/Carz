package me.A5H73Y.Carz.enums;

public enum Permissions {

    ALL("Carz.*"),
    ADMIN("Carz.Admin"),

    PURCHASE("Carz.Purchase"),
    UPGRADE("Carz.Upgrade"),
    PLACE("Carz.Place"),

    CREATE_SIGN("Carz.CreateSign");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
