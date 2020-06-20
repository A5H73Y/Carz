package io.github.a5h73y.carz.persistence;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;

public interface CarDataPersistence {

	/**
	 * Retrieve the specified item meta value stored.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return stored value
	 */
	String getValue(VehicleDetailKey keyName, ItemStack itemStack);

	/**
	 * Retrieve the specified value stored in the data holder.
	 * It must be checked that a value exists before it is retrieved.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 * @return stored value
	 */
	String getValue(VehicleDetailKey keyName, Entity vehicle);

	/**
	 * Store the specified value in the item stack.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @param value value to store
	 */
	ItemStack setValue(VehicleDetailKey keyName, ItemStack itemStack, String value);

	/**
	 * Store the specified value in the data holder.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 * @param value value to store
	 */
	void setValue(VehicleDetailKey keyName, Entity vehicle, String value);

	/**
	 * Check if the item stacks storage contains the key.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return key exists
	 */
	boolean has(VehicleDetailKey keyName, ItemStack itemStack);

	/**
	 * Check if the data holder contains the key.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 * @return key exists
	 */
	boolean has(VehicleDetailKey keyName, Entity vehicle);

	/**
	 * Remove the key data from the data holder.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link Vehicle}
	 */
	void remove(VehicleDetailKey keyName, Entity vehicle);

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
	 * @param dataHolder {@link Vehicle}
	 */
	void printDataDetails(Player player, Entity dataHolder);

}
