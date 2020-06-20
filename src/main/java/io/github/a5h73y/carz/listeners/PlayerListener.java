package io.github.a5h73y.carz.listeners;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.controllers.CarController;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.utility.PlayerUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_LOCKED;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_TYPE;

/**
 * Player related Events.
 */
public class PlayerListener extends AbstractPluginReceiver implements Listener {

    public PlayerListener(Carz carz) {
        super(carz);
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
        if (event.getClickedBlock().getBlockData() instanceof Rail) {
            return;
        }

        Player player = event.getPlayer();

        if (!ValidationUtils.canPlaceCar(player, event.getClickedBlock().getType())) {
            return;
        }

        ItemStack carInHand = player.getInventory().getItemInMainHand();

        // if only owned cars can drive and it doesn't have a vehicle type, ignore it.
        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, carInHand)) {
            if (Carz.getDefaultConfig().isOnlyOwnedCarsDrive()) {
                return;
            }

            carz.getItemMetaUtils().setValue(VEHICLE_TYPE, carInHand, CarController.DEFAULT_CAR);
        }

        // if the Minecart has an owner
        if (carz.getItemMetaUtils().has(VEHICLE_OWNER, carInHand)) {
            String owner = carz.getItemMetaUtils().getValue(VEHICLE_OWNER, carInHand);

            // check that the owner data matches the current player
            if (!owner.equalsIgnoreCase(player.getName())) {
                player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
                        .replace("%PLAYER%", owner));
                return;
            }
            // lock the car by default when placed
            carz.getItemMetaUtils().setValue(VEHICLE_LOCKED, carInHand, "true");
        }

        Location location = event.getClickedBlock().getLocation().add(0, 1, 0);
        Minecart spawnedCar = location.getWorld().spawn(location, Minecart.class);

        carz.getItemMetaUtils().transferNamespaceKeyValues(carInHand, spawnedCar);

        String vehicleType = carz.getItemMetaUtils().getValue(VEHICLE_TYPE, spawnedCar);
        Material fillMaterial = carz.getCarController().getCarTypes().get(vehicleType).getFillMaterial();

        if (fillMaterial != null && fillMaterial != Material.AIR) {
            BlockData data = Bukkit.createBlockData(fillMaterial);
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

        if (!player.isInsideVehicle() || !carz.getCarController().isDriving(player.getName())) {
            return;
        }

        Car car = carz.getCarController().getCar((Minecart) player.getVehicle());
        carz.getItemMetaUtils().setValue(VEHICLE_FUEL, player.getVehicle(), car.getCurrentFuel().toString());
    }
}
