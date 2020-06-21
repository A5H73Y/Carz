package io.github.a5h73y.carz.configuration;

import io.github.a5h73y.carz.configuration.impl.BlocksConfig;
import io.github.a5h73y.carz.configuration.impl.DefaultConfig;
import io.github.a5h73y.carz.configuration.impl.StringsConfig;
import io.github.a5h73y.carz.enums.ConfigType;
import java.io.File;
import java.util.EnumMap;

/**
 * Carz Configuration Manager.
 * Manages and stores references to each of the available Config files.
 */
public class ConfigManager {

	private final File dataFolder;

	private final EnumMap<ConfigType, CarzConfiguration> carzConfigs = new EnumMap<>(ConfigType.class);

	/**
	 * Initialise the Config Manager.
	 * Will invoke setup for each available config type.
	 *
	 * @param dataFolder where to store the configs
	 */
	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		createCarzFolder();

		carzConfigs.put(ConfigType.DEFAULT, new DefaultConfig());
		carzConfigs.put(ConfigType.STRINGS, new StringsConfig());
		carzConfigs.put(ConfigType.BLOCKS, new BlocksConfig());

		for (CarzConfiguration carzConfig : carzConfigs.values()) {
			carzConfig.setupFile(dataFolder);
		}
	}

	/**
	 * Get matching CarzConfiguration for the ConfigType.
	 *
	 * @param type requested config type
	 * @return matching CarzConfiguration
	 */
	public CarzConfiguration get(ConfigType type) {
		return carzConfigs.get(type);
	}

	/**
	 * Reload each of the configuration files.
	 */
	public void reloadConfigs() {
		for (CarzConfiguration carzrConfig: carzConfigs.values()) {
			carzrConfig.reload();
		}
	}

	private void createCarzFolder() {
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}
}
