package io.github.a5h73y.vehiclez.enums;

import io.github.a5h73y.vehiclez.Vehiclez;
import org.bukkit.NamespacedKey;

/**
 * All Available Vehicle Detail Keys.
 * Stored against the Minecart / ItemStack for persisting information.
 * NamespacedKey was introduced in 1.12 and for this reason is the minimum supported version.
 */
public enum VehicleDetailKey {

	VEHICLE_TYPE(new NamespacedKey(Vehiclez.getInstance(), "vehicle-type-key")),
	VEHICLE_OWNER(new NamespacedKey(Vehiclez.getInstance(), "vehicle-owner-key")),
	VEHICLE_SPEED(new NamespacedKey(Vehiclez.getInstance(), "vehicle-speed-key")),
	VEHICLE_FUEL(new NamespacedKey(Vehiclez.getInstance(), "vehicle-fuel-key")),
	VEHICLE_LOCKED(new NamespacedKey(Vehiclez.getInstance(), "vehicle-locked-key"));

	private final NamespacedKey namespacedKey;

	VehicleDetailKey(NamespacedKey namespacedKey) {
		this.namespacedKey = namespacedKey;
	}

	public NamespacedKey getNamespacedKey() {
		return namespacedKey;
	}
}
