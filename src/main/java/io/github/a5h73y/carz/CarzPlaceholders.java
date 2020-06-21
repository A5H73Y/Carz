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

            case "maxfuel":
                return String.valueOf(carz.getFuelController().getMaxCapacity());

            case "cartype":
                return car.getCarType();

            default:
                break;
        }

        return null;
    }

    /**
     * Validate if the player is inside and driving a vehicle.
     *
     * @param player requested player
     * @return player is driving
     */
    public boolean playerIsDriving(Player player) {
        return player.isInsideVehicle() && carz.getCarController().isDriving(player.getName());
    }

    /**
     * Get the requested player's Car.
     *
     * @param player requested player
     * @return player's car
     */
    public Car getPlayersCar(Player player) {
        if (playerIsDriving(player)) {
            return carz.getCarController().getCar(player.getVehicle().getEntityId());
        }
        return null;
    }
}
