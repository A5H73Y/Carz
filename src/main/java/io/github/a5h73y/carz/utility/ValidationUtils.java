package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.enums.VehicleDetailKey;
import io.github.a5h73y.carz.model.Car;
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

		return !(vehicle.getLocation().getBlock().getBlockData() instanceof Rail);
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
			double cost = Carz.getInstance().getConfig()
					.getDouble("CarTypes." + carType.toLowerCase() + ".Cost");
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
}
