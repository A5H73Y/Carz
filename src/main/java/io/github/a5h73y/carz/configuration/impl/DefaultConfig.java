package io.github.a5h73y.carz.configuration.impl;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.configuration.CarzConfiguration;
import io.github.a5h73y.carz.utility.StringUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class DefaultConfig extends CarzConfiguration {

	@Override
	protected String getFileName() {
		return "config.yml";
	}

	/**
	 * Initialise the config.yml on startup.
	 * Values will be defaulted if not set.
	 */
	@Override
	protected void initializeConfig() {
		this.options().header("==== Carz Config ==== #");

		this.addDefault("Key.Material", "STICK");
		this.addDefault("Key.Glow", true);
		this.addDefault("Key.GiveOnCarEnter", true);
		this.addDefault("Key.RequireCarzKey", true);
		this.addDefault("Key.AutomaticLocking", true);
		this.addDefault("Key.SneakLockAction", true);

		this.addDefault("CarItem.DisplayOwner", true);
		this.addDefault("CarItem.DisplaySummaryInformation", true);

		this.addDefault("Speed.Upgrade.Increment", 25.0);

		this.addDefault("CommandEnabled.Spawn", true);
		this.addDefault("CommandEnabled.Purchase", true);
		this.addDefault("CommandEnabled.Refuel", true);
		this.addDefault("CommandEnabled.Upgrade", true);
		this.addDefault("CommandEnabled.Store", true);

		this.addDefault("Fuel.Enabled", true);
		this.addDefault("Fuel.ScaleCost", true);
		this.addDefault("Fuel.MaxCapacity", 3000.0);
		this.addDefault("Fuel.GaugeScale", 40);

		this.addDefault("ClimbBlocks.AllSlabs", true);
		this.addDefault("ClimbBlocks.Strength", 0.25);

		this.addDefault("Vault.Enabled", true);
		this.addDefault("Vault.ConfirmPurchases", true);
		this.addDefault("Vault.Cost.Upgrade", 8.0);
		this.addDefault("Vault.Cost.Refuel", 2.0);

		this.addDefault("BountifulAPI.Enabled", true);

		this.addDefault("PlaceholderAPI.Enabled", true);

		this.addDefault("Other.ControlCarsWhileFalling", true);
		this.addDefault("Other.DamageEntities.Enabled", false);
		this.addDefault("Other.DamageEntities.Damage", 5.0);
		this.addDefault("Other.DestroyInLiquid", true);
		this.addDefault("Other.DisableFallDamage", false);
		this.addDefault("Other.OnlyOwnedCarsDrive", false);
		this.addDefault("Other.MaxPlayerOwnedCars", 5);
		this.addDefault("Other.PreventCarPurchaseWhenExisting", false);
		this.addDefault("Other.SignProtection", true);
		this.addDefault("Other.StartCarOnVehicleEnter", false);
		this.addDefault("Other.UpdateCheck", true);
		this.addDefault("Other.UseAutoTabCompletion", true);
		this.addDefault("Other.UseEffects", true);
		this.addDefault("Other.UsePermissions", true);

		this.addDefault("CarTypes.default.StartMaxSpeed", 60.0);
		this.addDefault("CarTypes.default.MaxUpgradeSpeed", 200.0);
		this.addDefault("CarTypes.default.Acceleration", 5.0);
		this.addDefault("CarTypes.default.FuelUsage", 1.0);
		this.addDefault("CarTypes.default.FillMaterial", "AIR");
		this.addDefault("CarTypes.default.Cost", 10.0);

		this.addDefault("Version", Double.valueOf(Carz.getInstance().getDescription().getVersion()));

		this.options().copyDefaults(true);
	}

	public Material getKey() {
		return Material.getMaterial(this.getString("Key.Material"));
	}

	public String getSignHeader() {
		return TranslationUtils.getTranslation("Carz.SignHeader", false);
	}

	public String getStrippedSignHeader() {
		return ChatColor.stripColor(getSignHeader());
	}

	public boolean isDestroyInLiquid() {
		return this.getBoolean("Other.DestroyInLiquid");
	}

	public boolean isOnlyOwnedCarsDrive() {
		return this.getBoolean("Other.OnlyOwnedCarsDrive");
	}

	public boolean isControlCarsWhileFalling() {
		return this.getBoolean("Other.ControlCarsWhileFalling");
	}

	public boolean isFuelScaleCost() {
		return this.getBoolean("Fuel.ScaleCost");
	}

	public boolean isAutomaticLocking() {
		return this.getBoolean("Key.AutomaticLocking");
	}

	public boolean isSneakLockAction() {
		return this.getBoolean("Key.SneakLockAction");
	}

	public boolean isAllSlabsClimb() {
		return this.getBoolean("ClimbBlocks.AllSlabs");
	}

	public boolean isFallDamageDisabled() {
		return this.getBoolean("Other.DisableFallDamage");
	}

	public boolean isGiveKeyOnEnter() {
		return this.getBoolean("Key.GiveOnCarEnter");
	}

	public double getUpgradeIncrement() {
		return this.getDouble("Speed.Upgrade.Increment");
	}

	public double getClimbBlockStrength() {
		return this.getDouble("ClimbBlocks.Strength");
	}

	public int getMaxPlayerOwnedCars() {
		return this.getInt("Other.MaxPlayerOwnedCars");
	}
}
