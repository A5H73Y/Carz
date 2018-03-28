package me.A5H73Y.Carz.enums;

public enum Commands {
    SPAWN("Command.Spawn"),
    PURCHASE("Command.Purchase"),
    REFUEL("Command.Refuel"),
    UPGRADE("Command.Upgrade");

    String configPath;

    Commands(String configPath) {
        this.configPath = configPath;
    }

    public String getConfigPath() {
        return configPath;
    }
}