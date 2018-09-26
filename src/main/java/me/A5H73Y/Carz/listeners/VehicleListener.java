package me.A5H73Y.Carz.listeners;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Permissions;
import me.A5H73Y.Carz.other.DelayTasks;
import me.A5H73Y.Carz.other.Utils;
import me.A5H73Y.Carz.other.Validation;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Set;

public class VehicleListener implements Listener {

    private final Carz carz;
    private Set<Material> climbBlocks;

    public VehicleListener(Carz carz) {
        this.carz = carz;
        reloadClimbBlocks();
    }

    public void reloadClimbBlocks() {
        this.climbBlocks = Utils.convertToValidMaterials(carz.getConfig().getStringList("ClimbBlocks.Materials"));
    }

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player))
            return;

        if (!(event.getVehicle() instanceof Minecart))
            return;

        Player player = (Player) event.getVehicle().getPassenger();
        Integer carId = event.getVehicle().getEntityId();

        if (!carz.getCarController().isDriving(player.getName()))
            return;

        if (carz.getFuelController().isFuelConsumed(carId)) {
            player.sendMessage(Utils.getTranslation("FuelEmpty"));
            carz.getCarController().removeDriver(player.getName());
            return;
        }

        if (event.getVehicle().getLocation().getBlock().isLiquid() &&
                carz.getSettings().isDestroyInLiquid()) {
            carz.getCarController().destroyCar(event.getVehicle());
            player.playEffect(player.getLocation(), Effect.EXTINGUISH, null);
            player.sendMessage(Utils.getTranslation("LiquidDamage"));
            return;
        }

        Vector playerVelocity = event.getVehicle().getVelocity();
        double carSpeed = carz.getCarController().getUpgradeController().getCarSpeed(carId);

        playerVelocity.setX((player.getEyeLocation().getDirection().getX() / 140.0D) * carSpeed);
        playerVelocity.setZ((player.getEyeLocation().getDirection().getZ() / 140.0D) * carSpeed);

        Minecart minecart = (Minecart) event.getVehicle();
        Material materialBelow = minecart.getLocation().subtract(0,1,0).getBlock().getType();

        if (climbBlocks.contains(materialBelow))
            playerVelocity.setY(playerVelocity.getY() + carz.getSettings().getClimbBlockStrength());

        event.getVehicle().setVelocity(playerVelocity);
        carz.getFuelController().decreaseFuel(carId);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player))
            return;

        if (!Validation.isACarzVehicle(event.getVehicle()))
            return;

        if (carz.getConfig().getBoolean("UsePermissions") && !event.getEntered().hasPermission("Carz.Start"))
            return;

        Player player = (Player) event.getEntered();
        Integer carID = event.getVehicle().getEntityId();

        if (carz.getCarController().isCarOwned(carID)) {
            if (!carz.getCarController().isCarOwnedByPlayer(carID, player.getName())) {
                player.sendMessage(Utils.getTranslation("Error.Owned"));
                event.setCancelled(true);
                return;
            } else {
                player.sendMessage(Utils.getTranslation("CarUnlocked"));
            }
        } else if (carz.getSettings().isOnlyOwnedCarsDrive()) {
            return;
        }

        if (carz.getFuelController().getFuelLevel(carID) != null) {
            carz.getFuelController().displayFuelLevel(player);
        }

        if (carz.getConfig().getBoolean("Key.GiveOnCarEnter") &&
                !player.getInventory().contains(carz.getSettings().getKey())) {
            player.sendMessage(Utils.getTranslation("KeyReceived"));
            player.getInventory().addItem(new ItemStack(carz.getSettings().getKey()));
        }
    }

    @EventHandler
    public void onEngineStart(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.getPlayer().isInsideVehicle())
            return;

        if (!Validation.isACarzVehicle((Vehicle) event.getPlayer().getVehicle()))
            return;

        if (carz.getConfig().getBoolean("UsePermission") && !event.getPlayer().hasPermission("Carz.Start"))
            return;

        if (carz.getSettings().isOnlyOwnedCarsDrive() &&
                !carz.getCarController().isCarOwned(event.getPlayer().getVehicle().getEntityId()))
            return;

        if (Utils.getMaterialInPlayersHand(event.getPlayer()) != carz.getSettings().getKey() &&
                carz.getConfig().getBoolean("Key.RequireCarzKey"))
            return;

        if (!DelayTasks.getInstance().delayPlayer(event.getPlayer(), 1))
            return;

        Player player = event.getPlayer();
        Minecart minecart = (Minecart) event.getPlayer().getVehicle();

        if (carz.getCarController().isDriving(player.getName())) {
            carz.getCarController().removeDriver(player.getName());
            minecart.setMaxSpeed(0D);
            player.sendMessage(Utils.getTranslation("EngineStop"));

        } else {
            carz.getCarController().addDriver(player.getName(), player.getVehicle().getEntityId());
            minecart.setMaxSpeed(1000D);
            player.sendMessage(Utils.getTranslation("EngineStart"));
        }
    }

    @EventHandler
    public void onCarDeath(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player))
            return;

        if (!(event.getVehicle() instanceof Minecart))
            return;

        if (!carz.getCarController().isCarOwned(event.getVehicle().getEntityId()))
            return;

        event.setCancelled(true);

        if (!carz.getCarController().isCarOwnedByPlayer(event.getVehicle().getEntityId(), event.getAttacker().getName()) &&
                !Utils.hasStrictPermission((Player) event.getAttacker(), Permissions.ADMIN)) {

            event.getAttacker().sendMessage(Carz.getPrefix() + "This vehicle is owned by another player!");
            return;
        }

        carz.getCarController().stashCar((Player) event.getAttacker(), event.getVehicle());
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }

        if (!(event.getVehicle() instanceof Minecart))
            return;

        Player player = (Player) event.getExited();

        if (!carz.getCarController().isDriving(player.getName()))
            return;

        carz.getCarController().removeDriver(player.getName());
        player.sendMessage(Utils.getTranslation("EngineStop"));

        if (carz.getCarController().isCarOwnedByPlayer(event.getVehicle().getEntityId(), player.getName())) {
            player.sendMessage(Utils.getTranslation("CarLocked"));
        }
    }
}
