package io.github.a5h73y.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.model.CarDetails;
import io.github.a5h73y.other.AbstractPluginReceiver;
import io.github.a5h73y.utility.CarUtils;
import io.github.a5h73y.utility.EffectUtils;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.Effect;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_SPEED;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_TYPE;

/**
 * All Car related functionality.
 */
public class CarController extends AbstractPluginReceiver {

    public static final String DEFAULT_CAR = "default";

    // Vehicle ID with it's associated Car
    private final Map<Integer, Car> entityIdToCar = new HashMap<>();

    // Players currently inside a Carz vehicle, with the value being Vehicle ID
    private final Map<String, Integer> playersDriving = new HashMap<>();

    // All available Car Types
    private final Map<String, CarDetails> carTypes = new HashMap<>();

    public CarController(final Carz carz) {
        super(carz);
        populateCarTypes();
    }

    public void populateCarTypes() {
        carTypes.clear();
        Set<String> allCarTypes = carz.getConfig().getConfigurationSection("CarTypes").getKeys(false);

        for (String carType : allCarTypes) {
            String configPath = "CarTypes." + carType;
            double startSpeed = carz.getConfig().getDouble(configPath + ".StartMaxSpeed");
            double maxSpeed = carz.getConfig().getDouble(configPath + ".MaxUpgradeSpeed");
            double acceleration = carz.getConfig().getDouble(configPath + ".Acceleration");
            double fuelUsage = carz.getConfig().getDouble(configPath + ".FuelUsage");
            String fillMaterial = carz.getConfig().getString(configPath + ".FillMaterial");
            carTypes.put(carType, new CarDetails(startSpeed, maxSpeed, acceleration, fuelUsage, fillMaterial));
        }
    }

    /**
     * Register the player as the driving for the Car.
     * @param playerName player driving
     * @param vehicle vehicle the player is driving
     */
    public void startDriving(String playerName, Vehicle vehicle) {
        String carType = carz.getItemMetaUtils().getValue(VEHICLE_TYPE, vehicle);
        Car car = getOrCreateCar(vehicle.getEntityId(), carType);

        if (carz.getItemMetaUtils().has(VEHICLE_SPEED, vehicle)) {
            car.setMaxSpeed(Double.parseDouble(carz.getItemMetaUtils().getValue(VEHICLE_SPEED, vehicle)));
        }
        if (carz.getItemMetaUtils().has(VEHICLE_FUEL, vehicle)) {
            car.setCurrentFuel(Double.parseDouble(carz.getItemMetaUtils().getValue(VEHICLE_FUEL, vehicle)));
        }

        playersDriving.put(playerName, car.getEntityId());
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
        if (car == null || car.getPassengers().isEmpty()) {
            return;
        }

        car.getPassengers().forEach(entity -> removeDriver(entity.getName()));
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

        String owner = carz.getItemMetaUtils().getValue(VEHICLE_OWNER, vehicle);

        if (!player.getName().equals(owner) && !PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
            return;
        }

        removeDriver(player.getName());
        CarUtils.transferVehicleToItemStack(player, vehicle);
        destroyCar(vehicle);
    }

    /**
     * Upgrade the Car the player is in.
     * @param player
     */
    public void upgradeCarSpeed(Player player) {
        if (player.getVehicle() == null) {
            return;
        }

        upgradeCarSpeed((Vehicle) player.getVehicle());
        Car car = getCar(player.getVehicle().getEntityId());
        EffectUtils.playEffect(player, Effect.ZOMBIE_CHEW_WOODEN_DOOR);
        player.sendMessage(TranslationUtils.getTranslation("Car.UpgradeSpeed")
                .replace("%SPEED%", String.valueOf(car.getMaxSpeed())));

        EffectUtils.createUpgradeEffect((Vehicle) player.getVehicle());
    }

    /**
     * Apply a speed upgrade to the vehicle
     * The current speed of the car will be increased by the upgrade speed
     * until the current speed reaches the maximum upgrade limit.
     * @param vehicle
     */
    private void upgradeCarSpeed(Vehicle vehicle) {
        Car car = getCar(vehicle.getEntityId());
        double currentMax = car.getMaxSpeed();
        double maxSpeed = car.getCarDetails().getMaxUpgradeSpeed();
        double upgradeBy = carz.getSettings().getUpgradeIncrement();

        if ((currentMax + upgradeBy) > maxSpeed) {//&& !event.getPlayer().hasPermission("Carz.Admin"))
            return;
        }

        car.setMaxSpeed(currentMax + upgradeBy);
        carz.getItemMetaUtils().setValue(VEHICLE_SPEED, vehicle, String.valueOf(car.getMaxSpeed()));
    }

    public Map<String, CarDetails> getCarTypes() {
        return carTypes;
    }

    public boolean doesCarTypeExist(String carType) {
        return carTypes.containsKey(carType.toLowerCase());
    }
}
