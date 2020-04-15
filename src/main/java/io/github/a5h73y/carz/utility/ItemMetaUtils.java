package io.github.a5h73y.carz.utility;

import io.github.a5h73y.carz.enums.VehicleDetailKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class ItemMetaUtils {

	public String getValue(VehicleDetailKey keyName, ItemStack itemStack) {
		String value = null;
		if (itemStack.hasItemMeta()) {
			value = itemStack.getItemMeta().getPersistentDataContainer().get(keyName.getNamespacedKey(), PersistentDataType.STRING);
		}
		return value;
	}

	public String getValue(VehicleDetailKey keyName, PersistentDataHolder persistentDataHolder) {
		return persistentDataHolder.getPersistentDataContainer().get(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	public ItemStack setValue(VehicleDetailKey keyName, ItemStack itemStack, String value) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.getPersistentDataContainer().set(keyName.getNamespacedKey(), PersistentDataType.STRING, value);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public void setValue(VehicleDetailKey keyName, PersistentDataHolder persistentDataHolder, String value) {
		persistentDataHolder.getPersistentDataContainer().set(keyName.getNamespacedKey(), PersistentDataType.STRING, value);
	}

	public boolean has(VehicleDetailKey keyName, ItemStack itemStack) {
		return itemStack.hasItemMeta()
				&& itemStack.getItemMeta().getPersistentDataContainer().has(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	public boolean has(VehicleDetailKey keyName, PersistentDataHolder persistentDataHolder) {
		return persistentDataHolder.getPersistentDataContainer().has(keyName.getNamespacedKey(), PersistentDataType.STRING);
	}

	public PersistentDataHolder transferNamespaceKeyValues(PersistentDataHolder from, PersistentDataHolder to) {
		for (VehicleDetailKey value : VehicleDetailKey.values()) {
			if (has(value, from)) {
				String storedValue = getValue(value, from);
				setValue(value, to, storedValue);

				System.out.println("transferred " + value + ": " + storedValue);
			}
		}

		return to;
	}

}
