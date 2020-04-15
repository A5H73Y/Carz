package io.github.a5h73y.carz;

import io.github.a5h73y.carz.commands.CarzAutoTabCompleter;
import io.github.a5h73y.carz.commands.CarzCommands;
import io.github.a5h73y.carz.commands.CarzConsoleCommands;
import io.github.a5h73y.carz.configuration.Settings;
import io.github.a5h73y.carz.controllers.CarController;
import io.github.a5h73y.carz.controllers.FuelController;
import io.github.a5h73y.carz.listeners.PlayerListener;
import io.github.a5h73y.carz.listeners.SignListener;
import io.github.a5h73y.carz.listeners.VehicleListener;
import io.github.a5h73y.carz.other.CarzUpdater;
import io.github.a5h73y.carz.other.PluginUtils;
import io.github.a5h73y.carz.plugin.BountifulAPI;
import io.github.a5h73y.carz.plugin.EconomyAPI;
import io.github.a5h73y.carz.utility.ItemMetaUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;

public class Carz extends JavaPlugin {

    private static final int PLUGIN_ID = 42269;
    private static Carz instance;

    private BountifulAPI bountifulAPI;
    private EconomyAPI economyAPI;

    private FuelController fuelController;
    private CarController carController;
    private Settings settings;
    private ItemMetaUtils itemMetaUtils;

    public static Carz getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        settings = new Settings(this);
        carController = new CarController(this);
        fuelController = new FuelController(this);
        itemMetaUtils = new ItemMetaUtils();

        registerCommands();
        registerEvents();

        setupPlugins();

        getLogger().info("Enabled Carz v" + getDescription().getVersion());
        new MetricsLite(this, PLUGIN_ID);
        updatePlugin();
    }

    @Override
    public void onDisable() {
        PluginUtils.log("Disabled Carz v" + getDescription().getVersion());
        instance = null;
    }

    private void setupPlugins() {
        bountifulAPI = new BountifulAPI();
        economyAPI = new EconomyAPI();
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

    public static String getPrefix() {
        return TranslationUtils.getTranslation("Carz.Prefix", false);
    }

    private void updatePlugin() {
        if (getConfig().getBoolean("Other.UpdateCheck")) {
            new CarzUpdater(this, PLUGIN_ID, this.getFile(), CarzUpdater.UpdateType.DEFAULT, true);
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public FuelController getFuelController() {
        return fuelController;
    }

    public CarController getCarController() {
        return carController;
    }

    public BountifulAPI getBountifulAPI() {
        return bountifulAPI;
    }

    public EconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public ItemMetaUtils getItemMetaUtils() {
        return itemMetaUtils;
    }
}
