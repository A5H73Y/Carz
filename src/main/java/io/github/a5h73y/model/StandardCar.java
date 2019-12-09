package io.github.a5h73y.model;

public class StandardCar extends Car {

	public StandardCar(int entityId, String owner, String type) {
		super(entityId, owner, type);
	}

	public StandardCar(int entityId) {
		super(entityId);
	}

	@Override
	public double getAcceleration() {
		return 1.0;
	}

	@Override
	public double getFuelEfficiency() {
		return 1.0;
	}


}
