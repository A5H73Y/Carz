package io.github.a5h73y.vehiclez.listeners;

import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_LOCKED;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_TYPE;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.controllers.CarController;
import io.github.a5h73y.vehiclez.model.Car;
import io.github.a5h73y.vehiclez.other.AbstractPluginReceiver;
import io.github.a5h73y.vehiclez.utility.PlayerUtils;
import io.github.a5h73y.vehiclez.utility.PluginUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import io.github.a5h73y.vehiclez.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Player related Events.
 */
public class PlayerListener extends AbstractPluginReceiver implements Listener {

    public PlayerListener(Vehiclez vehiclez) {
        super(vehiclez);
    }

    /**
     * When the player places a Minecart.
     * Determine if it's an owned Car.
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onPlaceMinecart(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // check that they are right clicking the floor with a Minecart
        if (PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != Material.MINECART) {
            return;
        }

        // make sure they aren't trying to place a normal Minecart (i.e on rails)
        if (ValidationUtils.isRail(event.getClickedBlock())) {
            return;
        }

        Player player = event.getPlayer();

        if (!ValidationUtils.canPlaceCar(player, event.getClickedBlock().getType())) {
            return;
        }

        Location location = event.getClickedBlock().getLocation().add(0, 1, 0);

        if (!location.getBlock().isEmpty()) {
            TranslationUtils.sendTranslation("Error.InvalidPlaceLocation", player);
            return;
        }

        ItemStack carInHand = player.getInventory().getItemInMainHand();

        // if only owned cars can drive and it doesn't have a vehicle type, ignore it.
        if (!vehiclez.getCarDataPersistence().has(VEHICLE_TYPE, carInHand)) {
            if (Vehiclez.getDefaultConfig().isOnlyOwnedCarsDrive()) {
                return;
            }

            vehiclez.getCarDataPersistence().setValue(VEHICLE_TYPE, carInHand, CarController.DEFAULT_CAR);

        } else if (!vehiclez.getCarController().doesCarTypeExist(
                vehiclez.getCarDataPersistence().getValue(VEHICLE_TYPE, carInHand))) {
            // the player has a Minecart with a vehicle type, but it no longer exists.
            return;
        }

        // if the Minecart has an owner
        if (vehiclez.getCarDataPersistence().has(VEHICLE_OWNER, carInHand)) {
            String owner = vehiclez.getCarDataPersistence().getValue(VEHICLE_OWNER, carInHand);

            // check that the owner data matches the current player
            if (!owner.equalsIgnoreCase(player.getName())) {
                TranslationUtils.sendValueTranslation("Error.Owned", owner, player);
                return;
            }
            // lock the car by default when placed
            if (PluginUtils.getMinorServerVersion() >= 14) {
                vehiclez.getCarDataPersistence().setValue(VEHICLE_LOCKED, carInHand, "true");
            }
        }

        Minecart spawnedCar = location.getWorld().spawn(location, Minecart.class);

        vehiclez.getCarDataPersistence().transferNamespaceKeyValues(carInHand, spawnedCar);

        String vehicleType = vehiclez.getCarDataPersistence().getValue(VEHICLE_TYPE, spawnedCar);
        String fillMaterialData = vehiclez.getCarController().getCarTypes().get(vehicleType).getFillMaterialData();

        if (ValidationUtils.isStringValid(fillMaterialData)) {
            BlockData data = Bukkit.createBlockData(fillMaterialData);
            spawnedCar.setDisplayBlockData(data);
        }

        PlayerUtils.reduceItemStackInPlayersHand(player);
    }

    /**
     * When the player disconnects from the server.
     * If they are driving, persist the amount of fuel they had.
     *
     * @param event {@link PlayerQuitEvent}
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!player.isInsideVehicle() || !vehiclez.getCarController().isDriving(player.getName())) {
            return;
        }

        Car car = vehiclez.getCarController().getCar((Minecart) player.getVehicle());
        vehiclez.getCarDataPersistence().setValue(VEHICLE_FUEL, player.getVehicle(), car.getCurrentFuel().toString());
    }

    /**
     * When the player takes fall damage.
     *
     * @param event {@link EntityDamageEvent}
     */
    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player
                && vehiclez.getCarController().isDriving(event.getEntity().getName())
                && event.getCause() == EntityDamageEvent.DamageCause.FALL
                && vehiclez.getConfig().isFallDamageDisabled()) {
            event.setCancelled(true);
        }
    }
}
