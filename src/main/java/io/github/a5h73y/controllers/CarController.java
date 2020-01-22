package io.github.a5h73y.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.model.CarDetails;
import io.github.a5h73y.other.Utils;
import io.github.a5h73y.utility.EffectUtils;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.Effect;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

/**
 * All Car related functionality.
 */
public class CarController {

    public static final String DEFAULT_CAR = "Default";

    private final Carz carz;

    // Vehicle ID with it's associated Car
    private final Map<Integer, Car> entityIdToCar = new HashMap<>();

    // Players currently inside a Carz vehicle, with the value being Vehicle ID
    private final Map<String, Integer> playersDriving = new HashMap<>();

    private final Map<String, CarDetails> carTypes = new HashMap<>();

    public CarController(Carz carz) {
        this.carz = carz;
        populateCarTypes();
    }

    private void populateCarTypes() {
        Set<String> allCarTypes = carz.getConfig().getConfigurationSection("CarTypes").getKeys(false);

        for (String carType : allCarTypes) {
            double startSpeed = carz.getConfig().getDouble("CarTypes." + carType + ".StartMaxSpeed");
            double maxSpeed = carz.getConfig().getDouble("CarTypes." + carType + ".MaxUpgradeSpeed");
            double acceleration = carz.getConfig().getDouble("CarTypes." + carType + ".Acceleration");
            double fuelUsage = carz.getConfig().getDouble("CarTypes." + carType + ".FuelUsage");
            String fillMaterial = carz.getConfig().getString("CarTypes." + carType + ".FillMaterial");
            carTypes.put(carType, new CarDetails(startSpeed, maxSpeed, acceleration, fuelUsage, fillMaterial));
        }
    }

    /**
     * Register the player as the driving for the Car.
     * @param playerName player driving
     * @param carId entity id for the vehicle
     * @param owner is the player the registered owner?
     */
    public void startDriving(String playerName, Integer carId, boolean owner) {
        Car car = getOrCreateCar(carId, DEFAULT_CAR);
        if (owner) {
            car.setOwner(playerName);
        }
        playersDriving.put(playerName, car.getEntityId());
    }

    public void startDriving(String playerName, Integer carId) {
        this.startDriving(playerName, carId, false);
    }

    /**
     * Get the matching known Car.
     * Otherwise, create a new entry for the car.
     * @param entityId
     * @param carType
     * @return matching or new Car
     */
    public Car getOrCreateCar(Integer entityId, String carType) {
        return entityIdToCar.computeIfAbsent(entityId, k -> new Car(entityId, carType));
    }

    /**
     * Register a new car.
     * Set the default car speed, and start the fuel management
     * @param entityId
     */
    public Car getOrCreateCar(Integer entityId) {
        return getOrCreateCar(entityId, DEFAULT_CAR);
    }

    /**
     * Player has stopped driving.
     * @param playerName
     */
    public void removeDriver(String playerName) {
        playersDriving.remove(playerName);
    }

    /**
     * Is the player currently driving a Car.
     * @param playerName
     * @return player driving?
     */
    public boolean isDriving(String playerName) {
        return playersDriving.containsKey(playerName);
    }

    /**
     * Completely remove a vehicle.
     * Eject a player, remove the ownership and fuel management
     * @param vehicle
     */
    public void destroyCar(Vehicle vehicle) {
        entityIdToCar.remove(vehicle.getEntityId());
        //TODO improve this (1.7)
        tryAndRemovePlayerFromCar(vehicle);
        EffectUtils.createDamageEffect(vehicle);
        vehicle.eject();
        vehicle.remove();
    }

    /**
     * Attempt to remove the passenger from the Vehicle.
     * @param car
     */
    private void tryAndRemovePlayerFromCar(Vehicle car) {
        try {
            if (car == null || car.getPassenger() == null) {
                return;
            }

            removeDriver(car.getPassenger().getName());
        } catch (NoSuchMethodError ignored) {
        }
    }

    /**
     * Get the matching Car based on entityId.
     * @param carID entity ID.
     * @return Car
     */
    public Car getCar(Integer carID) {
        return entityIdToCar.get(carID);
    }

    /**
     * Stash the Player's current vehicle to their inventory.
     * @param player
     */
    public void stashCar(Player player) {
        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
            player.sendMessage(Carz.getPrefix() + "You need to be inside your owned car!");
            return;
        }

        stashCar(player, (Vehicle) player.getVehicle());
    }

    /**
     * Stash the Vehicle to the Player's inventory.
     * @param player
     * @param vehicle
     */
    public void stashCar(Player player, Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }

        if (!player.getName().equals(getCar(vehicle.getEntityId()).getOwner())
                && !PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
            return;
        }

        removeDriver(player.getName());
        destroyCar(vehicle);
        Utils.givePlayerOwnedCar(player);
    }

    /**
     * Upgrade the Car the player is in.
     * @param player
     */
    public void upgradeCarSpeed(Player player) {
        Car car = getCar(player.getVehicle().getEntityId());
        upgradeCarSpeed(car);
        EffectUtils.playEffect(player, Effect.ZOMBIE_CHEW_WOODEN_DOOR);
        player.sendMessage(TranslationUtils.getTranslation("Car.UpgradeSpeed")
                .replace("%SPEED%", String.valueOf(car.getMaxSpeed())));

        EffectUtils.createUpgradeEffect((Vehicle) player.getVehicle());
    }

    /**
     * Apply a speed upgrade to the vehicle
     * The current speed of the car will be increased by the upgrade speed
     * until the current speed reaches the maximum upgrade limit.
     * @param car
     */
    private void upgradeCarSpeed(Car car) {
        double currentMax = car.getCarDetails().getStartMaxSpeed();
        double maxSpeed = car.getCarDetails().getMaxUpgradeSpeed();
        double upgradeBy = carz.getSettings().getUpgradeIncrement();

        if ((currentMax + upgradeBy) > maxSpeed) {//&& !event.getPlayer().hasPermission("Carz.Admin"))
            return;
        }

        car.setMaxSpeed(currentMax + upgradeBy);
    }

    public Map<String, CarDetails> getCarTypes() {
        return carTypes;
    }
}
