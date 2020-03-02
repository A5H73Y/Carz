package io.github.a5h73y.utility;

import io.github.a5h73y.Carz;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.a5h73y.controllers.CarController.DEFAULT_CAR;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_TYPE;

public class CarUtils {

	/**
	 * Destroy all Minecarts on the server.
	 */
	public static void destroyAllCars() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Minecart) {
					entity.remove();
				}
			}
		}
	}

	/**
	 * Place an Minecart in the player's inventory with their name on it.
	 * @param player
	 */
	public static void givePlayerOwnedCar(Player player, String carType) {
		ItemStack itemStack = new ItemStack(Material.MINECART);

		Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_TYPE, itemStack, carType);
		Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_OWNER, itemStack, player.getName());

		setOwnerDisplayName(itemStack, player);

		player.getInventory().addItem(itemStack);
		player.updateInventory();
	}

	/**
	 * Place an Minecart in the player's inventory with their name on it.
	 * @param player
	 */
	public static void givePlayerOwnedCar(Player player) {
		givePlayerOwnedCar(player, DEFAULT_CAR);
	}

	public static void givePlayerOwnedCar(Player player, Vehicle vehicle) {
		ItemStack itemStack = new ItemStack(Material.MINECART);

		ItemMeta itemMeta = itemStack.getItemMeta();
		Carz.getInstance().getItemMetaUtils().transferNamespaceKeyValues(vehicle, itemMeta);
		itemStack.setItemMeta(itemMeta);

		setOwnerDisplayName(itemStack, player);

		player.getInventory().addItem(itemStack);
		player.updateInventory();
	}

	public static void setOwnerDisplayName(ItemStack itemStack, Player player) {
		if (itemStack.hasItemMeta()) {
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(TranslationUtils.getTranslation("Car.PlayerCar", false)
					.replace("%PLAYER%", player.getName()));
			itemStack.setItemMeta(itemMeta);
		}
	}

}
