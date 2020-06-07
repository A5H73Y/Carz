package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.configuration.impl.BlocksConfig;
import io.github.a5h73y.carz.enums.ConfigType;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.enums.VehicleDetailKey;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.DelayTasks;
import org.bukkit.Material;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

/**
 * Validation related utility methods.
 */
public class ValidationUtils {

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

		if (vehicle.getLocation().getBlock().getBlockData() instanceof Rail) {
			return false;
		}

		return Carz.getInstance().getItemMetaUtils().has(VehicleDetailKey.VEHICLE_TYPE, vehicle);
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
		return canPurchaseCar(player, carType, true);
	}

	/**
	 * Validate if the player is currently able to purchase a car.
	 * This includes checking the permission status.
	 *
	 * @param player target player
	 * @param carType requested car type
	 * @param checkEconomy validate the economy
	 * @return player can purchase
	 */
	public static boolean canPurchaseCar(Player player, String carType, boolean checkEconomy) {
		if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
			return false;
		}

		if (player.isInsideVehicle()) {
			TranslationUtils.sendTranslation("Error.InCar", player);
			return false;
		}

		if (player.getInventory().contains(Material.MINECART)) {
			TranslationUtils.sendTranslation("Error.HaveCar", player);
			return false;
		}

		if (!Carz.getInstance().getCarController().doesCarTypeExist(carType.toLowerCase())) {
			TranslationUtils.sendTranslation("Error.UnknownCarType", player);
			return false;
		}

		if (checkEconomy) {
			double cost = Carz.getDefaultConfig().getDouble("CarTypes." + carType.toLowerCase() + ".Cost");
			return Carz.getInstance().getEconomyAPI().canPurchase(player, cost);
		} else {
			return true;
		}
	}

	/**
	 * Validate if the player is currently able to purchase an upgrade.
	 * This includes checking the permission status and economy funds.
	 *
	 * @param player target player
	 * @return player can upgrade
	 */
	public static boolean canPurchaseUpgrade(Player player) {
		return canPurchaseUpgrade(player, true);
	}

	/**
	 * Validate if the player is currently able to purchase an upgrade.
	 * This includes checking the permission status.
	 *
	 * @param player target player
	 * @param checkEconomy validate the economy
	 * @return player can upgrade
	 */
	public static boolean canPurchaseUpgrade(Player player, boolean checkEconomy) {
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

		Car currentCar = Carz.getInstance().getCarController().getCar(player.getVehicle().getEntityId());

		if (currentCar.getMaxSpeed() + Carz.getDefaultConfig().getUpgradeIncrement()
				>= currentCar.getCarDetails().getMaxUpgradeSpeed()) {
			TranslationUtils.sendTranslation("Error.FullyUpgraded", player);
			return false;
		}

		if (checkEconomy) {
			double cost = Carz.getDefaultConfig().getDouble("Vault.Cost.Upgrade");
			return Carz.getInstance().getEconomyAPI().canPurchase(player, cost);
		} else {
			return true;
		}
	}

	/**
	 * Validate if the player is currently able to purchase fuel.
	 * There is no permission node to purchase fuel.
	 *
	 * @param player target player
	 * @return player can refuel
	 */
	public static boolean canPurchaseFuel(Player player) {
		return canPurchaseFuel(player, true);
	}

	/**
	 * Validate if the player is currently able to purchase fuel.
	 * There is no permission node to purchase fuel.
	 *
	 * @param player target player
	 * @param checkEconomy validate the economy
	 * @return player can refuel
	 */
	public static boolean canPurchaseFuel(Player player, boolean checkEconomy) {
		if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (!isACarzVehicle((Vehicle) player.getVehicle())) {
			return false;
		}

		if (!Carz.getInstance().getItemMetaUtils().has(VehicleDetailKey.VEHICLE_FUEL, player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.CarNotDriven", player);
			return false;
		}

		if (checkEconomy) {
			double remainingFuel = Double.parseDouble(Carz.getInstance().getItemMetaUtils().getValue(
					VehicleDetailKey.VEHICLE_FUEL, player.getVehicle()));
			double cost = Carz.getInstance().getEconomyAPI().getRefuelCost(remainingFuel);
			return Carz.getInstance().getEconomyAPI().canPurchase(player, cost);
		} else {
			return true;
		}
	}

	/**
	 * Validate if the player is currently able to claim a car.
	 * There is no permission node to claim a car.
	 *
	 * @param player target player
	 * @return player can claim car
	 */
	public static boolean canClaimCar(Player player) {
		if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)
				|| !ValidationUtils.isACarzVehicle((Vehicle) player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (Carz.getInstance().getItemMetaUtils().has(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle())) {
			String owner = Carz.getInstance().getItemMetaUtils()
					.getValue(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle());
			player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
					.replace("%PLAYER%", owner));
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
		if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)
				|| !ValidationUtils.isACarzVehicle((Vehicle) player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (!Carz.getInstance().getItemMetaUtils().has(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle())) {
			TranslationUtils.sendTranslation("Error.NoOwnership", player);
			return false;
		}

		String owner = Carz.getInstance().getItemMetaUtils().getValue(
				VehicleDetailKey.VEHICLE_OWNER, player.getVehicle());

		if (!PermissionUtils.hasStrictPermission(player, Permissions.BYPASS_OWNER, false)
				&& !owner.equalsIgnoreCase(player.getName())) {
			player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
					.replace("%PLAYER%", owner));
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
}
