package io.github.a5h73y.carz;

import io.github.a5h73y.carz.model.Car;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * Carz implementation of {@link PlaceholderExpansion}.
 * Various Car related information can be retrieved through placeholders.
 */
public class CarzPlaceholders extends PlaceholderExpansion {

    private final Carz carz;

    public CarzPlaceholders(Carz carz) {
        this.carz = carz;
    }

    @Override
    public String getIdentifier() {
        return carz.getName();
    }

    @Override
    public String getAuthor() {
        return carz.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return carz.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String message) {
        String command = message.toLowerCase();

        Car car = getPlayersCar(player);

        if (car == null) {
            return "";
        }

        // car detail specific
        switch (command) {
            case "fuelgauge":
                return carz.getFuelController().formattedFuelLevel(car);

            case "speed":
                return car.getCurrentSpeed().toString();

            case "fuel":
                return car.getCurrentFuel().toString();

            case "cartype":
                return car.getCarType();
        }

        return null;
    }

    public boolean playerIsDriving(Player player) {
        return player.isInsideVehicle() && carz.getCarController().isDriving(player.getName());
    }

    public Car getPlayersCar(Player player) {
        if (playerIsDriving(player)) {
            return carz.getCarController().getCar(player.getVehicle().getEntityId());
        }
        return null;
    }
}
