package io.github.a5h73y.vehiclez;

import io.github.a5h73y.vehiclez.model.Car;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * Vehiclez implementation of {@link PlaceholderExpansion}.
 * Various Car related information can be retrieved through placeholders.
 */
public class VehiclezPlaceholders extends PlaceholderExpansion {

    private final Vehiclez vehiclez;

    public VehiclezPlaceholders(Vehiclez vehiclez) {
        this.vehiclez = vehiclez;
    }

    @Override
    public String getIdentifier() {
        return vehiclez.getName();
    }

    @Override
    public String getAuthor() {
        return vehiclez.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return vehiclez.getDescription().getVersion();
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
            return TranslationUtils.getTranslation("PlaceholderAPI.NoCar", false);
        }

        // car detail specific
        switch (command) {
            case "fuelgauge":
                return vehiclez.getFuelController().formattedFuelLevel(car);

            case "speed":
                return car.getCurrentSpeed().toString();

            case "fuel":
                return car.getCurrentFuel().toString();

            case "maxfuel":
                return String.valueOf(vehiclez.getFuelController().getMaxCapacity());

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
        return player.isInsideVehicle() && vehiclez.getCarController().isDriving(player.getName());
    }

    /**
     * Get the requested player's Car.
     *
     * @param player requested player
     * @return player's car
     */
    public Car getPlayersCar(Player player) {
        if (playerIsDriving(player)) {
            return vehiclez.getCarController().getCar(player.getVehicle().getEntityId());
        }
        return null;
    }
}
