package io.github.a5h73y.enums;

import io.github.a5h73y.Carz;
import org.bukkit.NamespacedKey;

public enum VehicleDetailKey {

	VEHICLE_TYPE(new NamespacedKey(Carz.getInstance(), "vehicle-type-key")),
	VEHICLE_OWNER(new NamespacedKey(Carz.getInstance(), "vehicle-owner-key")),
	VEHICLE_SPEED(new NamespacedKey(Carz.getInstance(), "vehicle-speed-key"));

	private NamespacedKey namespacedKey;

	VehicleDetailKey(NamespacedKey namespacedKey) {
		this.namespacedKey = namespacedKey;
	}

	public NamespacedKey getNamespacedKey() {
		return namespacedKey;
	}
}
