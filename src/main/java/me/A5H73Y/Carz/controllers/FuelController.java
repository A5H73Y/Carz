package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.other.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FuelController {

    private final double MAX_FUEL;
    private final boolean USE_FUEL;
    private final int GAUGE_SCALE;

    // map of car ID with fuel level (0 - 100)
    private final Map<Integer, Integer> fuelLevel = new HashMap<>();

    public FuelController() {
        USE_FUEL = Carz.getInstance().getConfig().getBoolean("Fuel.Enable");
        MAX_FUEL = Carz.getInstance().getConfig().getDouble("Fuel.StartAmount");
        GAUGE_SCALE = Carz.getInstance().getConfig().getInt("Fuel.GaugeScale");
    }

    public boolean isFuelEnabled() {
        return USE_FUEL;
    }

    public Integer getFuelLevel(Integer carID) {
        Integer amount = fuelLevel.get(carID);

        // if the car has not been started, the amount will not exist, so we can fill it here
        if (amount == null) {
            refuel(carID);
            amount = fuelLevel.get(carID);
        }

        return amount;
    }

    /**
     * Display the formatted fuel level of the car to the player
     * If fuel is disabled, or the player is not inside a vehicle an error will appear instead
     * @param player
     */
    public void displayFuelLevel(Player player) {
        if (!USE_FUEL) {
            player.sendMessage(Utils.getTranslation("Error.FuelDisabled"));
            return;
        }

        if (!player.isInsideVehicle()) {
            player.sendMessage(Utils.getTranslation("Error.NotInCar"));
            return;
        }

        player.sendMessage(formattedFuelLevel(player.getVehicle().getEntityId()));
    }

    public boolean isFuelConsumed(Integer carID) {
        return getFuelLevel(carID) <= 0;
    }

    public void refuel(Integer carID) {
        fuelLevel.put(carID, (int) MAX_FUEL);
    }

    public void refuel(Integer carID, Player player) {
        refuel(carID);
        player.sendMessage(Utils.getTranslation("Refuel"));
    }

    /**
     * If fuel is enabled then reduce the amount of fuel available
     * This will be run from the VehicleUpdateEvent
     * @param carID
     */
    public void decreaseFuel(Integer carID) {
        if (USE_FUEL && !isFuelConsumed(carID))
            fuelLevel.put(carID, fuelLevel.get(carID) - 1);
    }

    public void registerCar(Integer carID) {
        refuel(carID);
    }

    public void deregisterCar(Integer carID) {
        fuelLevel.remove(carID);
    }

    /**
     * Build a formatted string of the fuel gauge, to resemble how much fuel is remaining
     * @param carID
     * @return
     */
    private String formattedFuelLevel(Integer carID) {
        StringBuilder sb = new StringBuilder();
        double fuelRemaining = Math.floor((getFuelLevel(carID) / MAX_FUEL) * GAUGE_SCALE);
        double fuelMissing = GAUGE_SCALE - fuelRemaining;

        sb.append(ChatColor.RED + "E ");
        sb.append(ChatColor.WHITE);

        for (int i=0; i < fuelRemaining; i++)
            sb.append("|");

        sb.append(ChatColor.GRAY);

        for (int i=0; i < fuelMissing; i++)
            sb.append("|");

        sb.append(ChatColor.GREEN + " F");
        return sb.toString();
    }
}
