package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.other.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FuelController {

    private final int MAX_FUEL;
    private final boolean USE_FUEL;

    // map of car ID with fuel level (0 - 100)
    private Map<Integer, Integer> fuelLevel = new HashMap<>();

    public FuelController() {
        USE_FUEL = Carz.getInstance().getConfig().getBoolean("Fuel.Enable");
        MAX_FUEL = Carz.getInstance().getConfig().getInt("Fuel.StartAmount");
    }

    public Integer getFuelLevel(Integer carID) {
        return fuelLevel.get(carID);
    }

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

    private String formattedFuelLevel(Integer carID) {
        StringBuilder sb = new StringBuilder();
        double fuelRemaining = getFuelLevel(carID) / MAX_FUEL;
        double fuelMissing = MAX_FUEL - fuelRemaining;

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

    public boolean isFuelConsumed(Integer carID) {
        return getFuelLevel(carID) <= 0;
    }

    public void refuel(Integer carID) {
        fuelLevel.put(carID, MAX_FUEL);
    }

    public void refuel(Integer carID, Player player) {
        refuel(carID);
        player.sendMessage(Utils.getTranslation("Refuel"));
    }

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
}
