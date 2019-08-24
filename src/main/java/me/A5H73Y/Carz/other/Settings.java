package me.A5H73Y.Carz.other;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import me.A5H73Y.Carz.Carz;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {

    private Carz carz;

    private File stringsFile;
    private FileConfiguration stringsConfig;

    private Set<Material> climbBlocks;
    private boolean bountiful = false;

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
        Carz.getInstance().reloadConfig();
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
        carz.getConfig().addDefault("Fuel.StartAmount", 3000.0);
        carz.getConfig().addDefault("Fuel.GaugeScale", 40);

        carz.getConfig().addDefault("Economy.Use", true);
        carz.getConfig().addDefault("Economy.Cost.Purchase", 10.0);
        carz.getConfig().addDefault("Economy.Cost.Upgrade", 8.0);
        carz.getConfig().addDefault("Economy.Cost.Refuel", 2.0);

        carz.getConfig().addDefault("ClimbBlocks.Materials", new String[]{"GOLD_BLOCK"});
        carz.getConfig().addDefault("ClimbBlocks.Strength", 0.05D);

        carz.getConfig().addDefault("Other.OnlyOwnedCarsDrive" , false);
        carz.getConfig().addDefault("Other.ControlCarsWhileFalling", true);
        carz.getConfig().addDefault("Other.DestroyInLiquid", true);
        carz.getConfig().addDefault("Other.UsePermissions", true);
        carz.getConfig().addDefault("Other.UseEffects", true);
        carz.getConfig().addDefault("Other.UpdateCheck", true);

        carz.getConfig().addDefault("Other.BountifulAPI.Enabled", true);

        carz.getConfig().options().copyDefaults(true);
        carz.saveConfig();
    }

    private void setupStrings() {
        if (!createStringsConfig()) {
            return;
        }

        stringsConfig.addDefault("Carz.Prefix", "&0[&bCarz&0]&7 ");
        stringsConfig.addDefault("Carz.SignHeader", "&0[&bCarz&0]");
        stringsConfig.addDefault("Carz.Commands", "To Display all commands enter &f/Carz cmds");
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
        return Utils.getTranslation("SignHeader", false);
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

    public Double getStartSpeed() {
        return carz.getConfig().getDouble("Speed.Start");
    }

    public Double getUpgradeSpeed() {
        return carz.getConfig().getDouble("Speed.Upgrade.Increment");
    }

    public Double getUpgradeMaxSpeed() {
        return carz.getConfig().getDouble("Speed.Upgrade.Max");
    }

    public Double getClimbBlockStrength() {
        return carz.getConfig().getDouble("ClimbBlocks.Strength");
    }

    public boolean isUsingBountiful() {
        return bountiful;
    }

    public void setUsingBountiful(boolean bountiful) {
        this.bountiful = bountiful;
    }

    private boolean createStringsConfig() {
        stringsFile = new File(Carz.getInstance().getDataFolder(), "strings.yml");

        if (!stringsFile.exists()) {
            try {
                if (stringsFile.createNewFile()) {
                    Carz.getInstance().getLogger().info("Created strings.yml");
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
