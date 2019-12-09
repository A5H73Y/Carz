package io.github.a5h73y.enums;

/**
 * The configurable Commands.
 * Each can be configured to be disabled.
 */
public enum Commands {

    SPAWN("Command.Spawn"),
    PURCHASE("Command.Purchase"),
    REFUEL("Command.Refuel"),
    UPGRADE("Command.Upgrade");

    private final String configPath;

    Commands(String configPath) {
        this.configPath = configPath;
    }

    public String getConfigPath() {
        return configPath;
    }
}
