package io.github.a5h73y.vehiclez.configuration.impl;

import io.github.a5h73y.vehiclez.configuration.VehiclezConfiguration;

public class StringsConfig extends VehiclezConfiguration {

	@Override
	protected String getFileName() {
		return "strings.yml";
	}

	/**
	 * Initialise the strings.yml on startup.
	 * Values will be defaulted if not set.
	 */
	@Override
	protected void initializeConfig() {
		this.addDefault("Vehiclez.Prefix", "&0[&bVehiclez&0]&7 ");
		this.addDefault("Vehiclez.SignHeader", "&0[&bVehiclez&0]");
		this.addDefault("Vehiclez.ConsoleCommands", "To display all commands enter &f/vehiclezc cmds");
		this.addDefault("Vehiclez.ConfigReloaded", "The config has been reloaded.");
		this.addDefault("Vehiclez.SignRemoved", "Vehiclez sign removed!");
		this.addDefault("Vehiclez.CarsDestroyed", "All cars destroyed!");
		this.addDefault("Vehiclez.SignCreated", "&b%VALUE% &7sign created.");
		this.addDefault("Vehiclez.Heading", "-- &9&l%VALUE% &r--");

		this.addDefault("Help.Command", "&7/vehiclez help &9%VALUE% &0: &7To learn more about this command.");
		this.addDefault("Help.Commands", "To display all commands enter &f/vehiclez cmds");
		this.addDefault("Help.CommandSyntax", "&7Syntax: &f/vehiclez %VALUE%");
		this.addDefault("Help.CommandExample", "&7Example: &f%VALUE%");

		this.addDefault("Car.Spawned", "Car Spawned.");
		this.addDefault("Car.EngineStart", "You switch the engine on.");
		this.addDefault("Car.EngineStop", "You switch the engine off.");
		this.addDefault("Car.CarLocked", "You lock the car.");
		this.addDefault("Car.CarUnlocked", "You unlock the car.");
		this.addDefault("Car.PlayerCar", "&b%VALUE%&f's car");
		this.addDefault("Car.FuelEmpty", "Your car has run out of fuel!");
		this.addDefault("Car.LiquidDamage", "Your car has been destroyed by liquid!");
		this.addDefault("Car.UpgradeSpeed", "New top speed: &b%VALUE%");
		this.addDefault("Car.Key.Display", "&b%VALUE%&f's key");
		this.addDefault("Car.Key.Received", "You receive a key.");
		this.addDefault("Car.Claimed", "You are now the owner of this car.");
		this.addDefault("Car.OwnershipRemoved", "You have removed ownership of this car.");
		this.addDefault("Car.Given", "You have given &b%VALUE% &7your car.");
		this.addDefault("Car.Received", "You have received a car from &b%VALUE%&7.");

		this.addDefault("Purchase.Confirm.Purchase", "&7Enter &a/vehiclez confirm &7to confirm, or &c/vehiclez cancel &7to cancel the purchase.");
		this.addDefault("Purchase.Confirm.Car", "You are about to purchase a &b%TYPE% &7car, costing &b%COST%%CURRENCY%&7.");
		this.addDefault("Purchase.Confirm.Upgrade", "You are about to upgrade your car from &b%FROM% &7to &b%TO%&7, costing &b%COST%%CURRENCY%&7.");
		this.addDefault("Purchase.Confirm.Refuel", "You are about to refuel &b%PERCENT% &7of your car's fuel, costing &b%COST%%CURRENCY%&7.");
		this.addDefault("Purchase.Success.Car", "&f%VALUE% &7car Purchased!");
		this.addDefault("Purchase.Success.Upgrade", "Car Upgraded!");
		this.addDefault("Purchase.Success.Refuel", "Car Refuelled!");
		this.addDefault("Purchase.Cancelled", "Purchase cancelled.");

		this.addDefault("CarType.Heading", "Car Types");
		this.addDefault("CarType.Create.Name", "&d What would you like this car to be called?");
		this.addDefault("CarType.Create.StartMaxSpeed", "&d What should the Car's Start Speed be?\n&a (default = 60.0)");
		this.addDefault("CarType.Create.MaxUpgradeSpeed", "&d What should the Car's Max Upgrade Speed be?\n&a (default = 120.0)");
		this.addDefault("CarType.Create.Acceleration", "&d What should the Car's Acceleration be?\n&a (default = 1.0)");
		this.addDefault("CarType.Create.FuelUsage", "&d What should the Fuel Usage be?\n&a (default = 1.0)");
		this.addDefault("CarType.Create.FillMaterialData", "&d What should the Fill Material be?\n&a (default = minecraft:AIR)");
		this.addDefault("CarType.Create.Cost", "&d How much should the car cost?\n&a (default = 10.0)");
		this.addDefault("CarType.Create.Success", "&d All done, &a%VALUE% &dcreated.");
		this.addDefault("CarType.Removed", "&b%VALUE% &7removed.");
		this.addDefault("CarType.Error.InvalidName", "Invalid Car Type name.");
		this.addDefault("CarType.Error.InvalidValue", "Invalid Value.");
		this.addDefault("CarType.Error.AlreadyExists", "This Car Type already exists.");

		this.addDefault("BlockTypes.Added.List", "&b%MATERIAL% &7added to &b%TYPE% &7blocks.");
		this.addDefault("BlockTypes.Added.Amount", "&b%MATERIAL% &7added to &b%TYPE% &7blocks, with an amount of &b%AMOUNT%&7.");
		this.addDefault("BlockTypes.Removed", "&b%MATERIAL% &7removed from &b%TYPE% &7blocks.");

		this.addDefault("CarStore.Heading", "Car Store");
		this.addDefault("CarStore.Setup.Line1", "         ");
		this.addDefault("CarStore.Setup.Line2", " ggggggg ");
		this.addDefault("CarStore.Setup.Line3", "  fp nl  ");
		this.addDefault("CarStore.Material.Filler", "GRAY_STAINED_GLASS");

		this.addDefault("CarDetails.Heading", "Car Details");
		this.addDefault("CarDetails.Type", "&fCar Type: &b%VALUE%");
		this.addDefault("CarDetails.MaxSpeed", "&fMax Speed: &b%VALUE%");
		this.addDefault("CarDetails.CurrentSpeed", "&fCurrent Speed: &b%VALUE%");
		this.addDefault("CarDetails.Acceleration", "&fAcceleration: &b%VALUE%");
		this.addDefault("CarDetails.FuelUsage", "&fFuel Usage: &b%VALUE%");
		this.addDefault("CarDetails.Cost", "&fCost: &b%VALUE%");
		this.addDefault("CarDetails.Fuel", "&fFuel: &b%VALUE%");

		this.addDefault("PlaceholderAPI.NoCar", "Not Driving");

		this.addDefault("Error.NoPermission", "You do not have permission: &4%VALUE%");
		this.addDefault("Error.SignProtected", "This sign is protected.");
		this.addDefault("Error.UnknownCommand", "Unknown Command.");
		this.addDefault("Error.UnknownSignCommand", "Unknown Sign Command.");
		this.addDefault("Error.CommandDisabled", "This command has been disabled.");
		this.addDefault("Error.InCar", "You are already in a car.");
		this.addDefault("Error.NotInCar", "You are not in a car.");
		this.addDefault("Error.InvalidPlaceLocation", "Please place your car somewhere valid.");
		this.addDefault("Error.HaveCar", "You already have a car.");
		this.addDefault("Error.FuelDisabled", "Fuel is disabled.");
		this.addDefault("Error.PurchaseFailed", "Purchase failed. Cost: &4%VALUE%");
		this.addDefault("Error.FullyUpgraded", "Your car is already fully upgraded.");
		this.addDefault("Error.Owned", "This car is owned by &b%VALUE%");
		this.addDefault("Error.UnknownCarType", "Unknown car type.");
		this.addDefault("Error.UnknownPlayer", "Unknown player.");
		this.addDefault("Error.UnknownMaterial", "Unknown Material: &4%VALUE%");
		this.addDefault("Error.SpecifyPlayer", "Please specify a player.");
		this.addDefault("Error.PurchaseOutstanding", "You have an outstanding purchase.");
		this.addDefault("Error.NoPurchaseOutstanding", "You don't have an outstanding purchase.");
		this.addDefault("Error.CarNotDriven", "This car hasn't been driven yet.");
		this.addDefault("Error.NoOwnership", "This car doesn't have an owner.");
		this.addDefault("Error.NotHoldingCar", "You need to be holding a Car to give it.");
		this.addDefault("Error.TooMany", "Too many arguments! (%VALUE%)");
		this.addDefault("Error.TooLittle", "Not enough arguments! (%VALUE%)");
		this.addDefault("Error.OwnedCarsLimit", "You have reached the amount of Owned cars you can place.");
		this.addDefault("Error.InvalidPlaceableMaterial", "You are unable to place a Car here.");
		this.addDefault("Error.InvalidNumber", "&4%VALUE% &7is not a valid number.");

		this.addDefault("Error.BlockTypes.Invalid", "Invalid Block Type. Valid options: speed, climb, launch, placeable");
		this.addDefault("Error.BlockTypes.AlreadyExists", "%MATERIAL% is already a %TYPE% block.");
		this.addDefault("Error.BlockTypes.SpecifyAmount", "Invalid Syntax: /vehiclez add %VALUE% (material) (amount)");

		this.options().copyDefaults(true);
	}

}
