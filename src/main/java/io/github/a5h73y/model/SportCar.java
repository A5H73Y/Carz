package io.github.a5h73y.model;

public class SportCar extends Car {

	public SportCar(int entityId, String owner, String type) {
		super(entityId, owner, type);
	}

	public SportCar(int entityId) {
		super(entityId);
	}

	@Override
	public Double getMaxSpeed() {
		return super.getMaxSpeed() + 50.0;
	}

	@Override
	public double getAcceleration() {
		return 2.0;
	}

	@Override
	public double getFuelEfficiency() {
		return 2.0;
	}
}
