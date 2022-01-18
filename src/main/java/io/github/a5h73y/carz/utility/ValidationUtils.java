package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.configuration.impl.BlocksConfig;
import io.github.a5h73y.carz.enums.ConfigType;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.enums.VehicleDetailKey;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.DelayTasks;
import io.github.a5h73y.carz.persistence.CarDataPersistence;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

/**
 * Validation related utility methods.
 */
public class ValidationUtils {

	private static final List<String> RAIL_MATERIALS =
			Arrays.asList("RAILS", "ACTIVATOR_RAIL", "DETECTOR_RAIL", "POWERED_RAIL");

	/**
	 * Validate if the input is a valid String.
	 *
	 * @param input text
	 * @return input is a valid String
	 */
	public static boolean isStringValid(String input) {
		return input != null && !input.trim().isEmpty();
	}

	/**
	 * Validate if the input is a valid Integer.
	 *
	 * @param input text
	 * @return input is an Integer
	 */
	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception ignored) { }
		return false;
	}

	/**
	 * Validate if the input is a valid Double.
	 *
	 * @param input text
	 * @return input is a Double
	 */
	public static boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception ignored) { }
		return false;
	}

	/**
	 * Validate that the vehicle is a valid Carz vehicle.
	 * Checks to see if the vehicle is a Minecart that isn't on rails.
	 *
	 * @param vehicle vehicle
	 * @return is Carz vehicle
	 */
	public static boolean isACarzVehicle(Vehicle vehicle) {
		if (!(vehicle instanceof Minecart)) {
			return false;
		}

		if (isRail(vehicle.getLocation().getBlock())) {
			return false;
		}

		CarDataPersistence persistence = Carz.getInstance().getCarDataPersistence();

		if (!persistence.has(VehicleDetailKey.VEHICLE_TYPE, vehicle)) {
			return false;
		}

		return Carz.getInstance().getCarController().doesCarTypeExist(
				persistence.getValue(VehicleDetailKey.VEHICLE_TYPE, vehicle));
	}

	/**
	 * Validate if the player is currently able to purchase a car.
	 * This includes checking the permission status and economy funds.
	 *
	 * @param player target player
	 * @param carType requested car type
	 * @return player can purchase
	 */
	public static boolean canPurchaseCar(Player player, String carType) {
		return canPurchaseCar(player, carType, 0);
	}

	/**
	 * Validate if the player is currently able to purchase a car.
	 * This includes checking the permission status.
	 *
	 * @param player target player
	 * @param carType requested car type
	 * @param costOverride economy cost override
	 * @return player can purchase
	 */
	public static boolean canPurchaseCar(Player player, String carType, double costOverride) {
		if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
			return false;
		}

		if (player.isInsideVehicle()) {
			TranslationUtils.sendTranslation("Error.InCar", player);
			return false;
		}

		if (player.getInventory().contains(Material.MINECART)
				&& Carz.getInstance().getConfig().getBoolean("PreventCarPurchaseWhenExisting")) {
			TranslationUtils.sendTranslation("Error.HaveCar", player);
			return false;
		}

		if (!Carz.getInstance().getCarController().doesCarTypeExist(carType.toLowerCase())) {
			TranslationUtils.sendTranslation("Error.UnknownCarType", player);
			return false;
		}

		double cost = costOverride > 0 ? costOverride :
				Carz.getDefaultConfig().getDouble("CarTypes." + carType.toLowerCase() + ".Cost");
		return Carz.getInstance().getEconomyApi().canPurchase(player, cost);
	}

	/**
	 * Validate if the player is currently able to purchase an upgrade.
	 * This includes checking the permission status and economy funds.
	 *
	 * @param player target player
	 * @return player can upgrade
	 */
	public static boolean canPurchaseUpgrade(Player player) {
		return canPurchaseUpgrade(player, 0);
	}

	/**
	 * Validate if the player is currently able to purchase an upgrade.
	 * This includes checking the permission status.
	 *
	 * @param player target player
	 * @param costOverride economy cost override
	 * @return player can upgrade
	 */
	public static boolean canPurchaseUpgrade(Player player, double costOverride) {
		if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (!isACarzVehicle((Vehicle) player.getVehicle())) {
			return false;
		}

		if (!PermissionUtils.hasPermission(player, Permissions.UPGRADE)) {
			return false;
		}

		Car currentCar = Carz.getInstance().getCarController().getCar((Minecart) player.getVehicle());

		if (currentCar.getMaxSpeed() + Carz.getDefaultConfig().getUpgradeIncrement()
				> currentCar.getCarDetails().getMaxUpgradeSpeed()) {
			TranslationUtils.sendTranslation("Error.FullyUpgraded", player);
			return false;
		}

		double cost = costOverride > 0 ? costOverride :
				Carz.getDefaultConfig().getDouble("Vault.Cost.Upgrade");
		return Carz.getInstance().getEconomyApi().canPurchase(player, cost);
	}

	/**
	 * Validate if the player is currently able to purchase fuel.
	 * There is no permission node to purchase fuel.
	 *
	 * @param player target player
	 * @return player can refuel
	 */
	public static boolean canPurchaseFuel(Player player) {
		return canPurchaseFuel(player, 0);
	}

	/**
	 * Validate if the player is currently able to purchase fuel.
	 * There is no permission node to purchase fuel.
	 *
	 * @param player target player
	 * @param costOverride economy cost override
	 * @return player can refuel
	 */
	public static boolean canPurchaseFuel(Player player, double costOverride) {
		if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (!isACarzVehicle((Vehicle) player.getVehicle())) {
			return false;
		}

		if (!Carz.getInstance().getCarDataPersistence().has(VehicleDetailKey.VEHICLE_FUEL, player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.CarNotDriven", player);
			return false;
		}

		double cost;
		if (costOverride > 0) {
			cost = costOverride;

		} else {
			double remainingFuel = Double.parseDouble(Carz.getInstance().getCarDataPersistence().getValue(
					VehicleDetailKey.VEHICLE_FUEL, player.getVehicle()));
			cost = Carz.getInstance().getEconomyApi().getRefuelCost(remainingFuel);
		}
		return Carz.getInstance().getEconomyApi().canPurchase(player, cost);
	}

	/**
	 * Validate if the player is currently able to claim a car.
	 * There is no permission node to claim a car.
	 *
	 * @param player target player
	 * @return player can claim car
	 */
	public static boolean canClaimCar(Player player) {
		if (!player.isInsideVehicle() || !ValidationUtils.isACarzVehicle((Vehicle) player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (Carz.getInstance().getCarDataPersistence().has(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle())) {
			String owner = Carz.getInstance().getCarDataPersistence()
					.getValue(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle());
			TranslationUtils.sendValueTranslation("Error.Owned", owner, player);
			return false;
		}

		return true;
	}

	/**
	 * Validate if the player is able to remove ownership of a car.
	 * If they are the owner of the car, or have a permission override.
	 *
	 * @param player target player
	 * @return player can remove ownership of car
	 */
	public static boolean canRemoveCarOwnership(Player player) {
		if (!player.isInsideVehicle() || !ValidationUtils.isACarzVehicle((Vehicle) player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (!Carz.getInstance().getCarDataPersistence().has(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.NoOwnership", player);
			return false;
		}

		String owner = Carz.getInstance().getCarDataPersistence().getValue(
				VehicleDetailKey.VEHICLE_OWNER, player.getVehicle());

		if (!PermissionUtils.hasStrictPermission(player, Permissions.BYPASS_OWNER, false)
				&& !owner.equalsIgnoreCase(player.getName())) {
			TranslationUtils.sendValueTranslation("Error.Owned", owner, player);
			return false;
		}

		return true;
	}

	/**
	 * Validate if the player is currently able to place a car.
	 * There is no permission node to claim a car.
	 *
	 * @param player target player
	 * @param placedMaterial material to place upon
	 * @return player can claim car
	 */
	public static boolean canPlaceCar(Player player, Material placedMaterial) {
		if (!PermissionUtils.hasPermission(player, Permissions.PLACE)) {
			return false;
		}

		// prevent the player from creating mass amounts of Minecarts
		if (!DelayTasks.getInstance().delayPlayer(player, 3)) {
			return false;
		}

		BlocksConfig blocksConfig = (BlocksConfig) Carz.getConfig(ConfigType.BLOCKS);
		if (!blocksConfig.getPlaceableBlocks().isEmpty()
				&& !blocksConfig.getPlaceableBlocks().contains(placedMaterial)) {
			TranslationUtils.sendTranslation("Error.InvalidPlaceableMaterial", player);
			return false;
		}

		int maxOwnedCars = Carz.getDefaultConfig().getMaxPlayerOwnedCars();
		if (maxOwnedCars > 0
				&& CarUtils.numberOfOwnedCars(player) >= maxOwnedCars) {
			TranslationUtils.sendTranslation("Error.OwnedCarsLimit", player);
			return false;
		}

		return true;
	}

	/**
	 * Validate if the player is currently able to give a car.
	 * This includes checking the permission status.
	 *
	 * @param player requesting player
	 * @param targetPlayerName target player name
	 * @return player can give car
	 */
	public static boolean canGiveCar(Player player, String targetPlayerName) {
		if (!PermissionUtils.hasPermission(player, Permissions.GIVE)) {
			return false;
		}

		ItemStack itemStack = player.getInventory().getItemInMainHand();

		if (itemStack.getType() != Material.MINECART) {
			TranslationUtils.sendTranslation("Error.NotHoldingCar", player);
			return false;
		}

		CarDataPersistence carDataPersistence = Carz.getInstance().getCarDataPersistence();

		if (!carDataPersistence.has(VehicleDetailKey.VEHICLE_TYPE, itemStack)) {
			TranslationUtils.sendTranslation("Error.NotHoldingCar", player);
			return false;
		}

		Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
		if (targetPlayer == null || !targetPlayer.isOnline()) {
			TranslationUtils.sendTranslation("Error.UnknownPlayer", player);
			return false;
		}

		return true;
	}

	public static boolean isRail(Block block) {
		if (PluginUtils.getMinorServerVersion() >= 13) {
			return block.getBlockData() instanceof Rail;
		} else {
			return RAIL_MATERIALS.contains(block.getType().name());
		}
	}
}
