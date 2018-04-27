package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import org.bukkit.entity.Vehicle;

import java.util.*;

public class CarController {

    private final Carz carz;

    // Players currently inside a Carz vehicle, not necessarily the owner
    private final Set<String> playersDriving = new HashSet<>();

    // Vehicle ID with it's corresponding Owner's name
    private Map<Integer, String> ownership = new HashMap<>();

    // Control to handle the car's speed upgrades
    private UpgradeController upgradeController = new UpgradeController();

    public CarController(Carz carz) {
        this.carz = carz;
    }

    public UpgradeController getUpgradeController() {
        return upgradeController;
    }

    public void addDriver(String playerName, Integer carId) {
        playersDriving.add(playerName);
        registerNewCar(carId);
    }

    public void removeDriver(String playerName) {
        playersDriving.remove(playerName);
    }

    public boolean isDriving(String playerName) {
        return playersDriving.contains(playerName);
    }

    /**
     * Register a new car
     * Set the default car speed, and start the fuel management
     * @param carID
     */
    public void registerNewCar(Integer carID) {
        if (upgradeController.isCarRegistered(carID))
            return;

        upgradeController.setDefaultCarSpeed(carID);
        carz.getFuelController().registerCar(carID);
    }

    /**
     * Completely remove a car
     * Eject a player, remove the ownership and fuel management
     * @param car
     */
    public void destroyCar(Vehicle car) {
        car.eject();
        car.remove();
        removeOwnership(car.getEntityId());
        removeDriver(car.getPassenger().getName());
        carz.getFuelController().deregisterCar(car.getEntityId());
    }

    // --- Ownership methods ---
    public String getOwner(Integer carID) {
        return ownership.get(carID);
    }

    public boolean isCarOwned(Integer carID) {
        return ownership.containsKey(carID);
    }

    public boolean isCarOwnedByPlayer(Integer carID, String playerName) {
        return isCarOwned(carID) && ownership.get(carID).equals(playerName);

    }

    public void declareOwnership(Integer carID, String playerName) {
        ownership.put(carID, playerName);
    }

    public void removeOwnership(Integer carID) {
        ownership.remove(carID);
    }
}
