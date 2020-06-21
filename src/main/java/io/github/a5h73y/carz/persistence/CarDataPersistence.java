package io.github.a5h73y.carz.persistence;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

/**
 * Due to supporting < 1.14 the PersistentDataHolder had to be
 * separated from ItemStack and Vehicle (Entity).
 */
public interface CarDataPersistence {

	/**
	 * Retrieve the specified item meta value stored.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return stored value
	 */
	String getValue(VehicleDetailKey detailKey, ItemStack itemStack);

	/**
	 * Retrieve the specified value stored in the data holder.
	 * It must be checked that a value exists before it is retrieved.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 * @return stored value
	 */
	String getValue(VehicleDetailKey detailKey, Entity vehicle);

	/**
	 * Store the specified value in the item stack.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @param value value to store
	 */
	void setValue(VehicleDetailKey detailKey, ItemStack itemStack, String value);

	/**
	 * Store the specified value in the data holder.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 * @param value value to store
	 */
	void setValue(VehicleDetailKey detailKey, Entity vehicle, String value);

	/**
	 * Check if the item stacks storage contains the key.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return key exists
	 */
	boolean has(VehicleDetailKey detailKey, ItemStack itemStack);

	/**
	 * Check if the data holder contains the key.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 * @return key exists
	 */
	boolean has(VehicleDetailKey detailKey, Entity vehicle);

	/**
	 * Remove the key data from the data holder.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 */
	void remove(VehicleDetailKey detailKey, Entity vehicle);

	/**
	 * Transfer the values from one data holder to another.
	 * Each available {@link VehicleDetailKey} will be checked if it exists and then transferred.
	 *
	 * @param from sending data holder
	 * @param to receiving data holder
	 */
	void transferNamespaceKeyValues(Entity from, ItemStack to);

	/**
	 * Transfer the values from one data holder to another.
	 * Each available {@link VehicleDetailKey} will be checked if it exists and then transferred.
	 *
	 * @param from sending data holder
	 * @param to receiving data holder
	 */
	void transferNamespaceKeyValues(ItemStack from, Entity to);

	/**
	 * Print each summary detail to the requesting player.
	 *
	 * @param player requesting player
	 * @param vehicle {@link Vehicle}
	 */
	void printDataDetails(Player player, Entity vehicle);

	/**
	 * Print each summary detail to the requesting player.
	 *
	 * @param player requesting player
	 * @param itemStack {@link ItemStack}
	 */
	void printDataDetails(Player player, ItemStack itemStack);

}
