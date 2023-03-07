package io.github.a5h73y.vehiclez.configuration;

import io.github.a5h73y.vehiclez.configuration.impl.BlocksConfig;
import io.github.a5h73y.vehiclez.configuration.impl.DefaultConfig;
import io.github.a5h73y.vehiclez.configuration.impl.StringsConfig;
import io.github.a5h73y.vehiclez.enums.ConfigType;
import java.io.File;
import java.util.EnumMap;

/**
 * Vehiclez Configuration Manager.
 * Manages and stores references to each of the available Config files.
 */
public class ConfigManager {

	private final File dataFolder;

	private final EnumMap<ConfigType, VehiclezConfiguration> vehiclezConfigs = new EnumMap<>(ConfigType.class);

	/**
	 * Initialise the Config Manager.
	 * Will invoke setup for each available config type.
	 *
	 * @param dataFolder where to store the configs
	 */
	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		createVehiclezFolder();

		vehiclezConfigs.put(ConfigType.DEFAULT, new DefaultConfig());
		vehiclezConfigs.put(ConfigType.STRINGS, new StringsConfig());
		vehiclezConfigs.put(ConfigType.BLOCKS, new BlocksConfig());

		for (VehiclezConfiguration vehiclezConfig : vehiclezConfigs.values()) {
			vehiclezConfig.setupFile(dataFolder);
		}
	}

	/**
	 * Get matching VehiclezConfiguration for the ConfigType.
	 *
	 * @param type requested config type
	 * @return matching VehiclezConfiguration
	 */
	public VehiclezConfiguration get(ConfigType type) {
		return vehiclezConfigs.get(type);
	}

	/**
	 * Reload each of the configuration files.
	 */
	public void reloadConfigs() {
		for (VehiclezConfiguration vehiclezrConfig: vehiclezConfigs.values()) {
			vehiclezrConfig.reload();
		}
	}

	private void createVehiclezFolder() {
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}
}
