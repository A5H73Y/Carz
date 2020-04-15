package io.github.a5h73y.carz.controllers;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

/**
 * Fuel related functionality.
 */
public class FuelController extends AbstractPluginReceiver {

    private final double MAX_FUEL;
    private final boolean USE_FUEL;
    private final int GAUGE_SCALE;

    /**
     * Initialise the global fuel variables.
     * @param carz
     */
    public FuelController(final Carz carz) {
        super(carz);
        USE_FUEL = carz.getConfig().getBoolean("Fuel.Enabled");
        MAX_FUEL = carz.getConfig().getDouble("Fuel.StartAmount");
        GAUGE_SCALE = carz.getConfig().getInt("Fuel.GaugeScale");
    }

    public boolean isFuelEnabled() {
        return USE_FUEL;
    }

    public double getStartAmount() {
        return MAX_FUEL;
    }

    /**
     * Display the formatted fuel level of the Car.
     * If fuel is disabled, or the player is not inside a vehicle an error will appear instead.
     * @param player
     */
    public void displayFuelLevel(Player player) {
        if (!USE_FUEL) {
            TranslationUtils.sendTranslation("Error.FuelDisabled", player);
            return;
        }

        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
            TranslationUtils.sendTranslation("Error.NotInCar", player);
            return;
        }

        Car car = carz.getCarController().getCar(player.getVehicle().getEntityId());
        if (car != null) {
            Carz.getInstance().getBountifulAPI().sendActionBar(player, formattedFuelLevel(car));
        }
    }

    public void refuel(Car car) {
        car.setCurrentFuel(MAX_FUEL);
    }

    /**
     * Build a formatted string of the fuel gauge, to resemble how much fuel is remaining
     * @param car
     * @return Formatted fuel gauge: E |||||| F
     */
    private String formattedFuelLevel(Car car) {
        StringBuilder sb = new StringBuilder();
        double fuelRemaining = Math.floor((car.getCurrentFuel() / MAX_FUEL) * GAUGE_SCALE);
        double fuelMissing = GAUGE_SCALE - fuelRemaining;

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
     * @param remaining remaining fuel amount
     * @return cost multiplier
     */
    public double determineScaleOfCostMultiplier(double remaining) {
        double percentRemaining = remaining / MAX_FUEL;
        return 1 - (Math.floor(percentRemaining * 4) / 4);
    }
}
