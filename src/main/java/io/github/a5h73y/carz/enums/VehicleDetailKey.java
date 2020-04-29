package io.github.a5h73y.carz.enums;

import io.github.a5h73y.carz.Carz;
import org.bukkit.NamespacedKey;

/**
 * All Available Vehicle Detail Keys.
 * Stored against the Minecart / ItemStack for persisting information.
 */
public enum VehicleDetailKey {

	VEHICLE_TYPE(new NamespacedKey(Carz.getInstance(), "vehicle-type-key")),
	VEHICLE_OWNER(new NamespacedKey(Carz.getInstance(), "vehicle-owner-key")),
	VEHICLE_SPEED(new NamespacedKey(Carz.getInstance(), "vehicle-speed-key")),
	VEHICLE_FUEL(new NamespacedKey(Carz.getInstance(), "vehicle-fuel-key"));

	private final NamespacedKey namespacedKey;

	VehicleDetailKey(NamespacedKey namespacedKey) {
		this.namespacedKey = namespacedKey;
	}

	public NamespacedKey getNamespacedKey() {
		return namespacedKey;
	}
}
