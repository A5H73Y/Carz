package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

/**
 * Item Meta related utility methods.
 * Uses {@link VehicleDetailKey} and {@link PersistentDataHolder} to achieve persistence of car details.
 */
public class ItemMetaUtils {

	/**
	 * Retrieve the specified item meta value stored.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return stored value
	 */
	public String getValue(VehicleDetailKey keyName, ItemStack itemStack) {
		String value = null;
		if (itemStack.hasItemMeta()) {
			value = getValue(keyName, itemStack.getItemMeta());
		}
		return value;
	}

	/**
	 * Retrieve the specified value stored in the data holder.
	 * It must be checked that a value exists before it is retrieved.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param persistentDataHolder the {@link PersistentDataHolder}
	 * @return stored value
	 */
	public String getValue(VehicleDetailKey keyName, PersistentDataHolder persistentDataHolder) {
		return persistentDataHolder.getPersistentDataContainer()
				.get(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	/**
	 * Store the specified value in the item stack.
	 *
	 * @param keyName keyName the {@link VehicleDetailKey}
	 * @param itemStack persistentDataHolder the {@link PersistentDataHolder}
	 * @param value value to store
	 */
	public ItemStack setValue(VehicleDetailKey keyName, ItemStack itemStack, String value) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.getPersistentDataContainer().set(keyName.getNamespacedKey(), PersistentDataType.STRING, value);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	/**
	 * Store the specified value in the data holder.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param persistentDataHolder the {@link PersistentDataHolder}
	 * @param value value to store
	 */
	public void setValue(VehicleDetailKey keyName, PersistentDataHolder persistentDataHolder, String value) {
		persistentDataHolder.getPersistentDataContainer()
				.set(keyName.getNamespacedKey(), PersistentDataType.STRING, value);
	}

	/**
	 * Check if the data holder contains the key.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param persistentDataHolder the {@link PersistentDataHolder}
	 * @return key exists
	 */
	public boolean has(VehicleDetailKey keyName, PersistentDataHolder persistentDataHolder) {
		return persistentDataHolder.getPersistentDataContainer()
				.has(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	/**
	 * Check if the item stacks storage contains the key.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return key exists
	 */
	public boolean has(VehicleDetailKey keyName, ItemStack itemStack) {
		return itemStack.hasItemMeta() && has(keyName, itemStack.getItemMeta());
	}

	/**
	 * Transfer the values from one data holder to another.
	 * Each available {@link VehicleDetailKey} will be checked if it exists and then transferred.
	 *
	 * @param from sending data holder
	 * @param to receiving data holder
	 * @return updated receiving data holder
	 */
	public PersistentDataHolder transferNamespaceKeyValues(PersistentDataHolder from, PersistentDataHolder to) {
		for (VehicleDetailKey value : VehicleDetailKey.values()) {
			if (has(value, from)) {
				String storedValue = getValue(value, from);
				setValue(value, to, storedValue);
			}
		}

		return to;
	}

	/**
	 * Send each summary detail to the requesting player.
	 *
	 * @param player requesting player
	 * @param dataHolder {@link PersistentDataHolder}
	 */
	public void sendDataDetails(Player player, PersistentDataHolder dataHolder) {
		for (VehicleDetailKey value : VehicleDetailKey.values()) {
			if (has(value, dataHolder)) {
				String nameSpace = value.getNamespacedKey().getKey()
						.replace("-key", "")
						.replace("-", " ");
				player.sendMessage(nameSpace + " = " + getValue(value, dataHolder));
			}
		}
	}
}
