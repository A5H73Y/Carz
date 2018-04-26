package me.A5H73Y.Carz.other;

import me.A5H73Y.Carz.Carz;
import org.bukkit.Material;

public class Settings {

    private Carz carz;

    public Settings(Carz carz) {
        this.carz = carz;
        
        setupConfig();
    }

    public Material getKey() {
        return Material.getMaterial(carz.getConfig().getString("Key.Material"));
    }

    public boolean isDestroyInLiquid() {
        return carz.getConfig().getBoolean("Other.DestroyInLiquid");
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

    /**
     * Initialise the configuration options on startup
     */
    private void setupConfig() {
        carz.getConfig().options().header("==== Carz Config ==== #");

        carz.getConfig().addDefault("Key.Material", "STICK");
        carz.getConfig().addDefault("Key.GiveOnCarEnter", true);
        carz.getConfig().addDefault("Key.RequireCarzKey", true);

        carz.getConfig().addDefault("Speed.Start", 50.0);
        carz.getConfig().addDefault("Speed.Upgrade.Enable", true); //TODO update in code
        carz.getConfig().addDefault("Speed.Upgrade.Increment", 25.0);
        carz.getConfig().addDefault("Speed.Upgrade.Max", 200.0);

        carz.getConfig().addDefault("Command.Spawn", true);
        carz.getConfig().addDefault("Command.Purchase", true);
        carz.getConfig().addDefault("Command.Refuel", true);
        carz.getConfig().addDefault("Command.Upgrade", true);

        carz.getConfig().addDefault("Fuel.Enable", true);
        carz.getConfig().addDefault("Fuel.StartAmount", 1000.0);

        carz.getConfig().addDefault("Economy.Use", true);
        carz.getConfig().addDefault("Economy.Cost.Purchase", 10);
        carz.getConfig().addDefault("Economy.Cost.Upgrade", 8);
        carz.getConfig().addDefault("Economy.Cost.Refuel", 2);

        carz.getConfig().addDefault("Other.DestroyInLiquid", true);
        carz.getConfig().addDefault("Other.UpdateCheck", true);
        carz.getConfig().addDefault("Other.GlowingCarz", true);
        carz.getConfig().addDefault("Other.UsePermissions", true);

        carz.getConfig().addDefault("Message.Prefix", "&0[&bCarz&0]&7 ");
        carz.getConfig().addDefault("Message.Spawned", "Car Spawned!");
        carz.getConfig().addDefault("Message.Purchased", "Car purchased!");
        carz.getConfig().addDefault("Message.Refuelled", "Car Refuelled!");
        carz.getConfig().addDefault("Message.EngineStart", "You switch the engine on.");
        carz.getConfig().addDefault("Message.EngineStop", "You switch the engine off.");
        carz.getConfig().addDefault("Message.CarLocked", "You lock the car.");
        carz.getConfig().addDefault("Message.CarUnlocked", "You unlock the car");
        carz.getConfig().addDefault("Message.Commands", "To Display all commands enter /carz cmds");
        carz.getConfig().addDefault("Message.PlayerCar", "%PLAYER%'s car");
        carz.getConfig().addDefault("Message.FuelEmpty", "This car has run out of fuel!");
        carz.getConfig().addDefault("Message.KeyReceived", "You receive a key");
        carz.getConfig().addDefault("Message.LiquidDamage", "Your car has been destroyed by liquid!");
        carz.getConfig().addDefault("Message.UpgradeSpeed", "New top speed: %SPEED%");

        carz.getConfig().addDefault("Message.Error.NoPermission", "You do not have permission: &b%PERMISSION%");
        carz.getConfig().addDefault("Message.Error.SignProtected", "This sign is protected!");
        carz.getConfig().addDefault("Message.Error.UnknownCommand", "Unknown Command!");
        carz.getConfig().addDefault("Message.Error.UnknownSignCommand", "Unknown Sign Command!");
        carz.getConfig().addDefault("Message.Error.CommandDisabled", "This command has been disabled!");
        carz.getConfig().addDefault("Message.Error.InCar", "You are already in a car!");
        carz.getConfig().addDefault("Message.Error.NotInCar", "You are not in a car!");
        carz.getConfig().addDefault("Message.Error.HaveCar", "You already have a car!");
        carz.getConfig().addDefault("Message.Error.FuelDisabled", "Fuel is disabled");
        carz.getConfig().addDefault("Message.Error.PurchaseFailed", "Purchase failed. Cost: %COST%");
        carz.getConfig().addDefault("Message.Error.FullyUpgraded", "Your car is already fully upgraded!");
        carz.getConfig().addDefault("Message.Error.Owned", "This car is owned by someone else!");

        carz.getConfig().options().copyDefaults(true);
        carz.saveConfig();
    }
}
