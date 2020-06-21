package io.github.a5h73y.carz;

import io.github.a5h73y.carz.commands.CarzAutoTabCompleter;
import io.github.a5h73y.carz.commands.CarzCommands;
import io.github.a5h73y.carz.commands.CarzConsoleCommands;
import io.github.a5h73y.carz.configuration.CarzConfiguration;
import io.github.a5h73y.carz.configuration.ConfigManager;
import io.github.a5h73y.carz.configuration.impl.DefaultConfig;
import io.github.a5h73y.carz.controllers.CarController;
import io.github.a5h73y.carz.controllers.FuelController;
import io.github.a5h73y.carz.enums.ConfigType;
import io.github.a5h73y.carz.gui.CarzGuiManager;
import io.github.a5h73y.carz.listeners.PlayerListener;
import io.github.a5h73y.carz.listeners.SignListener;
import io.github.a5h73y.carz.listeners.VehicleListener;
import io.github.a5h73y.carz.other.CarzUpdater;
import io.github.a5h73y.carz.persistence.CarDataHolder;
import io.github.a5h73y.carz.persistence.CarDataMap;
import io.github.a5h73y.carz.persistence.CarDataPersistence;
import io.github.a5h73y.carz.plugin.BountifulApi;
import io.github.a5h73y.carz.plugin.EconomyApi;
import io.github.a5h73y.carz.plugin.PlaceholderApi;
import io.github.a5h73y.carz.utility.PluginUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;

public class Carz extends JavaPlugin {

    private static final int BUKKIT_PLUGIN_ID = 42269;
    private static final int SPIGOT_PLUGIN_ID = 56255;
    private static Carz instance;

    private BountifulApi bountifulApi;
    private EconomyApi economyApi;
    private PlaceholderApi placeholderApi;

    private FuelController fuelController;
    private CarController carController;
    private ConfigManager configManager;
    private CarzGuiManager guiManager;
    private CarDataPersistence carDataPersistence;

    /**
     * Get the plugin's instance.
     *
     * @return Carz plugin instance.
     */
    public static Carz getInstance() {
        return instance;
    }

    /**
     * Initialise the Carz plugin.
     */
    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this.getDataFolder());
        carController = new CarController(this);
        fuelController = new FuelController(this);
        guiManager = new CarzGuiManager(this);

        if (PluginUtils.getMinorServerVersion() < 14) {
            PluginUtils.log("Unsupported server version, expect unintended behaviour.", 2);
            carDataPersistence = new CarDataMap();
        } else {
            carDataPersistence = new CarDataHolder();
        }

        registerCommands();
        registerEvents();

        setupPlugins();

        getLogger().info("Enabled Carz v" + getDescription().getVersion());
        new MetricsLite(this, BUKKIT_PLUGIN_ID);
        checkForUpdates();
    }

    /**
     * Shutdown the plugin.
     */
    @Override
    public void onDisable() {
        PluginUtils.log("Disabled Carz v" + getDescription().getVersion());
        instance = null;
    }

    @Override
    public DefaultConfig getConfig() {
        return (DefaultConfig) this.configManager.get(ConfigType.DEFAULT);
    }

    /**
     * Get the matching {@link CarzConfiguration} for the given {@link ConfigType}.
     *
     * @param type {@link ConfigType}
     * @return matching {@link CarzConfiguration}
     */
    public static CarzConfiguration getConfig(ConfigType type) {
        return instance.configManager.get(type);
    }

    /**
     * The Carz message prefix.
     * @return carz prefix from the config.
     */
    public static String getPrefix() {
        return TranslationUtils.getTranslation("Carz.Prefix", false);
    }

    /**
     * Get the default config.yml file.
     *
     * @return {@link DefaultConfig}
     */
    public static DefaultConfig getDefaultConfig() {
        return (DefaultConfig) instance.configManager.get(ConfigType.DEFAULT);
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

    public CarzGuiManager getGuiManager() {
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

    private void setupPlugins() {
        bountifulApi = new BountifulApi();
        economyApi = new EconomyApi();
        placeholderApi = new PlaceholderApi();
    }

    private void registerCommands() {
        getCommand("carz").setExecutor(new CarzCommands(this));
        getCommand("carzc").setExecutor(new CarzConsoleCommands(this));
        if (getConfig().getBoolean("Other.UseAutoTabCompletion")) {
            getCommand("carz").setTabCompleter(new CarzAutoTabCompleter(this));
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new VehicleListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new SignListener(this), this);
    }

    private void checkForUpdates() {
        if (getConfig().getBoolean("Other.UpdateCheck")) {
            new CarzUpdater(this, SPIGOT_PLUGIN_ID).checkForUpdateAsync();
        }
    }
}
