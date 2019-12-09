package io.github.a5h73y.model;

import io.github.a5h73y.Carz;

public abstract class Car {

	/**
	 car:
		 types:
			 default:
				 speed: 1.0
	             acceleration: 1.0
				 fuelUsage: 1.0
				 fillMaterial: AIR
			 sport:
				 speed: 2.0
	             acceleration: 2.0
				 fuelUsage: 2.0
				 fillMaterial: WET_SPONGE
			 tractor:
				 speed: 0.5
	             acceleration: 0.1
				 fuelUsage: 1.5
	             fillMaterial: STONE
	 **/


	private int entityId;

	private String owner;

	private Double speed;

	private Double maxSpeed;

	private String type;

	private Double fuel;

	private Car() {

	}

	public Car(final int entityId) {
		this(entityId, null, null);
	}

	public Car(final int entityId, final String owner) {
		this(entityId, owner, null);
	}

	public Car(final int entityId, final String owner, final String type) {
		this.entityId = entityId;
		this.owner = owner;
		this.type = type;
		this.speed = 0.0;
		this.maxSpeed = Carz.getInstance().getSettings().getStartSpeed();
		this.fuel = Carz.getInstance().getFuelController().getStartAmount();
	}

	public abstract double getAcceleration();

	public abstract double getFuelEfficiency();

	public void accelerate() {
		if (this.speed < this.maxSpeed) {
			this.speed += getAcceleration();
		}
		this.fuel -= getFuelEfficiency();
	}

	public boolean isFuelConsumed() {
		return getFuel() <= 0;
	}

	public void resetSpeed() {
		this.speed = 0.0;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Double getSpeed() {
		return speed;
	}

	public Double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getFuel() {
		return fuel;
	}

	public void setFuel(Double fuel) {
		this.fuel = fuel;
	}
}
