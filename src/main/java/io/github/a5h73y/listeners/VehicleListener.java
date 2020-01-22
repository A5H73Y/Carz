package io.github.a5h73y.listeners;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.other.DelayTasks;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.PlayerUtils;
import io.github.a5h73y.utility.TranslationUtils;
import io.github.a5h73y.utility.ValidationUtils;
import org.bukkit.Effect;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Vehicle related events.
 * The Order of events is in the typical lifecycle of a Car.
 */
public class VehicleListener implements Listener {

    private final Carz carz;

    public VehicleListener(Carz carz) {
        this.carz = carz;
    }

    /**
     * When the player enters a Vehicle.
     * If the user gets into an owned car that isn't theirs, it will be prevented.
     * The player is given a key if configured.
     * @param event
     */
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        if (!ValidationUtils.isACarzVehicle(event.getVehicle())) {
            return;
        }

        if (carz.getConfig().getBoolean("UsePermissions")
                && !event.getEntered().hasPermission("Carz.Start")) {
            return;
        }

        Player player = (Player) event.getEntered();
        Integer carID = event.getVehicle().getEntityId();
        Car car = carz.getCarController().getCar(carID);

        if (car != null && car.getOwner() != null) {
            boolean isOwner = car.getOwner().equals(player.getName());
            if (!isOwner) {
                if (!PermissionUtils.hasStrictPermission(player, Permissions.BYPASS_OWNER, false)) {
                    player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
                            .replace("%PLAYER%", car.getOwner()));
                    event.setCancelled(true);
                    return;
                } else {
                    TranslationUtils.sendTranslation("Car.CarUnlocked", player);
                }
            }
        } else if (carz.getSettings().isOnlyOwnedCarsDrive()) {
            return;
        }

        if (carz.getFuelController().isFuelEnabled()) {
            carz.getFuelController().displayFuelLevel(player);
        }

        if (carz.getConfig().getBoolean("Key.GiveOnCarEnter")
                && !player.getInventory().contains(carz.getSettings().getKey())) {
            TranslationUtils.sendTranslation("Car.KeyReceived", player);
            player.getInventory().addItem(new ItemStack(carz.getSettings().getKey()));
        }
    }

    /**
     * When the player starts / stops the engine.
     * i.e. When a player right clicks with a key (Stick by default).
     * @param event
     */
    @EventHandler
    public void onEngineToggle(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!event.getPlayer().isInsideVehicle()
                || !(event.getPlayer().getVehicle() instanceof Minecart)
                || !(event.getPlayer().getVehicle() instanceof Vehicle)) { // for some reason 1.15 needs this..?
            return;
        }

        if (!ValidationUtils.isACarzVehicle((Vehicle) event.getPlayer().getVehicle())) {
            return;
        }

        if (carz.getConfig().getBoolean("UsePermission")
                && !event.getPlayer().hasPermission("Carz.Start")) {
            return;
        }

        Integer carId = event.getPlayer().getVehicle().getEntityId();
        Car car = carz.getCarController().getCar(carId);

        if (carz.getSettings().isOnlyOwnedCarsDrive()
                && (car == null || car.getOwner() == null)) {
            return;
        }

        if (carz.getConfig().getBoolean("Key.RequireCarzKey")
                && PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != carz.getSettings().getKey()) {
            return;
        }

        if (!DelayTasks.getInstance().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        Player player = event.getPlayer();
        Minecart minecart = (Minecart) event.getPlayer().getVehicle();

        if (carz.getCarController().isDriving(player.getName())) {
            carz.getCarController().removeDriver(player.getName());
            carz.getCarController().getCar(carId).resetSpeed();
            minecart.setMaxSpeed(0D);
            TranslationUtils.sendTranslation("Car.EngineStop", player);

        } else {
            carz.getCarController().startDriving(player.getName(), player.getVehicle().getEntityId());
            minecart.setMaxSpeed(1000D);
            TranslationUtils.sendTranslation("Car.EngineStart", player);
        }
    }

    /**
     * Car drive update event.
     * The Car's speed and direction is determined.
     * @param event
     */
    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player)) {
            return;
        }

        if (!(event.getVehicle() instanceof Minecart)) {
            return;
        }

        Player player = (Player) event.getVehicle().getPassenger();

        if (!carz.getCarController().isDriving(player.getName())) {
            return;
        }

        if (event.getVehicle().getLocation().getBlock().isLiquid()
                && carz.getSettings().isDestroyInLiquid()) {
            carz.getCarController().destroyCar(event.getVehicle());
            player.playEffect(player.getLocation(), Effect.EXTINGUISH, null);
            TranslationUtils.sendTranslation("Car.LiquidDamage", player);
            return;
        }

        if (event.getVehicle().getFallDistance() > 1F && !carz.getSettings().isControlCarsWhileFalling()) {
            return;
        }

        Integer carId = event.getVehicle().getEntityId();
        Car drivingCar = carz.getCarController().getCar(carId);

        if (drivingCar.isFuelConsumed()) {
            carz.getCarController().removeDriver(player.getName());
            TranslationUtils.sendTranslation("Car.FuelEmpty", player);
            return;
        }

        drivingCar.accelerate();

        Vector vehicleVelocity = event.getVehicle().getVelocity();
        Vector playerLocationVelocity = player.getLocation().getDirection();

        double carSpeed = drivingCar.getCurrentSpeed();

        vehicleVelocity.setX((playerLocationVelocity.getX() / 100.0D) * carSpeed);
        vehicleVelocity.setZ((playerLocationVelocity.getZ() / 100.0D) * carSpeed);

        event.getVehicle().setVelocity(vehicleVelocity);
    }

    /**
     * Car destroy event.
     * @param event
     */
    @EventHandler
    public void onCarDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }

        if (!(event.getVehicle() instanceof Minecart)) {
            return;
        }

        Car car = carz.getCarController().getCar(event.getVehicle().getEntityId());

        if (car == null || car.getOwner() == null) {
            carz.getCarController().destroyCar(event.getVehicle());
            return;
        }

        event.setCancelled(true);

        if (!car.getOwner().equals(event.getAttacker().getName()) &&
                !PermissionUtils.hasStrictPermission((Player) event.getAttacker(), Permissions.ADMIN, false)) {

            event.getAttacker().sendMessage(Carz.getPrefix() + "This vehicle is owned by another player!");
            return;
        }

        carz.getCarController().stashCar((Player) event.getAttacker(), event.getVehicle());
    }

    /**
     * Vehicle Exit event.
     * When the Player requests to exit the car.
     * @param event
     */
    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }

        if (!(event.getVehicle() instanceof Minecart)) {
            return;
        }

        Player player = (Player) event.getExited();

        if (!carz.getCarController().isDriving(player.getName())) {
            return;
        }

        carz.getCarController().removeDriver(player.getName());
        TranslationUtils.sendTranslation("Car.EngineStop", player);

        Car car = carz.getCarController().getCar(event.getVehicle().getEntityId());
        car.resetSpeed();

        if (player.getName().equals(car.getOwner())) {
            TranslationUtils.sendTranslation("Car.CarLocked", player);
        }
    }
}
