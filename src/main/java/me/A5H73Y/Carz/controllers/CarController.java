package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.other.Utils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        removeOwnership(car.getEntityId());
        upgradeController.removeCar(car.getEntityId());
        //TODO improve this (1.7)
        tryAndRemovePlayerFromCar(car);
        carz.getFuelController().deregisterCar(car.getEntityId());
        createDamageEffect(car);
        car.eject();
        car.remove();
    }

    /**
     * Pain in the arse method because getPassengers() would break <1.13
     * @param car
     */
    private void tryAndRemovePlayerFromCar(Vehicle car) {
        try {
            if (car == null || car.getPassenger() == null)
                return;

            removeDriver(car.getPassenger().getName());
        } catch (NoSuchMethodError ex) {}
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

    public void createEffect(Location location, Effect effect, final int repeat) {
        if (!carz.getConfig().getBoolean("Other.UseEffects"))
            return;

        new BukkitRunnable() {
            int amount = repeat;
            @Override
            public void run() {
                if (amount == 0) cancel();
                location.getWorld().playEffect(location, effect, 4);
                amount--;
            }
        }.runTaskTimer(Carz.getInstance(), 0, 10);
    }

    public void createDamageEffect(Vehicle car) {
        createEffect(car.getLocation().add(0, 1, 0), Effect.SMOKE, 2);
    }

    public void createUpgradeEffect(Vehicle car) {
        createEffect(car.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    }

    public void stashCar(Player player) {
        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)) {
            player.sendMessage(Carz.getPrefix() + "You need to be inside your owned car!");
            return;
        }

        stashCar(player, (Vehicle) player.getVehicle());
    }

    public void stashCar(Player player, Vehicle vehicle) {
        if (vehicle == null)
            return;

        if (!isCarOwnedByPlayer(vehicle.getEntityId(), player.getName())) {
            return;
        }

        removeDriver(player.getName());
        destroyCar(vehicle);
        Utils.givePlayerOwnedCar(player);
    }
}