package io.github.a5h73y.vehiclez.event;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

public class CarStashEvent extends VehiclezEvent {

    private final Minecart minecart;

    public CarStashEvent(Player player, Minecart minecart) {
        super(player);
        this.minecart = minecart;
    }

    public Minecart getMinecart() {
        return minecart;
    }
}
