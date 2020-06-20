package io.github.a5h73y.carz.persistence;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

/**
 * Item Meta related utility methods.
 * Uses {@link VehicleDetailKey} and {@link PersistentDataHolder} to achieve persistence of car details.
 */
public class CarDataHolder implements CarDataPersistence {

	/**
	 * Retrieve the specified item meta value stored.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return stored value
	 */
	@Override
	public String getValue(VehicleDetailKey keyName, ItemStack itemStack) {
		String value = null;
		if (itemStack.hasItemMeta()) {
			value = getContainerValue(keyName, itemStack.getItemMeta());
		}
		return value;
	}

	/**
	 * Retrieve the specified value stored in the data holder.
	 * It must be checked that a value exists before it is retrieved.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link PersistentDataHolder}
	 * @return stored value
	 */
	@Override
	public String getValue(VehicleDetailKey keyName, Entity vehicle) {
		return getContainerValue(keyName, vehicle);
	}

	private String getContainerValue(VehicleDetailKey keyName, PersistentDataHolder holder) {
		return holder.getPersistentDataContainer().get(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	/**
	 * Store the specified value in the item stack.
	 *
	 * @param keyName keyName the {@link VehicleDetailKey}
	 * @param itemStack persistentDataHolder the {@link PersistentDataHolder}
	 * @param value value to store
	 */
	@Override
	public ItemStack setValue(VehicleDetailKey keyName, ItemStack itemStack, String value) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		setContainerValue(keyName, itemMeta, value);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	/**
	 * Store the specified value in the data holder.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link PersistentDataHolder}
	 * @param value value to store
	 */
	@Override
	public void setValue(VehicleDetailKey keyName, Entity vehicle, String value) {
		setContainerValue(keyName, vehicle, value);
	}

	private void setContainerValue(VehicleDetailKey keyName, PersistentDataHolder holder, String value) {
		holder.getPersistentDataContainer().set(keyName.getNamespacedKey(), PersistentDataType.STRING, value);
	}

	/**
	 * Check if the item stacks storage contains the key.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return key exists
	 */
	@Override
	public boolean has(VehicleDetailKey keyName, ItemStack itemStack) {
		return itemStack.hasItemMeta() && hasContainerValue(keyName, itemStack.getItemMeta());
	}

	/**
	 * Check if the data holder contains the key.
	 *
	 * @param keyName the {@link VehicleDetailKey}
	 * @param vehicle the {@link PersistentDataHolder}
	 * @return key exists
	 */
	@Override
	public boolean has(VehicleDetailKey keyName, Entity vehicle) {
		return hasContainerValue(keyName, vehicle);
	}

	private boolean hasContainerValue(VehicleDetailKey keyName, PersistentDataHolder holder) {
		return holder.getPersistentDataContainer().has(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	@Override
	public void remove(VehicleDetailKey keyName, Entity vehicle) {
		vehicle.getPersistentDataContainer().remove(keyName.getNamespacedKey());
	}

	@Override
	public void transferNamespaceKeyValues(Entity from, ItemStack to) {
		ItemMeta itemMeta = to.getItemMeta();
		this.transferPersistentDataHolder(from, itemMeta);
		to.setItemMeta(itemMeta);
	}

	@Override
	public void transferNamespaceKeyValues(ItemStack from, Entity to) {
		this.transferPersistentDataHolder(from.getItemMeta(), to);
	}

	@Override
	public void printDataDetails(Player player, Entity dataHolder) {
		for (VehicleDetailKey value : VehicleDetailKey.values()) {
			if (has(value, dataHolder)) {
				String nameSpace = value.getNamespacedKey().getKey()
						.replace("-key", "")
						.replace("-", " ");
				player.sendMessage(nameSpace + " = " + getValue(value, dataHolder));
			}
		}
	}

	private void transferPersistentDataHolder(PersistentDataHolder from, PersistentDataHolder to) {
		for (VehicleDetailKey value : VehicleDetailKey.values()) {
			if (hasContainerValue(value, from)) {
				String storedValue = getContainerValue(value, from);
				setContainerValue(value, to, storedValue);
			}
		}
	}
}
