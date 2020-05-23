package io.github.a5h73y.carz.configuration;

import java.io.File;
import java.util.EnumMap;

import io.github.a5h73y.carz.configuration.impl.BlocksConfig;
import io.github.a5h73y.carz.configuration.impl.DefaultConfig;
import io.github.a5h73y.carz.configuration.impl.StringsConfig;
import io.github.a5h73y.carz.enums.ConfigType;

public class ConfigManager {

	private final File dataFolder;

	private final EnumMap<ConfigType, CarzConfiguration> carzConfigs = new EnumMap<>(ConfigType.class);

	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		createCarzFolder();

		carzConfigs.put(ConfigType.DEFAULT, new DefaultConfig());
		carzConfigs.put(ConfigType.STRINGS, new StringsConfig());
		carzConfigs.put(ConfigType.BLOCKS, new BlocksConfig());

		for (CarzConfiguration carzConfig: carzConfigs.values()) {
			carzConfig.setupFile(dataFolder);
		}
	}

	private void createCarzFolder() {
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}

	public CarzConfiguration get(ConfigType type) {
		return carzConfigs.get(type);
	}

	public void reloadConfigs() {
		for (CarzConfiguration carzrConfig: carzConfigs.values()) {
			carzrConfig.reload();
		}
	}
}
