package io.github.a5h73y.carz.event;

import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

public class CarStashEvent extends CarzEvent {

    private final Minecart minecart;

    public CarStashEvent(Player player, Minecart minecart) {
        super(player);
        this.minecart = minecart;
    }

    public Minecart getMinecart() {
        return minecart;
    }
}
