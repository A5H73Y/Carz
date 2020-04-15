package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.enums.VehicleDetailKey;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

public class ValidationUtils {

	/**
	 * Validate if the input is a populated String
	 * @param input
	 * @return whether the input is a valid String
	 */
	public static boolean isStringValid(String input) {
		return input != null && !input.trim().isEmpty();
	}

	/**
	 * Check if the argument is an integer.
	 * "1" - true, "Hi" - false
	 * @param text
	 * @return whether the input is an integer
	 */
	public static boolean isInteger(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception ignored) {}
		return false;
	}

	/**
	 * Check if the argument is a double.
	 * "1" - true, "Hi" - false
	 * @param text
	 * @return whether the input is a double
	 */
	public static boolean isDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (Exception ignored) {}
		return false;
	}

	/**
	 * Validate that the vehicle is a valid Carz vehicle
	 * Checks to see if the vehicle is a Minecart that isn't on rails
	 * @param cart
	 * @return boolean
	 */
	public static boolean isACarzVehicle(Vehicle cart) {
		if (!(cart instanceof Minecart)) {
			return false;
		}

		Material material = cart.getLocation().getBlock().getType();
		return (material != XMaterial.RAIL.parseMaterial())
				&& (material != Material.POWERED_RAIL)
				&& (material != Material.DETECTOR_RAIL);
	}

	/**
	 * Check to see if the player is currently able to purchase a car
	 * This includes checking the permission status
	 * @param player
	 * @param carType
	 * @return boolean
	 */
	public static boolean canPurchaseCar(Player player, String carType) {
		return canPurchaseCar(player, carType, true);
	}

	/**
	 * Check to see if the player is currently able to purchase a car
	 * This includes checking the permission status
	 * @param player
	 * @param carType
	 * @param checkEconomy
	 * @return boolean
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
			double cost = Carz.getInstance().getConfig().getDouble("CarTypes." + carType.toLowerCase() + ".Cost");
			return Carz.getInstance().getEconomyAPI().canPurchase(player, cost);
		} else {
			return true;
		}
	}

	/**
	 * Check to see if the player is currently able to purchase an upgrade
	 * This includes checking the permission status
	 * @param player
	 * @return boolean
	 */
	public static boolean canPurchaseUpgrade(Player player) {
		return canPurchaseUpgrade(player, true);
	}

	/**
	 * Check to see if the player is currently able to purchase an upgrade
	 * This includes checking the permission status
	 * @param player
	 * @param checkEconomy
	 * @return boolean
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

		if (currentCar.getMaxSpeed() >= Carz.getInstance().getSettings().getUpgradeMaxSpeed()) {
			TranslationUtils.sendTranslation("Error.FullyUpgraded", player);
			return false;
		}

		if (checkEconomy) {
			double cost = Carz.getInstance().getConfig().getDouble("Vault.Cost.Upgrade");
			return Carz.getInstance().getEconomyAPI().canPurchase(player, cost);
		} else {
			return true;
		}
	}

	/**
	 * Check to see if the player is currently able to purchase fuel
	 * There will be no permission node to purchase fuel, as this would be silly
	 * @param player
	 * @return boolean
	 */
	public static boolean canPurchaseFuel(Player player) {
		return canPurchaseFuel(player, true);
	}

	/**
	 * Check to see if the player is currently able to purchase fuel
	 * There will be no permission node to purchase fuel, as this would be silly
	 * @param player
	 * @param checkEconomy
	 * @return boolean
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
			player.sendMessage(Carz.getPrefix() + "This car hasn't been driven yet.");
			return false;
		}

		if (checkEconomy) {
			double remainingFuel = Double.parseDouble(Carz.getInstance().getItemMetaUtils().getValue(VehicleDetailKey.VEHICLE_FUEL, player.getVehicle()));
			double cost = Carz.getInstance().getEconomyAPI().getRefuelCost(remainingFuel);
			return Carz.getInstance().getEconomyAPI().canPurchase(player, cost);
		} else {
			return true;
		}
	}
}
