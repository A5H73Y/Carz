package io.github.a5h73y.model;

import io.github.a5h73y.Carz;
import io.github.a5h73y.controllers.CarController;

public class Car {

	private int entityId;

	private String owner;

	private CarDetails carDetails;

	private double maxSpeed;

	private double currentSpeed;

	private double currentFuel;

	public Car(final int entityId) {
		this(entityId, null, null);
	}

	public Car(final int entityId, final String carType) {
		this(entityId, carType, null);
	}

	public Car(final int entityId, final String carType, final String owner) {
		this.entityId = entityId;
		this.owner = owner;
		this.currentSpeed = 0.0;

		this.carDetails = Carz.getInstance().getCarController().getCarTypes().get(
				carType == null ? CarController.DEFAULT_CAR : carType);
		this.currentFuel = Carz.getInstance().getFuelController().getStartAmount();
		this.maxSpeed = this.carDetails.getStartMaxSpeed();
	}

	public void accelerate() {
		if (this.currentSpeed < this.carDetails.getStartMaxSpeed()) {
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
		return "Car{" +
				"entityId=" + entityId +
				", owner='" + owner + '\'' +
				", carDetails=" + carDetails +
				", maxSpeed=" + maxSpeed +
				", currentSpeed=" + currentSpeed +
				", currentFuel=" + currentFuel +
				'}';
	}

	public int getEntityId() {
		return entityId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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
