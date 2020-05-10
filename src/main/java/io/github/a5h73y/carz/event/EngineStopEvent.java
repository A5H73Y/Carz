package io.github.a5h73y.carz.event;

import io.github.a5h73y.carz.model.Car;
import org.bukkit.entity.Player;

public class EngineStopEvent extends CarzEvent {

    private final Car car;

    public EngineStopEvent(Player player, Car car) {
        super(player);
        this.car = car;
    }

    public Car getCar() {
        return car;
    }
}
