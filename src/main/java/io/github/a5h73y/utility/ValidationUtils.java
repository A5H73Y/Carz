package io.github.a5h73y.utility;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.enums.PurchaseType;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.other.XMaterial;
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
		return input != null && input.trim().length() != 0;
	}

	/**
	 * Check if the argument is numeric.
	 * "1" - true, "Hi" - false
	 * @param text
	 * @return whether the input is numeric
	 */
	public static boolean isNumber(String text) {
		try {
			Integer.parseInt(text);
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
	 * @return boolean
	 */
	public static boolean canPurchaseCar(Player player) {
		if (player.isInsideVehicle()) {
			TranslationUtils.sendTranslation("Error.InCar", player);
			return false;
		}

		if (player.getInventory().contains(Material.MINECART)) {
			TranslationUtils.sendTranslation("Error.HaveCar", player);
			return false;
		}

		if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
			return false;
		}

		return Carz.getInstance().getEconomyAPI().processPurchase(player, PurchaseType.CAR);
	}

	/**
	 * Check to see if the player is currently able to purchase an upgrade
	 * This includes checking the permission status
	 * @param player
	 * @return boolean
	 */
	public static boolean canPurchaseUpgrade(Player player) {
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

		double currentSpeed = Carz.getInstance().getCarController().getCar(player.getVehicle().getEntityId()).getMaxSpeed();

		if (currentSpeed >= Carz.getInstance().getSettings().getUpgradeMaxSpeed()) {
			TranslationUtils.sendTranslation("Error.FullyUpgraded", player);
			return false;
		}

		return Carz.getInstance().getEconomyAPI().processPurchase(player, PurchaseType.UPGRADE);
	}

	/**
	 * Check to see if the player is currently able to purchase fuel
	 * There will be no permission node to purchase fuel, as this would be silly
	 * @param player
	 * @return boolean
	 */
	public static boolean canPurchaseFuel(Player player) {
		if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
			return false;
		}

		if (!isACarzVehicle((Vehicle) player.getVehicle())) {
			return false;
		}

		Car car = Carz.getInstance().getCarController().getCar(player.getVehicle().getEntityId());
		return Carz.getInstance().getEconomyAPI().processFuelPurchase(player, car);
	}
}
