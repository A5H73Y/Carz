package io.github.a5h73y.carz.model;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.controllers.CarController;

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

	/**
	 * Create a Car Model.
	 * The vehicle entity id must be passed, car type will be "default" if null.
	 *
	 * @param entityId vehicle entity id
	 * @param carType type of car
	 */
	public Car(final int entityId, final String carType) {
		this.entityId = entityId;
		this.currentSpeed = 0.0;
		this.carType = carType != null ? carType : CarController.DEFAULT_CAR;
		this.carDetails = Carz.getInstance().getCarController().getCarTypes().get(carType);
		this.currentFuel = Carz.getInstance().getFuelController().getMaxCapacity();
		this.maxSpeed = this.carDetails.getStartMaxSpeed();
	}

	/**
	 * Car Acceleration.
	 * The speed of the car will increase until it reaches its acceleration limit.
	 * The fuel will be decreased by its configured amount.
	 */
	public void accelerate() {
		if (this.currentSpeed < this.maxSpeed) {
			this.currentSpeed += this.carDetails.getAcceleration();
		}
		this.currentFuel -= this.carDetails.getFuelUsage();
	}

	/**
	 * Determine if the car's fuel has been consumed.
	 *
	 * @return fuel consumed
	 */
	public boolean isFuelConsumed() {
		return getCurrentFuel() <= 0;
	}

	/**
	 * Reset the speed of the Car.
	 */
	public void resetSpeed() {
		this.currentSpeed = 0.0;
	}

	@Override
	public String toString() {
		return "entityId = " + entityId
				+ ", \ncarType = " + carType
				+ ", \nmaxSpeed = " + maxSpeed
				+ ", \ncurrentSpeed = " + currentSpeed
				+ ", \ncurrentFuel = " + currentFuel
				+ "\n" + carDetails;
	}

	public int getEntityId() {
		return entityId;
	}

	public String getCarType() {
		return carType;
	}

	public Double getCurrentSpeed() {
		return currentSpeed;
	}

	public CarDetails getCarDetails() {
		return carDetails;
	}

	public Double getCurrentFuel() {
		return currentFuel;
	}

	public void setCurrentFuel(double fuelAmount) {
		this.currentFuel = fuelAmount;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}
