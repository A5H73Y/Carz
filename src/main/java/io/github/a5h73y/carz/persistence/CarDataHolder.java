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
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return stored value
	 */
	@Override
	public String getValue(VehicleDetailKey detailKey, ItemStack itemStack) {
		return getContainerValue(detailKey, itemStack.getItemMeta());
	}

	/**
	 * Retrieve the specified value stored in the data holder.
	 * It must be checked that a value exists before it is retrieved.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link PersistentDataHolder}
	 * @return stored value
	 */
	@Override
	public String getValue(VehicleDetailKey detailKey, Entity vehicle) {
		return getContainerValue(detailKey, vehicle);
	}

	private String getContainerValue(VehicleDetailKey detailKey, PersistentDataHolder holder) {
		return holder.getPersistentDataContainer().get(detailKey.getNamespacedKey(), PersistentDataType.STRING);
	}

	/**
	 * Store the specified value in the item stack.
	 *
	 * @param detailKey detailKey the {@link VehicleDetailKey}
	 * @param itemStack persistentDataHolder the {@link PersistentDataHolder}
	 * @param value value to store
	 */
	@Override
	public void setValue(VehicleDetailKey detailKey, ItemStack itemStack, String value) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		setContainerValue(detailKey, itemMeta, value);
		itemStack.setItemMeta(itemMeta);
	}

	/**
	 * Store the specified value in the data holder.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link PersistentDataHolder}
	 * @param value value to store
	 */
	@Override
	public void setValue(VehicleDetailKey detailKey, Entity vehicle, String value) {
		setContainerValue(detailKey, vehicle, value);
	}

	private void setContainerValue(VehicleDetailKey detailKey, PersistentDataHolder holder, String value) {
		holder.getPersistentDataContainer().set(detailKey.getNamespacedKey(), PersistentDataType.STRING, value);
	}

	/**
	 * Check if the item stacks storage contains the key.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param itemStack the {@link ItemStack}
	 * @return key exists
	 */
	@Override
	public boolean has(VehicleDetailKey detailKey, ItemStack itemStack) {
		return hasContainerValue(detailKey, itemStack.getItemMeta());
	}

	/**
	 * Check if the data holder contains the key.
	 *
	 * @param detailKey the {@link VehicleDetailKey}
	 * @param vehicle the {@link PersistentDataHolder}
	 * @return key exists
	 */
	@Override
	public boolean has(VehicleDetailKey detailKey, Entity vehicle) {
		return hasContainerValue(detailKey, vehicle);
	}

	private boolean hasContainerValue(VehicleDetailKey detailKey, PersistentDataHolder holder) {
		return holder.getPersistentDataContainer().has(detailKey.getNamespacedKey(), PersistentDataType.STRING);
	}

	@Override
	public void remove(VehicleDetailKey detailKey, Entity vehicle) {
		vehicle.getPersistentDataContainer().remove(detailKey.getNamespacedKey());
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

	@Override
	public void printDataDetails(Player player, ItemStack itemStack) {
		for (VehicleDetailKey value : VehicleDetailKey.values()) {
			if (has(value, itemStack)) {
				String nameSpace = value.getNamespacedKey().getKey()
						.replace("-key", "")
						.replace("-", " ");
				player.sendMessage(nameSpace + " = " + getValue(value, itemStack));
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
