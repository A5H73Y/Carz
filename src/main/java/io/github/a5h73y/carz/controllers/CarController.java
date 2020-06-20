package io.github.a5h73y.carz.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.enums.VehicleDetailKey;
import io.github.a5h73y.carz.event.CarStashEvent;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.model.CarDetails;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.utility.CarUtils;
import io.github.a5h73y.carz.utility.EffectUtils;
import io.github.a5h73y.carz.utility.PermissionUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

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

    /**
     * Get the matching Car based on entityId.
     *
     * @param carID entity ID.
     * @return Car
     */
    public Car getCar(Integer carID) {
        return entityIdToCar.get(carID);
    }

    /**
     * Try and find the matching {@link Car} for the Vehicle.
     * If it doesn't exist, check if it has a vehicle type (it was once registered).
     *
     * @param vehicle {@link Vehicle}
     * @return Car
     */
    public Car getCar(Minecart vehicle) {
        Car car = getCar(vehicle.getEntityId());
        if (car == null && carz.getItemMetaUtils().has(VehicleDetailKey.VEHICLE_TYPE, vehicle)) {
            car = getOrCreateCar(vehicle.getEntityId(),
                    carz.getItemMetaUtils().getValue(VehicleDetailKey.VEHICLE_TYPE, vehicle));
        }
        return car;
    }

    /**
     * Get the matching known Car.
     * Otherwise, create a new entry for the car.
     *
     * @param entityId vehicle entity id
     * @param carType requested car type
     * @return matching or new Car
     */
    public Car getOrCreateCar(Integer entityId, String carType) {
        return entityIdToCar.computeIfAbsent(entityId, k -> new Car(entityId, carType));
    }

    /**
     * Register a new car.
     * Sets the default car speed, and starts the fuel management.
     *
     * @param entityId vehicle entity id
     */
    public Car getOrCreateCar(Integer entityId) {
        return getOrCreateCar(entityId, DEFAULT_CAR);
    }

    /**
     * Register the player as the driving for the Car.
     *
     * @param playerName player driving
     * @param vehicle vehicle the player is driving
     */
    public void startDriving(String playerName, Vehicle vehicle) {
        String carType = carz.getItemMetaUtils().getValue(VehicleDetailKey.VEHICLE_TYPE, vehicle);
        boolean existed = entityIdToCar.containsKey(vehicle.getEntityId());

        Car car = getOrCreateCar(vehicle.getEntityId(), carType);

        if (carz.getItemMetaUtils().has(VehicleDetailKey.VEHICLE_SPEED, vehicle)) {
            car.setMaxSpeed(Double.parseDouble(
                    carz.getItemMetaUtils().getValue(VehicleDetailKey.VEHICLE_SPEED, vehicle)));
        }
        // if the car wasn't known and it has fuel data - set it.
        if (!existed && carz.getItemMetaUtils().has(VehicleDetailKey.VEHICLE_FUEL, vehicle)) {
            car.setCurrentFuel(Double.parseDouble(
                    carz.getItemMetaUtils().getValue(VehicleDetailKey.VEHICLE_FUEL, vehicle)));
        }

        playersDriving.put(playerName, car.getEntityId());
    }

    /**
     * Remove player from players driving.
     *
     * @param playerName player name
     */
    public void removeDriver(String playerName) {
        playersDriving.remove(playerName);
    }

    /**
     * Is the player currently driving a Car.
     *
     * @param playerName requested player name
     * @return player driving?
     */
    public boolean isDriving(String playerName) {
        return playersDriving.containsKey(playerName);
    }

    /**
     * Remove all references to Car.
     * Eject player(s), remove the ownership and fuel management.
     *
     * @param vehicle {@link Vehicle}
     */
    public void removeCar(Vehicle vehicle) {
        entityIdToCar.remove(vehicle.getEntityId());
        removeAllDriversFromVehicle(vehicle);

        vehicle.eject();
        vehicle.remove();
    }

    /**
     * Destroy a vehicle.
     * Remove references from the Vehicle and play a damage effect.
     *
     * @param vehicle {@link Vehicle}
     */
    public void destroyCar(Vehicle vehicle) {
        removeCar(vehicle);
        EffectUtils.createDamageEffect(vehicle);
    }

    /**
     * Remove the passenger(s) from the Vehicle.
     *
     * @param vehicle {@link Vehicle}
     */
    private void removeAllDriversFromVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getPassengers().isEmpty()) {
            return;
        }

        vehicle.getPassengers().forEach(entity -> removeDriver(entity.getName()));
    }

    /**
     * Stash the Player's current vehicle to their inventory.
     *
     * @param player requesting player
     */
    public void stashCar(Player player) {
        if (!player.isInsideVehicle() || !ValidationUtils.isACarzVehicle((Vehicle) player.getVehicle())) {
            TranslationUtils.sendTranslation("Error.NotInCar", player);
            return;
        }

        stashCar(player, (Minecart) player.getVehicle());
    }

    /**
     * Stash the Vehicle to the Player's inventory.
     *
     * @param player requesting player
     * @param vehicle minecart to stash
     */
    public void stashCar(Player player, Minecart vehicle) {
        if (vehicle == null || !ValidationUtils.isACarzVehicle(vehicle)) {
            return;
        }

        String owner = carz.getItemMetaUtils().getValue(VehicleDetailKey.VEHICLE_OWNER, vehicle);

        if (!player.getName().equals(owner)
                && !PermissionUtils.hasStrictPermission(player, Permissions.BYPASS_OWNER, false)) {
            player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
                    .replace("%PLAYER%", owner));
            return;
        }

        Bukkit.getServer().getPluginManager().callEvent(new CarStashEvent(player, vehicle));
        CarUtils.transferMinecartToInventory(player, vehicle);
        removeCar(vehicle);
    }

    /**
     * Upgrade the Car the player is in.
     *
     * @param player requesting player
     */
    public void upgradeCarSpeed(Player player) {
        if (player.getVehicle() == null || !(player.getVehicle() instanceof Minecart)) {
            return;
        }

        upgradeCarSpeed((Minecart) player.getVehicle());
        Car car = getCar(player.getVehicle().getEntityId());
        EffectUtils.playEffect(player, Effect.ZOMBIE_CHEW_WOODEN_DOOR);
        TranslationUtils.sendValueTranslation("Car.UpgradeSpeed",
                String.valueOf(car.getMaxSpeed()), true, player);

        EffectUtils.createUpgradeEffect((Vehicle) player.getVehicle());
    }

    /**
     * Apply a speed upgrade to the vehicle.
     * The current speed of the car will be increased by the upgrade speed amount
     * until the current speed reaches the maximum upgrade limit.
     *
     * @param vehicle minecart to upgrade
     */
    private void upgradeCarSpeed(Minecart vehicle) {
        Car car = getCar(vehicle.getEntityId());
        double currentMax = car.getMaxSpeed();
        double maxSpeed = car.getCarDetails().getMaxUpgradeSpeed();
        double upgradeAmount = Carz.getDefaultConfig().getUpgradeIncrement();

        if ((currentMax + upgradeAmount) > maxSpeed) {
            return;
        }

        car.setMaxSpeed(currentMax + upgradeAmount);
        carz.getItemMetaUtils().setValue(
                VehicleDetailKey.VEHICLE_SPEED, vehicle, String.valueOf(car.getMaxSpeed()));
    }

    /**
     * Available Car Types.
     *
     * @return car types.
     */
    public Map<String, CarDetails> getCarTypes() {
        return carTypes;
    }

    /**
     * Check if the requested car type exists.
     * @param carType car type
     *
     * @return car types exist.
     */
    public boolean doesCarTypeExist(String carType) {
        return carTypes.containsKey(carType.toLowerCase());
    }

    /**
     * Populate the available Car Types.
     * Cache the available Car Types from the Config using the name as the key.
     */
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
     * Set the Owner of the car to the Player.
     *
     * @param player requesting player
     */
    public void claimOwnership(Player player) {
        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
            return;
        }

        carz.getItemMetaUtils().setValue(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle(), player.getName());
        TranslationUtils.sendTranslation("Car.Claimed", player);
    }

    /**
     * Remove the Ownership data from the player's vehicle.
     *
     * @param player requesting player
     */
    public void removeOwnership(Player player) {
        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
            return;
        }

        carz.getItemMetaUtils().remove(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle());
        TranslationUtils.sendTranslation("Car.OwnershipRemoved", player);
    }
}
