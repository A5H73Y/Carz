package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.persistence.ItemMetaUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_TYPE;

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

		ItemMetaUtils itemMetaUtils = Carz.getInstance().getItemMetaUtils();
		for (Minecart vehicle : player.getWorld().getEntitiesByClass(Minecart.class)) {
			if (itemMetaUtils.has(VEHICLE_OWNER, vehicle)
					&& itemMetaUtils.getValue(VEHICLE_OWNER, vehicle).equals(player.getName())) {
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
		Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_TYPE, itemStack, carType);

		if (owner) {
			Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_OWNER, itemStack, player.getName());
			setOwnerDisplayName(itemStack, player);
		}

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
		ItemMetaUtils itemMetaUtils = Carz.getInstance().getItemMetaUtils();
		ItemStack itemStack = new ItemStack(Material.MINECART);

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMetaUtils.transferNamespaceKeyValues(vehicle, itemMeta);
		itemStack.setItemMeta(itemMeta);

		if (itemMetaUtils.has(VEHICLE_OWNER, vehicle)) {
			setOwnerDisplayName(itemStack, itemMetaUtils.getValue(VEHICLE_OWNER, vehicle));
		}

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
		if (itemStack.hasItemMeta()) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(TranslationUtils.getTranslation("Car.PlayerCar", false)
					.replace("%PLAYER%", playerName));
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

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(keyName);
		Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_OWNER, itemMeta, player.getName());

		if (Carz.getDefaultConfig().getBoolean("Key.Glow")) {
			itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		itemStack.setItemMeta(itemMeta);
		player.getInventory().addItem(itemStack);
		TranslationUtils.sendTranslation("Car.Key.Received", player);
	}
}
