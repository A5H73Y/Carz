package io.github.a5h73y.carz.utility;

import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_SPEED;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_TYPE;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.model.CarDetails;
import io.github.a5h73y.carz.persistence.CarDataPersistence;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
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
				Carz.getInstance().getCarController().removeCar(entity);
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

		CarDataPersistence carDataPersistence = Carz.getInstance().getCarDataPersistence();
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
		if (!Carz.getInstance().getCarController().doesCarTypeExist(carType)) {
			TranslationUtils.sendTranslation("Error.UnknownCarType", player);
			return;
		}

		ItemStack itemStack = new ItemStack(Material.MINECART);
		Carz.getInstance().getCarDataPersistence().setValue(VEHICLE_TYPE, itemStack, carType);

		if (owner) {
			Carz.getInstance().getCarDataPersistence().setValue(VEHICLE_OWNER, itemStack, player.getName());
			setOwnerDisplayName(itemStack, player);
		}
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
		CarDataPersistence carDataPersistence = Carz.getInstance().getCarDataPersistence();
		ItemStack itemStack = new ItemStack(Material.MINECART);

		carDataPersistence.transferNamespaceKeyValues(vehicle, itemStack);

		if (carDataPersistence.has(VEHICLE_OWNER, itemStack)) {
			setOwnerDisplayName(itemStack, carDataPersistence.getValue(VEHICLE_OWNER, itemStack));
		}
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
		if (Carz.getDefaultConfig().getBoolean("CarItem.DisplaySummaryInformation")
				&& itemStack.hasItemMeta()) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(TranslationUtils.getTranslation("Car.PlayerCar", false)
					.replace("%PLAYER%", playerName));
			itemStack.setItemMeta(itemMeta);
		}
	}

	/**
	 * Add Car's summary information to the ItemStack.
	 * All details will be derived from the item meta utils.
	 *
	 * @param itemStack item stack
	 */
	public static void setCarSummaryInformation(ItemStack itemStack) {
		Carz carz = Carz.getInstance();

		if (Carz.getDefaultConfig().getBoolean("CarItem.DisplaySummaryInformation")
				&& itemStack.hasItemMeta()
				&& carz.getCarDataPersistence().has(VEHICLE_TYPE, itemStack)) {

			String vehicleType = carz.getCarDataPersistence().getValue(VEHICLE_TYPE, itemStack);
			CarDetails details = carz.getCarController().getCarTypes().get(vehicleType);
			boolean hasUpgrade = carz.getCarDataPersistence().has(VEHICLE_SPEED, itemStack);
			boolean hasFuel = carz.getCarDataPersistence().has(VEHICLE_FUEL, itemStack);

			String maxSpeed = !hasUpgrade ? String.valueOf(details.getStartMaxSpeed()) :
					carz.getCarDataPersistence().getValue(VEHICLE_SPEED, itemStack);

			String fuel = !hasFuel ? String.valueOf(carz.getFuelController().getMaxCapacity()) :
					carz.getCarDataPersistence().getValue(VEHICLE_FUEL, itemStack);

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
		ItemStack itemStack = new ItemStack(Carz.getDefaultConfig().getKey());
		String keyName = TranslationUtils.getTranslation("Car.Key.Display", false)
				.replace("%PLAYER%", player.getName());

		Carz.getInstance().getCarDataPersistence().setValue(VEHICLE_OWNER, itemStack, player.getName());
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(keyName);

		if (Carz.getDefaultConfig().getBoolean("Key.Glow")) {
			itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		itemStack.setItemMeta(itemMeta);
		player.getInventory().addItem(itemStack);
		TranslationUtils.sendTranslation("Car.Key.Received", player);
	}
}
