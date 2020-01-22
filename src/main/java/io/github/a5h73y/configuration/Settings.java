package io.github.a5h73y.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import io.github.a5h73y.Carz;
import io.github.a5h73y.other.Utils;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Carz config convenience accessors.
 */
public class Settings {

    private Carz carz;

    private File stringsFile;
    private FileConfiguration stringsConfig;

    private Set<Material> climbBlocks;

    public Settings(Carz carz) {
        this.carz = carz;

        setupConfig();
        setupStrings();
        reloadClimbBlocks();
    }

    public FileConfiguration getStringsConfig() {
        return stringsConfig;
    }

    public void reload() {
        carz.reloadConfig();
        stringsConfig = YamlConfiguration.loadConfiguration(stringsFile);
        reloadClimbBlocks();
    }

    /**
     * Initialise the configuration options on startup
     */
    private void setupConfig() {
        carz.getConfig().options().header("==== Carz Config ==== #");

        carz.getConfig().addDefault("Key.Material", "STICK");
        carz.getConfig().addDefault("Key.GiveOnCarEnter", true);
        carz.getConfig().addDefault("Key.RequireCarzKey", true);

        carz.getConfig().addDefault("Speed.Start", 50.0);
        carz.getConfig().addDefault("Speed.Upgrade.Increment", 25.0);
        carz.getConfig().addDefault("Speed.Upgrade.Max", 200.0);

        carz.getConfig().addDefault("Command.Spawn", true);
        carz.getConfig().addDefault("Command.Purchase", true);
        carz.getConfig().addDefault("Command.Refuel", true);
        carz.getConfig().addDefault("Command.Upgrade", true);

        carz.getConfig().addDefault("Fuel.Enable", true);
        carz.getConfig().addDefault("Fuel.ScaleCost", true);
        carz.getConfig().addDefault("Fuel.StartAmount", 3000.0);
        carz.getConfig().addDefault("Fuel.GaugeScale", 40);

        carz.getConfig().addDefault("ClimbBlocks.Materials", new String[]{"GOLD_BLOCK"});
        carz.getConfig().addDefault("ClimbBlocks.Strength", 0.05D);

        carz.getConfig().addDefault("Other.OnlyOwnedCarsDrive" , false);
        carz.getConfig().addDefault("Other.ControlCarsWhileFalling", true);
        carz.getConfig().addDefault("Other.DestroyInLiquid", true);
        carz.getConfig().addDefault("Other.UsePermissions", true);
        carz.getConfig().addDefault("Other.UseEffects", true);
        carz.getConfig().addDefault("Other.UseAutoTabCompletion", true);
        carz.getConfig().addDefault("Other.UpdateCheck", true);

        carz.getConfig().addDefault("Other.BountifulAPI.Enabled", true);
        carz.getConfig().addDefault("Other.Vault.Enabled", true);
        carz.getConfig().addDefault("Other.Vault.Cost.Purchase", 10.0);
        carz.getConfig().addDefault("Other.Vault.Cost.Upgrade", 8.0);
        carz.getConfig().addDefault("Other.Vault.Cost.Refuel", 2.0);

        carz.getConfig().addDefault("CarTypes.Default.StartMaxSpeed", 1.0);
        carz.getConfig().addDefault("CarTypes.Default.Acceleration", 1.0);
        carz.getConfig().addDefault("CarTypes.Default.FuelUsage", 1.0);
        carz.getConfig().addDefault("CarTypes.Default.FillMaterial", "AIR");

        carz.getConfig().options().copyDefaults(true);
        carz.saveConfig();
    }

    private void setupStrings() {
        if (!createStringsConfig()) {
            return;
        }

        stringsConfig.addDefault("Carz.Prefix", "&0[&bCarz&0]&7 ");
        stringsConfig.addDefault("Carz.SignHeader", "&0[&bCarz&0]");
        stringsConfig.addDefault("Carz.Commands", "To display all commands enter &f/Carz cmds");
        stringsConfig.addDefault("Carz.ConsoleCommands", "To display all commands enter &f/Carzc cmds");
        stringsConfig.addDefault("Carz.ConfigReloaded", "The config has been reloaded.");

        stringsConfig.addDefault("Car.Spawned", "Car Spawned!");
        stringsConfig.addDefault("Car.Purchased", "Car purchased!");
        stringsConfig.addDefault("Car.Refuel", "Car Refuelled!");
        stringsConfig.addDefault("Car.EngineStart", "You switch the engine on.");
        stringsConfig.addDefault("Car.EngineStop", "You switch the engine off.");
        stringsConfig.addDefault("Car.CarLocked", "You lock the car.");
        stringsConfig.addDefault("Car.CarUnlocked", "You unlock the car.");
        stringsConfig.addDefault("Car.PlayerCar", "%PLAYER%'s car");
        stringsConfig.addDefault("Car.FuelEmpty", "This car has run out of fuel!");
        stringsConfig.addDefault("Car.KeyReceived", "You receive a key.");
        stringsConfig.addDefault("Car.LiquidDamage", "Your car has been destroyed by liquid!");
        stringsConfig.addDefault("Car.UpgradeSpeed", "New top speed: %SPEED%");

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

        stringsConfig.options().copyDefaults(true);
        try {
            stringsConfig.save(stringsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void reloadClimbBlocks() {
        this.climbBlocks = Utils.convertToValidMaterials(getRawClimbBlocks());
    }

    public Set<Material> getClimbBlocks() {
        return this.climbBlocks;
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

    public double getStartSpeed() {
        return carz.getConfig().getDouble("Speed.Start");
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

    private boolean createStringsConfig() {
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
}
