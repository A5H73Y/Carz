package io.github.a5h73y.vehiclez.enums;

/**
 * Each type of Vehiclez block.
 * Each can be configured to be enabled / disabled.
 */
public enum BlockType {

    CLIMB("ClimbBlocks", false),
    PLACEABLE("PlaceableBlocks", false),

    SPEED("SpeedBlocks", true),
    LAUNCH("LaunchBlocks", true);

    private final String configPath;
    private final boolean hasAmount;

    BlockType(String configPath, boolean hasAmount) {
        this.configPath = configPath;
        this.hasAmount = hasAmount;
    }

    public String getConfigPath() {
        return configPath;
    }

    public boolean isHasAmount() {
        return hasAmount;
    }
}
