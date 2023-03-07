package io.github.a5h73y.vehiclez.model;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.controllers.CarController;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;

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
		this.carDetails = Vehiclez.getInstance().getCarController().getCarTypes().get(carType);
		this.currentFuel = Vehiclez.getInstance().getFuelController().getMaxCapacity();
		this.maxSpeed = this.carDetails.getStartMaxSpeed();
	}

	/**
	 * Car Acceleration.
	 * The speed of the car will increase until it reaches its acceleration limit.
	 * The fuel will be decreased by its configured amount.
	 */
	public void accelerate() {
		// if we haven't reached the top speed
		if (this.currentSpeed < this.maxSpeed) {
			this.currentSpeed += this.carDetails.getAcceleration();

		} else if (this.currentSpeed > this.maxSpeed) {
			// we have exceeded the maxSpeed (i.e. by boosting)
			this.currentSpeed = Math.floor(Math.max(this.maxSpeed, this.currentSpeed * 0.8));
		}

		this.currentFuel -= this.carDetails.getFuelUsage();
	}

	/**
	 * Apply speed modifier to current speed.
	 * Used to slow or boost the vehicle's normal speed.
	 *
	 * @param modifier speed modifier
	 */
	public void applySpeedModifier(double modifier) {
		this.currentSpeed = this.maxSpeed * modifier;
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
		return "Entity Id: " + entityId
				+ ", \nCar Type: " + carType
				+ ", \nMax Speed: " + maxSpeed
				+ ", \nCurrent Speed: " + currentSpeed
				+ ", \nCurrent Fuel: " + currentFuel
				+ "\n" + carDetails;
	}

	/**
	 * Display a basic summary of the Car's details.
	 *
	 * @return car detail summary
	 */
	public String getSummary() {
		return TranslationUtils.getValueTranslation("CarDetails.Type", carType, false)
				+ "\n" + TranslationUtils.getValueTranslation(
						"CarDetails.MaxSpeed", String.valueOf(maxSpeed), false)
				+ "\n" + TranslationUtils.getValueTranslation(
						"CarDetails.CurrentSpeed", String.valueOf(currentSpeed), false)
				+ "\n" + TranslationUtils.getValueTranslation(
						"CarDetails.Acceleration", String.valueOf(carDetails.getAcceleration()), false)
				+ "\n" + TranslationUtils.getValueTranslation(
						"CarDetails.Fuel", String.valueOf(currentFuel), false);
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
