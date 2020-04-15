package io.github.a5h73y.carz.model;

import org.bukkit.Material;

public class CarDetails {

	private double startMaxSpeed = 1.0;

	private double maxUpgradeSpeed = 5.0;

	private double acceleration = 1.0;

	private double fuelUsage = 1.0;

	private Material fillMaterial = Material.AIR;

	public CarDetails(double startMaxSpeed, double maxUpgradeSpeed, double acceleration, double fuelUsage, String fillMaterial) {
		if (startMaxSpeed > 0.0) {
			this.startMaxSpeed = startMaxSpeed;
		}
		if (maxUpgradeSpeed > 0.0) {
			this.maxUpgradeSpeed = maxUpgradeSpeed;
		}
		if (acceleration > 0.0) {
			this.acceleration = acceleration;
		}
		if (fuelUsage > 0.0) {
			this.fuelUsage = fuelUsage;
		}
		Material material = Material.getMaterial(fillMaterial);
		if (material != null) {
			this.fillMaterial = material;
		}
	}

	public double getStartMaxSpeed() {
		return startMaxSpeed;
	}

	public double getMaxUpgradeSpeed() {
		return maxUpgradeSpeed;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public double getFuelUsage() {
		return fuelUsage;
	}

	public Material getFillMaterial() {
		return fillMaterial;
	}

	@Override
	public String toString() {
		return "\nstartMaxSpeed = " + startMaxSpeed +
				", \nmaxUpgradeSpeed = " + maxUpgradeSpeed +
				", \nacceleration = " + acceleration +
				", \nfuelUsage = " + fuelUsage +
				", \nfillMaterial = " + fillMaterial;
	}
}
