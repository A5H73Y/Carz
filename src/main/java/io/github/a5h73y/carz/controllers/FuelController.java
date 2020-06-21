package io.github.a5h73y.carz.controllers;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

/**
 * All fuel related functionality.
 */
public class FuelController extends AbstractPluginReceiver {

    private final boolean useFuel;
    private final double maxCapacity;
    private final int gaugeScale;

    /**
     * Initialise the global fuel variables.
     *
     * @param carz plugin instance
     */
    public FuelController(final Carz carz) {
        super(carz);
        useFuel = carz.getConfig().getBoolean("Fuel.Enabled");
        maxCapacity = carz.getConfig().getDouble("Fuel.MaxCapacity");
        gaugeScale = carz.getConfig().getInt("Fuel.GaugeScale");
    }

    /**
     * Check if the Fuel System enabled.
     *
     * @return fuel enabled
     */
    public boolean isFuelEnabled() {
        return useFuel;
    }

    /**
     * Maximum / Initial capacity for the cars.
     * @return maximum capacity
     */
    public double getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Display the formatted fuel level of the Car.
     * If fuel is disabled, or the player is not inside a vehicle an error will appear instead.
     *
     * @param player requesting player
     */
    public void displayFuelLevel(Player player) {
        if (!useFuel) {
            TranslationUtils.sendTranslation("Error.FuelDisabled", player);
            return;
        }

        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
            TranslationUtils.sendTranslation("Error.NotInCar", player);
            return;
        }

        Car car = carz.getCarController().getCar(player.getVehicle().getEntityId());
        if (car != null) {
            carz.getBountifulApi().sendActionBar(player, formattedFuelLevel(car));
        }
    }

    /**
     * Refuel the car to max capacity.
     *
     * @param car requesting car
     */
    public void refuel(Car car) {
        car.setCurrentFuel(maxCapacity);
    }

    /**
     * Build a formatted string of the fuel gauge, to resemble how much fuel is remaining.
     *
     * @param car requesting car
     * @return Formatted fuel gauge: E |||||| F
     */
    public String formattedFuelLevel(Car car) {
        StringBuilder sb = new StringBuilder();
        final double fuelRemaining = Math.floor((car.getCurrentFuel() / maxCapacity) * gaugeScale);
        final double fuelMissing = gaugeScale - fuelRemaining;

        sb.append(ChatColor.RED);
        sb.append("E ");
        sb.append(ChatColor.WHITE);

        for (int i = 0; i < fuelRemaining; i++) {
            sb.append("|");
        }

        sb.append(ChatColor.GRAY);

        for (int i = 0; i < fuelMissing; i++) {
            sb.append("|");
        }

        sb.append(ChatColor.GREEN);
        sb.append(" F");
        return sb.toString();
    }

    /**
     * Calculate the cost multiplier based on remaining fuel.
     * Scenarios:
     *  0 / 3000 fuel left = 100% of cost refuel
     *  1500 / 3000 fuel left = 50% of cost refuel
     *  2999 / 3000 fuel left = 25% of cost refuel (to avoid exploit)
     *
     * @param remaining remaining fuel amount
     * @return cost multiplier
     */
    public double determineScaleOfCostMultiplier(double remaining) {
        double percentRemaining = remaining / maxCapacity;
        return 1 - (Math.floor(percentRemaining * 4) / 4);
    }
}
