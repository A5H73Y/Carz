package io.github.a5h73y.carz.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.utility.PluginUtils;
import io.github.a5h73y.carz.utility.StringUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Carz config convenience accessors.
 */
public class Settings extends AbstractPluginReceiver {

    private File stringsFile;
    private FileConfiguration stringsConfig;

    private Set<Material> climbBlocks;
    private Map<String, Double> speedBlocks;

    /**
     * Carz configuration Settings.
     * config.yml and strings.yml will be generated with default values.
     *
     * @param carz plugin instance
     */
    public Settings(final Carz carz) {
        super(carz);

        setupConfig();
        setupStrings();
        reloadClimbBlocks();
        reloadSpeedBlocks();
    }

    /**
     * The strings.yml config.
     *
     * @return string.yml {@link FileConfiguration}
     */
    public FileConfiguration getStringsConfig() {
        return stringsConfig;
    }

    /**
     * Reload the Carz Configuration files.
     */
    public void reload() {
        carz.reloadConfig();
        stringsConfig = YamlConfiguration.loadConfiguration(stringsFile);
        reloadClimbBlocks();
        reloadSpeedBlocks();
    }

    /**
     * Initialise the config.yml on startup.
     * Values will be defaulted if not set.
     */
    private void setupConfig() {
        carz.getConfig().options().header("==== Carz Config ==== #");

        carz.getConfig().addDefault("Key.Material", "STICK");
        carz.getConfig().addDefault("Key.Glow", true);
        carz.getConfig().addDefault("Key.GiveOnCarEnter", true);
        carz.getConfig().addDefault("Key.RequireCarzKey", true);
        carz.getConfig().addDefault("Key.ManualLocking.Enabled", true);
        carz.getConfig().addDefault("Key.ManualLocking.ShiftAction", true);

        carz.getConfig().addDefault("Speed.Upgrade.Increment", 25.0);
        carz.getConfig().addDefault("Speed.Upgrade.Max", 200.0);

        carz.getConfig().addDefault("CommandEnabled.Spawn", true);
        carz.getConfig().addDefault("CommandEnabled.Purchase", true);
        carz.getConfig().addDefault("CommandEnabled.Refuel", true);
        carz.getConfig().addDefault("CommandEnabled.Upgrade", true);

        carz.getConfig().addDefault("Fuel.Enabled", true);
        carz.getConfig().addDefault("Fuel.ScaleCost", true);
        carz.getConfig().addDefault("Fuel.MaxCapacity", 3000.0);
        carz.getConfig().addDefault("Fuel.GaugeScale", 40);

        carz.getConfig().addDefault("ClimbBlocks.AllBlocks", true);
        carz.getConfig().addDefault("ClimbBlocks.Materials", new String[]{"GOLD_BLOCK"});
        carz.getConfig().addDefault("ClimbBlocks.Strength", 0.05D);

        carz.getConfig().addDefault("Vault.Enabled", true);
        carz.getConfig().addDefault("Vault.ConfirmPurchases", true);
        carz.getConfig().addDefault("Vault.Cost.Upgrade", 8.0);
        carz.getConfig().addDefault("Vault.Cost.Refuel", 2.0);

        carz.getConfig().addDefault("BountifulAPI.Enabled", true);

        carz.getConfig().addDefault("PlaceholderAPI.Enabled", true);

        carz.getConfig().addDefault("Other.ControlCarsWhileFalling", true);
        carz.getConfig().addDefault("Other.DamageEntities.Enabled", true);
        carz.getConfig().addDefault("Other.DamageEntities.Damage", 5.0);
        carz.getConfig().addDefault("Other.DestroyInLiquid", true);
        carz.getConfig().addDefault("Other.AutomaticCarLock", true);
        carz.getConfig().addDefault("Other.OnlyOwnedCarsDrive", false);
        carz.getConfig().addDefault("Other.SignProtection", true);
        carz.getConfig().addDefault("Other.UpdateCheck", true);
        carz.getConfig().addDefault("Other.UseAutoTabCompletion", true);
        carz.getConfig().addDefault("Other.UseEffects", true);
        carz.getConfig().addDefault("Other.UsePermissions", true);

        carz.getConfig().addDefault("CarTypes.default.StartMaxSpeed", 60.0);
        carz.getConfig().addDefault("CarTypes.default.MaxUpgradeSpeed", 200.0);
        carz.getConfig().addDefault("CarTypes.default.Acceleration", 5.0);
        carz.getConfig().addDefault("CarTypes.default.FuelUsage", 1.0);
        carz.getConfig().addDefault("CarTypes.default.FillMaterial", "AIR");
        carz.getConfig().addDefault("CarTypes.default.Cost", 10.0);

        carz.getConfig().options().copyDefaults(true);
        carz.saveConfig();
    }

    /**
     * Initialise the strings.yml on startup.
     * Values will be defaulted if not set.
     */
    private void setupStrings() {
        if (!setupStringsConfig()) {
            return;
        }

        stringsConfig.addDefault("Carz.Prefix", "&0[&bCarz&0]&7 ");
        stringsConfig.addDefault("Carz.SignHeader", "&0[&bCarz&0]");
        stringsConfig.addDefault("Carz.Commands", "To display all commands enter &f/carz cmds");
        stringsConfig.addDefault("Carz.ConsoleCommands", "To display all commands enter &f/carzc cmds");
        stringsConfig.addDefault("Carz.ConfigReloaded", "The config has been reloaded.");
        stringsConfig.addDefault("Carz.SignRemoved", "Carz sign removed!");
        stringsConfig.addDefault("Carz.CarsDestroyed", "All cars destroyed!");
        stringsConfig.addDefault("Carz.SignCreated", "%TYPE% sign created.");
        stringsConfig.addDefault("Carz.Heading", "-- &9&l%TEXT% &r--");

        stringsConfig.addDefault("Car.Spawned", "Car Spawned!");
        stringsConfig.addDefault("Car.EngineStart", "You switch the engine on.");
        stringsConfig.addDefault("Car.EngineStop", "You switch the engine off.");
        stringsConfig.addDefault("Car.CarLocked", "You lock the car.");
        stringsConfig.addDefault("Car.CarUnlocked", "You unlock the car.");
        stringsConfig.addDefault("Car.PlayerCar", "&b%PLAYER%&f's car");
        stringsConfig.addDefault("Car.FuelEmpty", "This car has run out of fuel!");
        stringsConfig.addDefault("Car.LiquidDamage", "Your car has been destroyed by liquid!");
        stringsConfig.addDefault("Car.UpgradeSpeed", "New top speed: %SPEED%");
        stringsConfig.addDefault("Car.Key.Display", "&b%PLAYER%&f's key");
        stringsConfig.addDefault("Car.Key.Received", "You receive a key.");
        stringsConfig.addDefault("Car.Claimed", "You are now the owner of this car.");

        stringsConfig.addDefault("Purchase.Confirm.Purchase", "&7Enter &a/carz confirm &7to confirm, or &c/carz cancel &7to cancel the purchase.");
        stringsConfig.addDefault("Purchase.Confirm.Car", "You are about to purchase a &b%TYPE% &7car, costing &b%COST%%CURRENCY%&7.");
        stringsConfig.addDefault("Purchase.Confirm.Upgrade", "You are about to upgrade your car from &b%FROM% &7to &b%TO%&7, costing &b%COST%%CURRENCY%&7.");
        stringsConfig.addDefault("Purchase.Confirm.Refuel", "You are about to refuel &b%PERCENT% &7of your car's fuel, costing &b%COST%%CURRENCY%&7.");
        stringsConfig.addDefault("Purchase.Success.Car", "&f%TYPE% &7car Purchased!");
        stringsConfig.addDefault("Purchase.Success.Upgrade", "Car Upgraded!");
        stringsConfig.addDefault("Purchase.Success.Refuel", "Car Refuelled!");
        stringsConfig.addDefault("Purchase.Cancelled", "Purchase cancelled.");

        stringsConfig.addDefault("CarType.Create.Name", "&d What would you like this car to be called?");
        stringsConfig.addDefault("CarType.Create.StartMaxSpeed", "&d What should the Car's Start Speed be?\n&a (default = 60.0)");
        stringsConfig.addDefault("CarType.Create.MaxUpgradeSpeed", "&d What should the Car's Max Upgrade Speed be?\n&a (default = 120.0)");
        stringsConfig.addDefault("CarType.Create.Acceleration", "&d What should the Car's Acceleration be?\n&a (default = 1.0)");
        stringsConfig.addDefault("CarType.Create.FuelUsage", "&d What should the Fuel Usage be?\n&a (default = 1.0)");
        stringsConfig.addDefault("CarType.Create.FillMaterial", "&d What should the Fill Material be?\n&a (default = AIR)");
        stringsConfig.addDefault("CarType.Create.Cost", "&d How much should the car cost?\n&a (default = 10.0)");
        stringsConfig.addDefault("CarType.Create.Success", "&d All done, &a%VALUE% &dcreated.");
        stringsConfig.addDefault("CarType.Error.InvalidName", "Invalid Car Type name.");
        stringsConfig.addDefault("CarType.Error.InvalidValue", "Invalid Value.");
        stringsConfig.addDefault("CarType.Error.AlreadyExists", "This Car Type already exists.");

        stringsConfig.addDefault("Error.NoPermission", "You do not have permission: &b%PERMISSION%");
        stringsConfig.addDefault("Error.SignProtected", "This sign is protected!");
        stringsConfig.addDefault("Error.UnknownCommand", "Unknown Command!");
        stringsConfig.addDefault("Error.UnknownSignCommand", "Unknown Sign Command!");
        stringsConfig.addDefault("Error.CommandDisabled", "This command has been disabled!");
        stringsConfig.addDefault("Error.InCar", "You are already in a car!");
        stringsConfig.addDefault("Error.NotInCar", "You are not in a car!");
        stringsConfig.addDefault("Error.HaveCar", "You already have a car!");
        stringsConfig.addDefault("Error.FuelDisabled", "Fuel is disabled.");
        stringsConfig.addDefault("Error.PurchaseFailed", "Purchase failed. Cost: %COST%");
        stringsConfig.addDefault("Error.FullyUpgraded", "Your car is already fully upgraded!");
        stringsConfig.addDefault("Error.Owned", "This car is owned by %PLAYER%!");
        stringsConfig.addDefault("Error.UnknownCarType", "Unknown car type.");
        stringsConfig.addDefault("Error.UnknownPlayer", "Unknown player.");
        stringsConfig.addDefault("Error.UnknownMaterial", "Unknown Material: ");
        stringsConfig.addDefault("Error.SpecifyPlayer", "Please specify a player.");
        stringsConfig.addDefault("Error.PurchaseOutstanding", "You have an outstanding purchase.");
        stringsConfig.addDefault("Error.NoPurchaseOutstanding", "You don't have an outstanding purchase.");
        stringsConfig.addDefault("Error.CarNotDriven", "This can hasn't been driven yet.");

        stringsConfig.options().copyDefaults(true);
        try {
            stringsConfig.save(stringsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the Material to the list of Climb Blocks.
     *
     * @param material {@link Material}
     */
    public void addClimbBlock(Material material) {
        if (material == null) {
            return;
        }

        List<String> materials = getRawClimbBlocks();
        materials.add(material.name());
        carz.getConfig().set("ClimbBlocks.Materials", materials);
        carz.saveConfig();
        reloadClimbBlocks();
    }

    /**
     * Remove the Material from the list of Climb Blocks.
     *
     * @param material {@link Material}
     */
    public void removeClimbBlock(Material material) {
        if (material == null) {
            return;
        }

        List<String> materials = getRawClimbBlocks();
        materials.remove(material.name());
        carz.getConfig().set("ClimbBlocks.Materials", materials);
        carz.saveConfig();
        reloadClimbBlocks();
    }

    /**
     * Add the Material to the list of Speed Blocks.
     *
     * @param material {@link Material}
     * @param speed speed
     */
    public void addSpeedBlock(Material material, double speed) {
        if (material == null) {
            return;
        }

        carz.getConfig().set("SpeedBlocks." + material.name(), speed);
        carz.saveConfig();
        reloadSpeedBlocks();
    }

    /**
     * Remove the Material from the list of Speed Blocks.
     *
     * @param material {@link Material}
     */
    public void removeSpeedBlock(Material material) {
        if (material == null) {
            return;
        }

        carz.getConfig().set("SpeedBlocks." + material.name(), null);
        carz.saveConfig();
        reloadSpeedBlocks();
    }

    public Set<Material> getClimbBlocks() {
        return this.climbBlocks;
    }

    public boolean containsSpeedBlock(Material material) {
        return this.speedBlocks.containsKey(material.name());
    }

    public Set<String> getSpeedBlocks() {
        return this.speedBlocks.keySet();
    }

    public Double getSpeedModifier(Material material) {
        return this.speedBlocks.get(material.name());
    }

    public List<String> getRawClimbBlocks() {
        return carz.getConfig().getStringList("ClimbBlocks.Materials");
    }

    public Material getKey() {
        return Material.getMaterial(carz.getConfig().getString("Key.Material"));
    }

    public String getSignHeader() {
        return TranslationUtils.getTranslation("Carz.SignHeader", false);
    }

    public String getStrippedSignHeader() {
        return ChatColor.stripColor(StringUtils.colour(getSignHeader()));
    }

    public boolean isDestroyInLiquid() {
        return carz.getConfig().getBoolean("Other.DestroyInLiquid");
    }

    public boolean isOnlyOwnedCarsDrive() {
        return carz.getConfig().getBoolean("Other.OnlyOwnedCarsDrive");
    }

    public boolean isControlCarsWhileFalling() {
        return carz.getConfig().getBoolean("Other.ControlCarsWhileFalling");
    }

    public boolean isFuelScaleCost() {
        return carz.getConfig().getBoolean("Fuel.ScaleCost");
    }

    public boolean isAutomaticCarLock() {
        return carz.getConfig().getBoolean("Other.AutomaticCarLock");
    }

    public double getUpgradeIncrement() {
        return carz.getConfig().getDouble("Speed.Upgrade.Increment");
    }

    public double getUpgradeMaxSpeed() {
        return carz.getConfig().getDouble("Speed.Upgrade.Max");
    }

    public double getClimbBlockStrength() {
        return carz.getConfig().getDouble("ClimbBlocks.Strength");
    }

    /**
     * Setup the strings.yml config.
     * The file will be created if it doesn't exist.
     *
     * @return file setup was successful
     */
    private boolean setupStringsConfig() {
        stringsFile = new File(carz.getDataFolder(), "strings.yml");

        if (!stringsFile.exists()) {
            try {
                if (stringsFile.createNewFile()) {
                    carz.getLogger().info("Created strings.yml");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        stringsConfig = YamlConfiguration.loadConfiguration(stringsFile);
        return true;
    }

    private void reloadClimbBlocks() {
        this.climbBlocks = PluginUtils.convertToValidMaterials(getRawClimbBlocks());
    }

    private void reloadSpeedBlocks() {
        this.speedBlocks = new HashMap<>();
        ConfigurationSection section = carz.getConfig().getConfigurationSection("SpeedBlocks");

        if (section != null) {
            Set<Material> test = PluginUtils.convertToValidMaterials(section.getKeys(false));

            for (Material s : test) {
                this.speedBlocks.put(s.name(), carz.getConfig().getDouble("SpeedBlocks." + s.name()));
            }
        }
    }
}
