package io.github.a5h73y.carz.model;

import org.bukkit.Material;

public class CarDetails {

	private final double startMaxSpeed;

	private final double maxUpgradeSpeed;

	private final double acceleration;

	private final double fuelUsage;

	private Material fillMaterial = Material.AIR;

	/**
	 * Car Details model.
	 * Populate the car details, with a validation to ensure the numbers are positive.
	 *
	 * @param startMaxSpeed initial maximum speed of the car
	 * @param maxUpgradeSpeed absolute maximum speed of the car
	 * @param acceleration acceleration speed of the car
	 * @param fuelUsage amount of fuel used during acceleration
	 * @param fillMaterial material to place inside the Minecart
	 */
	public CarDetails(double startMaxSpeed, double maxUpgradeSpeed, double acceleration,
	                  double fuelUsage, String fillMaterial) {

		this.startMaxSpeed = Math.max(0.0, startMaxSpeed);
		this.maxUpgradeSpeed = Math.max(0.0, maxUpgradeSpeed);
		this.acceleration = Math.max(0.0, acceleration);
		this.fuelUsage = Math.max(0.0, fuelUsage);

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
		return "\nstartMaxSpeed = " + startMaxSpeed
				+ ", \nmaxUpgradeSpeed = " + maxUpgradeSpeed
				+ ", \nacceleration = " + acceleration
				+ ", \nfuelUsage = " + fuelUsage
				+ ", \nfillMaterial = " + fillMaterial;
	}
}
