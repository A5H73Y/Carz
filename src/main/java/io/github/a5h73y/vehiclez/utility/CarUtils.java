package io.github.a5h73y.vehiclez.utility;

import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_SPEED;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_TYPE;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.model.Car;
import io.github.a5h73y.vehiclez.model.CarDetails;
import io.github.a5h73y.vehiclez.persistence.CarDataPersistence;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Car related utility methods.
 */
public class CarUtils {

	/**
	 * Destroy all Minecarts on the server.
	 */
	public static void destroyAllCars() {
		for (World world : Bukkit.getWorlds()) {
			for (Minecart entity : world.getEntitiesByClass(Minecart.class)) {
				Vehiclez.getInstance().getCarController().removeCar(entity);
			}
		}
	}

	/**
	 * Get the number of owned Minecart entities for the player.
	 *
	 * @param player requesting player
	 * @return player owned minecarts
	 */
	public static int numberOfOwnedCars(Player player) {
		int number = 0;

		CarDataPersistence carDataPersistence = Vehiclez.getInstance().getCarDataPersistence();
		for (Minecart vehicle : player.getWorld().getEntitiesByClass(Minecart.class)) {
			if (carDataPersistence.has(VEHICLE_OWNER, vehicle)
					&& carDataPersistence.getValue(VEHICLE_OWNER, vehicle).equals(player.getName())) {
				number++;
			}
		}

		return number;
	}

	/**
	 * Place a Car (Minecart) of the specified type in the player's inventory.
	 * The car will not have an owner.
	 *
	 * @param player target player
	 * @param carType the type of car
	 */
	public static void givePlayerCar(Player player, String carType) {
		givePlayerCar(player, carType, false);
	}

	/**
	 * Place a Car (Minecart) of the specified type in the player's inventory.
	 * The player will become the owner if specified.
	 *
	 * @param player target player
	 * @param carType the type of car
	 * @param owner mark player as owner
	 */
	public static void givePlayerCar(Player player, String carType, boolean owner) {
		if (!Vehiclez.getInstance().getCarController().doesCarTypeExist(carType)) {
			TranslationUtils.sendTranslation("Error.UnknownCarType", player);
			return;
		}

		ItemStack itemStack = new ItemStack(Material.MINECART);

		if (owner) {
			setOwnerDisplayName(itemStack, player);
			Vehiclez.getInstance().getCarDataPersistence().setValue(VEHICLE_OWNER, itemStack, player.getName());
		}
		Vehiclez.getInstance().getCarDataPersistence().setValue(VEHICLE_TYPE, itemStack, carType);
		setCarSummaryInformation(itemStack);

		player.getInventory().addItem(itemStack);
		player.updateInventory();
	}

	/**
	 * Transfer the Minecart to the player's inventory.
	 * Each namespace key will be persisted, with the name (if any) added to the Minecart item.
	 *
	 * @param player target player
	 * @param vehicle target minecart
	 */
	public static void transferMinecartToInventory(Player player, Minecart vehicle) {
		CarDataPersistence carDataPersistence = Vehiclez.getInstance().getCarDataPersistence();
		ItemStack itemStack = new ItemStack(Material.MINECART);

		if (carDataPersistence.has(VEHICLE_OWNER, vehicle)) {
			setOwnerDisplayName(itemStack, carDataPersistence.getValue(VEHICLE_OWNER, vehicle));
		}
		carDataPersistence.transferNamespaceKeyValues(vehicle, itemStack);
		setCarSummaryInformation(itemStack);

		player.getInventory().addItem(itemStack);
		player.updateInventory();
	}

	/**
	 * Add the player's name to the ItemStack.
	 *
	 * @param itemStack item stack
	 * @param player owner of the car
	 */
	public static void setOwnerDisplayName(ItemStack itemStack, Player player) {
		setOwnerDisplayName(itemStack, player.getName());
	}

	/**
	 * Add the player's name to the ItemStack.
	 *
	 * @param itemStack item stack
	 * @param playerName owner of the car
	 */
	public static void setOwnerDisplayName(ItemStack itemStack, String playerName) {
		if (Vehiclez.getDefaultConfig().getBoolean("CarItem.DisplayOwner")) {
			String playerCar = TranslationUtils.getValueTranslation("Car.PlayerCar", playerName, false);
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(playerCar);
			itemStack.setItemMeta(itemMeta);
		}
	}

	/**
	 * Add Car's summary information to the ItemStack.
	 * All details will be derived from the item meta utils.
	 * Only server versions 1.14+ will apply these effects as changes to ItemMeta will change ItemStack hashcode.
	 *
	 * @param itemStack item stack
	 */
	public static void setCarSummaryInformation(ItemStack itemStack) {
		Vehiclez vehiclez = Vehiclez.getInstance();

		if (PluginUtils.getMinorServerVersion() >= 14
				&& Vehiclez.getDefaultConfig().getBoolean("CarItem.DisplaySummaryInformation")
				&& vehiclez.getCarDataPersistence().has(VEHICLE_TYPE, itemStack)) {

			String vehicleType = vehiclez.getCarDataPersistence().getValue(VEHICLE_TYPE, itemStack);
			CarDetails details = vehiclez.getCarController().getCarTypes().get(vehicleType);
			boolean hasUpgrade = vehiclez.getCarDataPersistence().has(VEHICLE_SPEED, itemStack);
			boolean hasFuel = vehiclez.getCarDataPersistence().has(VEHICLE_FUEL, itemStack);

			String maxSpeed = !hasUpgrade ? String.valueOf(details.getStartMaxSpeed()) :
					vehiclez.getCarDataPersistence().getValue(VEHICLE_SPEED, itemStack);

			String fuel = !hasFuel ? String.valueOf(vehiclez.getFuelController().getMaxCapacity()) :
					vehiclez.getCarDataPersistence().getValue(VEHICLE_FUEL, itemStack);

			List<String> lore = Arrays.asList(
					TranslationUtils.getValueTranslation("CarDetails.Type", vehicleType, false),
					TranslationUtils.getValueTranslation("CarDetails.MaxSpeed", maxSpeed, false),
					TranslationUtils.getValueTranslation("CarDetails.Fuel", fuel, false));

			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
		}
	}

	/**
	 * Give the player a customised key.
	 * If enabled, a Durability enchantment will be applied to give a glowing effect.
	 *
	 * @param player target player
	 */
	public static void givePlayerKey(Player player) {
		ItemStack itemStack = new ItemStack(Vehiclez.getDefaultConfig().getKey());
		String keyName = TranslationUtils.getValueTranslation("Car.Key.Display", player.getName(), false);

		Vehiclez.getInstance().getCarDataPersistence().setValue(VEHICLE_OWNER, itemStack, player.getName());
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(keyName);

		if (Vehiclez.getDefaultConfig().getBoolean("Key.Glow")) {
			itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		itemStack.setItemMeta(itemMeta);
		player.getInventory().addItem(itemStack);
		TranslationUtils.sendTranslation("Car.Key.Received", player);
	}

	/**
	 * Display a summary of Car information to the player.
	 * Works with a driving Car or a valid Minecart ItemStack.
	 *
	 * @param player requesting player
	 * @param args command arguments
	 */
	public static void showCarDetails(Player player, String[] args) {
		Vehiclez vehiclez = Vehiclez.getInstance();
		boolean extraDetails = args.length == 2 && args[1].equalsIgnoreCase("extra");

		if (player.isInsideVehicle() && ValidationUtils.isAVehiclezVehicle((Vehicle) player.getVehicle())) {
			Car playerCar = vehiclez.getCarController().getCar((Minecart) player.getVehicle());
			TranslationUtils.sendHeading(TranslationUtils.getTranslation("CarDetails.Heading", false), player);
			if (!extraDetails) {
				player.sendMessage(playerCar.getSummary());
			} else {
				player.sendMessage(playerCar.toString());
				vehiclez.getCarDataPersistence().printDataDetails(player, player.getVehicle());
			}

		} else if (player.getInventory().getItemInMainHand().getType() == Material.MINECART
				&& vehiclez.getCarDataPersistence().has(VEHICLE_TYPE, player.getInventory().getItemInMainHand())) {
			TranslationUtils.sendHeading(TranslationUtils.getTranslation("CarDetails.Heading", false), player);
			vehiclez.getCarDataPersistence().printDataDetails(player,
					player.getInventory().getItemInMainHand());

		} else {
			TranslationUtils.sendTranslation("Error.NotInCar", player);
		}
	}

	public static Player getPlayerDrivingVehicle(Vehicle vehicle) {
		Player result;

		try {
			result = !vehicle.getPassengers().isEmpty() && vehicle.getPassengers().get(0) instanceof Player
					? (Player) vehicle.getPassengers().get(0) : null;
		} catch (NoSuchMethodError ex) {
			result = vehicle.getPassenger() != null && vehicle.getPassenger() instanceof Player
					? (Player) vehicle.getPassenger() : null;
		}

		return result;
	}
}
