package me.A5H73Y.Carz;

import me.A5H73Y.Carz.commands.CarzCommands;
import me.A5H73Y.Carz.controllers.CarController;
import me.A5H73Y.Carz.controllers.EconomyController;
import me.A5H73Y.Carz.controllers.FuelController;
import me.A5H73Y.Carz.listeners.PlayerListener;
import me.A5H73Y.Carz.listeners.SignListener;
import me.A5H73Y.Carz.listeners.VehicleListener;
import me.A5H73Y.Carz.other.Settings;
import me.A5H73Y.Carz.other.Updater;
import me.A5H73Y.Carz.other.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Carz extends JavaPlugin {

    private static Carz instance;
    private Settings settings;

    private FuelController fuelController;
    private CarController carController;
    private EconomyController economyController;

    public static Carz getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        getCommand("carz").setExecutor(new CarzCommands(this));

        getServer().getPluginManager().registerEvents(new VehicleListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new SignListener(this), this);

        settings = new Settings(this);
        carController = new CarController(this);
        fuelController = new FuelController();
        economyController = new EconomyController(this);

        setupBountifulApi();

        getLogger().info("Enabled Carz v" + getDescription().getVersion());
        new Metrics(this);
        updatePlugin();
    }

    public FuelController getFuelController() {
        return fuelController;
    }

    public CarController getCarController() {
        return carController;
    }

    public EconomyController getEconomyController() {
        return economyController;
    }

    public Settings getSettings() {
        return settings;
    }

    public static String getPrefix() {
        return Utils.getTranslation("Prefix", false);
    }

    private void updatePlugin() {
        if (instance.getConfig().getBoolean("Other.UpdateCheck")) {
            new Updater(this, 42269, this.getFile(), Updater.UpdateType.DEFAULT, true);
        }
    }

    private void setupBountifulApi() {
        if (!getConfig().getBoolean("Other.BountifulAPI.Enabled")) {
            return;
        }

        Plugin bountifulApi = getServer().getPluginManager().getPlugin("BountifulAPI");

        if (bountifulApi != null && bountifulApi.isEnabled()) {
            Utils.log("[BountifulAPI] Successfully linked. Version: "
                    + bountifulApi.getDescription().getVersion(), 0);
            settings.setUsingBountiful(true);
        } else {
            Utils.log("[BountifulAPI] Plugin is missing, disabling config option.", 1);
            getConfig().set("Other.BountifulAPI.Enabled", false);
            saveConfig();
        }
    }
}
