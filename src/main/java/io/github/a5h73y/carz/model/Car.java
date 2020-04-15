package io.github.a5h73y.carz.model;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.controllers.CarController;
import io.github.a5h73y.carz.utility.StringUtils;

public class Car {

	private final int entityId;

	private final String carType;

	private final CarDetails carDetails;

	private double maxSpeed;

	private double currentSpeed;

	private double currentFuel;

	public Car(final int entityId) {
		this(entityId, null);
	}

	public Car(final int entityId, final String carType) {
		this.entityId = entityId;
		this.currentSpeed = 0.0;
		this.carType = carType != null ? carType : CarController.DEFAULT_CAR;
		this.carDetails = Carz.getInstance().getCarController().getCarTypes().get(carType);
		this.currentFuel = Carz.getInstance().getFuelController().getStartAmount();
		this.maxSpeed = this.carDetails.getStartMaxSpeed();
	}

	public void accelerate() {
		if (this.currentSpeed < this.maxSpeed) {
			this.currentSpeed += this.carDetails.getAcceleration();
		}
		this.currentFuel -= this.carDetails.getFuelUsage();
	}

	public void setCurrentFuel(double fuelAmount) {
		this.currentFuel = fuelAmount;
	}

	public boolean isFuelConsumed() {
		return getCurrentFuel() <= 0;
	}

	public void resetSpeed() {
		this.currentSpeed = 0.0;
	}

	@Override
	public String toString() {
		return StringUtils.getStandardHeading("Car Details") +
				"\nentityId = " + entityId +
				", \ncarType = " + carType +
				", \nmaxSpeed = " + maxSpeed +
				", \ncurrentSpeed = " + currentSpeed +
				", \ncurrentFuel = " + currentFuel +
				"\n" + carDetails;
	}

	public int getEntityId() {
		return entityId;
	}

	public String getCarType() {
		return carType;
	}

	public Double getCurrentFuel() {
		return currentFuel;
	}

	public Double getCurrentSpeed() {
		return currentSpeed;
	}

	public CarDetails getCarDetails() {
		return carDetails;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}
