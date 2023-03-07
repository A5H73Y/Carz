package io.github.a5h73y.vehiclez;

import io.github.a5h73y.vehiclez.commands.VehiclezAutoTabCompleter;
import io.github.a5h73y.vehiclez.commands.VehiclezCommands;
import io.github.a5h73y.vehiclez.commands.VehiclezConsoleCommands;
import io.github.a5h73y.vehiclez.configuration.ConfigManager;
import io.github.a5h73y.vehiclez.configuration.VehiclezConfiguration;
import io.github.a5h73y.vehiclez.configuration.impl.DefaultConfig;
import io.github.a5h73y.vehiclez.controllers.CarController;
import io.github.a5h73y.vehiclez.controllers.FuelController;
import io.github.a5h73y.vehiclez.enums.ConfigType;
import io.github.a5h73y.vehiclez.gui.VehiclezGuiManager;
import io.github.a5h73y.vehiclez.listeners.PlayerListener;
import io.github.a5h73y.vehiclez.listeners.SignListener;
import io.github.a5h73y.vehiclez.listeners.VehicleListener;
import io.github.a5h73y.vehiclez.other.VehiclezUpdater;
import io.github.a5h73y.vehiclez.persistence.CarDataHolder;
import io.github.a5h73y.vehiclez.persistence.CarDataMap;
import io.github.a5h73y.vehiclez.persistence.CarDataPersistence;
import io.github.a5h73y.vehiclez.plugin.BountifulApi;
import io.github.a5h73y.vehiclez.plugin.EconomyApi;
import io.github.a5h73y.vehiclez.plugin.PlaceholderApi;
import io.github.a5h73y.vehiclez.utility.PluginUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Vehiclez extends JavaPlugin {

    public static final String PLUGIN_NAME = "vehiclez";

    private static final int BSTATS_PLUGIN_ID = 2371;
    private static final int SPIGOT_PLUGIN_ID = 56255;
    private static Vehiclez instance;

    private VehiclezCommands vehiclezCommands;
    private FuelController fuelController;
    private CarController carController;
    private ConfigManager configManager;
    private VehiclezGuiManager guiManager;
    private CarDataPersistence carDataPersistence;

    private BountifulApi bountifulApi;
    private EconomyApi economyApi;
    private PlaceholderApi placeholderApi;

    /**
     * Get the plugin's instance.
     *
     * @return Vehiclez plugin instance.
     */
    public static Vehiclez getInstance() {
        return instance;
    }

    /**
     * Initialise the Vehiclez plugin.
     */
    @Override
    public void onEnable() {
        instance = this;

        if (PluginUtils.getMinorServerVersion() < 12) {
            PluginUtils.log("Unsupported server version, 1.12+ is supported.", 2);
            this.setEnabled(false);
            return;
        }

        registerManagers();
        registerCommands();
        registerEvents();

        getLogger().info("Enabled Vehiclez v" + getDescription().getVersion());
        new Metrics(this, BSTATS_PLUGIN_ID);
        checkForUpdates();
    }

    /**
     * Shutdown the plugin.
     */
    @Override
    public void onDisable() {
        PluginUtils.log("Disabled Vehiclez v" + getDescription().getVersion());
        instance = null;
    }

    /**
     * Get the Default config.
     * Overrides the default getConfig() method.
     *
     * @return default config
     */
    @Override
    public DefaultConfig getConfig() {
        return (DefaultConfig) this.configManager.get(ConfigType.DEFAULT);
    }

    /**
     * Get the matching {@link VehiclezConfiguration} for the given {@link ConfigType}.
     *
     * @param type {@link ConfigType}
     * @return matching {@link VehiclezConfiguration}
     */
    public static VehiclezConfiguration getConfig(ConfigType type) {
        return instance.configManager.get(type);
    }

    /**
     * Get the default config.yml file.
     *
     * @return {@link DefaultConfig}
     */
    public static DefaultConfig getDefaultConfig() {
        return instance.getConfig();
    }

    /**
     * The Vehiclez message prefix.
     *
     * @return vehiclez prefix from the config.
     */
    public static String getPrefix() {
        return TranslationUtils.getTranslation("Vehiclez.Prefix", false);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FuelController getFuelController() {
        return fuelController;
    }

    public CarController getCarController() {
        return carController;
    }

    public VehiclezGuiManager getGuiManager() {
        return guiManager;
    }

    public CarDataPersistence getCarDataPersistence() {
        return carDataPersistence;
    }

    public BountifulApi getBountifulApi() {
        return bountifulApi;
    }

    public EconomyApi getEconomyApi() {
        return economyApi;
    }

    public PlaceholderApi getPlaceholderApi() {
        return placeholderApi;
    }

    public VehiclezCommands getVehiclezCommands() {
        return vehiclezCommands;
    }

    private void registerManagers() {
        configManager = new ConfigManager(this.getDataFolder());
        carController = new CarController(this);
        fuelController = new FuelController(this);
        guiManager = new VehiclezGuiManager(this);

        if (PluginUtils.getMinorServerVersion() < 14) {
            carDataPersistence = new CarDataMap();
            PluginUtils.log("Depreciated server version, expect unintended behaviour.", 2);
        } else {
            carDataPersistence = new CarDataHolder();
        }

        setupPlugins();
    }

    private void setupPlugins() {
        bountifulApi = new BountifulApi();
        economyApi = new EconomyApi();
        placeholderApi = new PlaceholderApi();
    }

    private void registerCommands() {
        vehiclezCommands = new VehiclezCommands(this);
        PluginCommand parkourCommand = getCommand(PLUGIN_NAME);
        parkourCommand.setExecutor(vehiclezCommands);
        if (this.getConfig().getBoolean("Other.UseAutoTabCompletion")) {
            parkourCommand.setTabCompleter(new VehiclezAutoTabCompleter(this));
        }

        getCommand("vehiclezc").setExecutor(new VehiclezConsoleCommands(this));
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new VehicleListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new SignListener(this), this);
    }

    private void checkForUpdates() {
        if (getConfig().getBoolean("Other.UpdateCheck")) {
            new VehiclezUpdater(this, SPIGOT_PLUGIN_ID).checkForUpdateAsync();
        }
    }
}
