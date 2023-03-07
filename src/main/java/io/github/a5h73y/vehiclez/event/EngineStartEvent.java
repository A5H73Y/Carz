package io.github.a5h73y.vehiclez.event;

import io.github.a5h73y.vehiclez.model.Car;
import org.bukkit.entity.Player;

public class EngineStartEvent extends VehiclezEvent {

    private final Car car;

    public EngineStartEvent(Player player, Car car) {
        super(player);
        this.car = car;
    }

    public Car getCar() {
        return car;
    }
}
