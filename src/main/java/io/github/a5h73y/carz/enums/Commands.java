package io.github.a5h73y.carz.enums;

/**
 * The configurable Commands.
 * Each can be configured to be enabled / disabled.
 */
public enum Commands {

    SPAWN("CommandEnabled.Spawn"),
    PURCHASE("CommandEnabled.Purchase"),
    REFUEL("CommandEnabled.Refuel"),
    UPGRADE("CommandEnabled.Upgrade"),
    STORE("CommandEnabled.Store"),
    GIVE("CommandEnabled.Give");

    private final String configPath;

    Commands(String configPath) {
        this.configPath = configPath;
    }

    public String getConfigPath() {
        return configPath;
    }
}
