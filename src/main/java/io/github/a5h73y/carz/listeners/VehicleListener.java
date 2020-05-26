package io.github.a5h73y.carz.listeners;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.configuration.impl.BlocksConfig;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.event.EngineStartEvent;
import io.github.a5h73y.carz.event.EngineStopEvent;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.other.DelayTasks;
import io.github.a5h73y.carz.utility.CarUtils;
import io.github.a5h73y.carz.utility.PermissionUtils;
import io.github.a5h73y.carz.utility.PlayerUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

import static io.github.a5h73y.carz.enums.ConfigType.BLOCKS;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_LOCKED;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.carz.enums.VehicleDetailKey.VEHICLE_TYPE;

/**
 * Vehicle related events.
 * The order of Events is in the typical lifecycle of a Car.
 */
public class VehicleListener extends AbstractPluginReceiver implements Listener {

    public VehicleListener(Carz carz) {
        super(carz);
    }

    /**
     * When the player enters a Vehicle.
     * If the user gets into an owned car that isn't theirs, it will be prevented.
     * The player is given a key if configured.
     *
     * @param event {@link VehicleEnterEvent}
     */
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        if (!ValidationUtils.isACarzVehicle(event.getVehicle())) {
            return;
        }

        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, event.getVehicle())) {
            return;
        }

        Player player = (Player) event.getEntered();

        if (!Carz.getDefaultConfig().isAutomaticCarLock() && player.isSneaking()) {
            return;
        }

        if (carz.getConfig().getBoolean("UsePermissions")
                && !player.hasPermission("Carz.Start")) {
            return;
        }

        Minecart minecart = (Minecart) event.getVehicle();

        if (carz.getItemMetaUtils().has(VEHICLE_LOCKED, minecart)
                && carz.getItemMetaUtils().has(VEHICLE_OWNER, minecart)) {
            String owner = carz.getItemMetaUtils().getValue(VEHICLE_OWNER, minecart);
            boolean isOwner = owner.equalsIgnoreCase(player.getName());

            if (!isOwner && !PermissionUtils.hasStrictPermission(player, Permissions.BYPASS_OWNER, false)) {
                player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
                        .replace("%PLAYER%", owner));
                event.setCancelled(true);
                return;

            } else {
                carz.getItemMetaUtils().remove(VEHICLE_LOCKED, minecart);
                TranslationUtils.sendTranslation("Car.CarUnlocked", player);
            }
        } else if (Carz.getDefaultConfig().isOnlyOwnedCarsDrive()) {
            return;
        }

        if (carz.getFuelController().isFuelEnabled()) {
            carz.getFuelController().displayFuelLevel(player);
        }

        if (carz.getConfig().getBoolean("Key.GiveOnCarEnter")
                && !player.getInventory().contains(Carz.getDefaultConfig().getKey())) {
            CarUtils.givePlayerKey(player);
        }
    }

    /**
     * When the player starts / stops the engine.
     * i.e. When a player right clicks with a key (Stick by default).
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onEngineToggle(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!event.getPlayer().isInsideVehicle()
                || !(event.getPlayer().getVehicle() instanceof Vehicle)
                || !(event.getPlayer().getVehicle() instanceof Minecart)) {
            return;
        }

        Vehicle vehicle = (Vehicle) event.getPlayer().getVehicle();
        if (!ValidationUtils.isACarzVehicle(vehicle)) {
            return;
        }

        if (carz.getConfig().getBoolean("UsePermission")
                && !event.getPlayer().hasPermission("Carz.Start")) {
            return;
        }

        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, vehicle)) {
            return;
        }

        Car car = carz.getCarController().getCar(vehicle.getEntityId());

        if (Carz.getDefaultConfig().isOnlyOwnedCarsDrive() && !carz.getItemMetaUtils().has(VEHICLE_OWNER, vehicle)) {
            return;
        }

        if (carz.getConfig().getBoolean("Key.RequireCarzKey")
                && PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != Carz.getDefaultConfig().getKey()) {
            return;
        }

        if (!DelayTasks.getInstance().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        Player player = event.getPlayer();
        Minecart minecart = (Minecart) event.getPlayer().getVehicle();

        if (carz.getCarController().isDriving(player.getName())) {
            carz.getCarController().removeDriver(player.getName());
            car.resetSpeed();
            minecart.setMaxSpeed(0D);
            TranslationUtils.sendTranslation("Car.EngineStop", player);
            carz.getItemMetaUtils().setValue(VEHICLE_FUEL, minecart, car.getCurrentFuel().toString());
            Bukkit.getServer().getPluginManager().callEvent(new EngineStopEvent(player, car));

        } else {
            carz.getCarController().startDriving(player.getName(), minecart);
            minecart.setMaxSpeed(1000D);
            TranslationUtils.sendTranslation("Car.EngineStart", player);
            Bukkit.getServer().getPluginManager().callEvent(new EngineStartEvent(player, car));
        }
    }

    /**
     * Car drive update event.
     * The Car's speed and direction is calculated.
     *
     * @param event {@link VehicleUpdateEvent}
     */
    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        if (!(event.getVehicle() instanceof Minecart)) {
            return;
        }

        if (event.getVehicle().getPassengers().isEmpty()
                || !(event.getVehicle().getPassengers().get(0) instanceof Player)) {
            return;
        }

        Player player = (Player) event.getVehicle().getPassengers().get(0);

        if (!carz.getCarController().isDriving(player.getName())) {
            return;
        }

        if (event.getVehicle().getLocation().getBlock().isLiquid()
                && Carz.getDefaultConfig().isDestroyInLiquid()) {
            carz.getCarController().destroyCar(event.getVehicle());
            player.playEffect(player.getLocation(), Effect.EXTINGUISH, null);
            TranslationUtils.sendTranslation("Car.LiquidDamage", player);
            return;
        }

        if (event.getVehicle().getFallDistance() > 1F && !Carz.getDefaultConfig().isControlCarsWhileFalling()) {
            return;
        }

        Integer carId = event.getVehicle().getEntityId();
        Car drivingCar = carz.getCarController().getCar(carId);

        if (drivingCar.isFuelConsumed()) {
            carz.getCarController().removeDriver(player.getName());
            carz.getItemMetaUtils().setValue(VEHICLE_FUEL, event.getVehicle(), "0");
            TranslationUtils.sendTranslation("Car.FuelEmpty", player);
            return;
        }

        drivingCar.accelerate();

        Vector vehicleVelocity = event.getVehicle().getVelocity();
        Vector playerLocationVelocity = player.getLocation().getDirection();
        Material materialBelow = event.getVehicle().getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType();
        BlocksConfig blocksConfig = (BlocksConfig) Carz.getConfig(BLOCKS);

        if (blocksConfig.containsSpeedBlock(materialBelow)) {
            Double modifier = blocksConfig.getSpeedModifier(materialBelow);
            drivingCar.applySpeedModifier(modifier);
        }

        if (blocksConfig.containsLaunchBlock(materialBelow)) {
            Double amount = blocksConfig.getLaunchAmount(materialBelow);
            vehicleVelocity.setY(vehicleVelocity.getY() + amount);
        }

        double carSpeed = drivingCar.getCurrentSpeed();

        vehicleVelocity.setX((playerLocationVelocity.getX() / 100.0) * carSpeed);
        vehicleVelocity.setZ((playerLocationVelocity.getZ() / 100.0) * carSpeed);

        Location playerLocation = player.getLocation().clone();
        playerLocation.setPitch(0f);

        Location twoBlocksAhead = playerLocation.add(playerLocation.getDirection().multiply(2));
        twoBlocksAhead.setY(Math.max(playerLocation.getY() + 1, twoBlocksAhead.getY()));

        boolean isClimbable = twoBlocksAhead.getBlock().getType() != Material.AIR
                && ((blocksConfig.getClimbBlocks().isEmpty() || twoBlocksAhead.getBlock().getBlockData() instanceof Slab)
                || blocksConfig.getClimbBlocks().contains(twoBlocksAhead.getBlock().getType()));

        // if there is a block ahead of us
        if (isClimbable && materialBelow != Material.AIR) {
            Location above = twoBlocksAhead.add(0, 1, 0);

            // if the block above it is AIR, allow to climb
            if (above.getBlock().getType() == Material.AIR) {
                vehicleVelocity.setY(0.25);

                vehicleVelocity.setX(playerLocationVelocity.getX() / 8.0);
                vehicleVelocity.setZ(playerLocationVelocity.getZ() / 8.0);
            }
        }

        event.getVehicle().setVelocity(vehicleVelocity);
    }

    /**
     * Car destroy event.
     *
     * @param event {@link VehicleDestroyEvent}
     */
    @EventHandler
    public void onCarDestroy(VehicleDestroyEvent event) {
        if (!(event.getVehicle() instanceof Minecart)) {
            return;
        }

        Minecart minecart = (Minecart) event.getVehicle();

        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, minecart)) {
            return;
        }

        if (!carz.getItemMetaUtils().has(VEHICLE_OWNER, minecart)) {
            carz.getCarController().destroyCar(minecart);
            return;
        }

        event.setCancelled(true);

        if (event.getAttacker() instanceof Player) {
            String owner = carz.getItemMetaUtils().getValue(VEHICLE_OWNER, minecart);

            if (!event.getAttacker().getName().equals(owner)
                    && !PermissionUtils.hasStrictPermission((Player) event.getAttacker(), Permissions.ADMIN, false)) {

                String ownedMessage = TranslationUtils.getTranslation("Error.Owned")
                        .replace("%PLAYER%", owner);
                event.getAttacker().sendMessage(ownedMessage);

            } else {
                carz.getCarController().stashCar((Player) event.getAttacker(), minecart);
            }
        }
    }

    /**
     * Vehicle Exit event.
     * When the Player requests to exit the car.
     *
     * @param event {@link VehicleExitEvent}
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

        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, event.getVehicle())) {
            return;
        }

        Minecart vehicle = (Minecart) event.getVehicle();

        if (carz.getCarController().isDriving(player.getName())) {
            carz.getCarController().removeDriver(player.getName());
            TranslationUtils.sendTranslation("Car.EngineStop", player);
        }

        if (Carz.getDefaultConfig().isAutomaticCarLock()
                && carz.getItemMetaUtils().has(VEHICLE_OWNER, vehicle)
                && player.getName().equals(carz.getItemMetaUtils().getValue(VEHICLE_OWNER, vehicle))) {
            carz.getItemMetaUtils().setValue(VEHICLE_LOCKED, vehicle, "true");
            TranslationUtils.sendTranslation("Car.CarLocked", player);
        }

        Car car = carz.getCarController().getCar(vehicle.getEntityId());

        // car could be destroyed at this point (i.e. Water damage)
        if (car != null) {
            car.resetSpeed();
            carz.getItemMetaUtils().setValue(VEHICLE_FUEL, vehicle, car.getCurrentFuel().toString());
        }
    }

    /**
     * Vehicle Entity Collision Event.
     * When a driver hits an entity with their car, damage amount configurable.
     *
     * @param event {@link VehicleEntityCollisionEvent}
     */
    @EventHandler
    public void onVehicleCollide(VehicleEntityCollisionEvent event) {
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

        if (!carz.getConfig().getBoolean("Other.DamageEntities.Enabled")) {
            return;
        }

        double damage = carz.getConfig().getDouble("Other.DamageEntities.Damage");

        if (event.getEntity() instanceof LivingEntity) {
            ((LivingEntity) event.getEntity()).damage(damage, player);
        }
    }

    /**
     * When the player requests to manually lock the car.
     * If the player is sneaking with a key in their hand.
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onCarLockToggle(PlayerInteractEntityEvent event) {
        if (!((event.getRightClicked()) instanceof Vehicle)) {
            return;
        }

        if (!ValidationUtils.isACarzVehicle((Vehicle) event.getRightClicked())) {
            return;
        }

        Minecart vehicle = (Minecart) event.getRightClicked();

        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, vehicle)) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != Carz.getDefaultConfig().getKey()) {
            return;
        }

        if (!carz.getItemMetaUtils().has(VEHICLE_TYPE, vehicle)) {
            return;
        }

        if (carz.getItemMetaUtils().has(VEHICLE_OWNER, vehicle)) {
            String owner = carz.getItemMetaUtils().getValue(VEHICLE_OWNER, vehicle);

            if (!owner.equals(event.getPlayer().getName())) {
                event.getPlayer().sendMessage(TranslationUtils.getTranslation("Error.Owned")
                        .replace("%PLAYER%", owner));
                return;
            }
        }

        if (!DelayTasks.getInstance().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        if (carz.getItemMetaUtils().has(VEHICLE_LOCKED, vehicle)) {
            carz.getItemMetaUtils().remove(VEHICLE_LOCKED, vehicle);
            TranslationUtils.sendTranslation("Car.CarUnlocked", event.getPlayer());

        } else {
            carz.getItemMetaUtils().setValue(VEHICLE_LOCKED, vehicle, "true");
            TranslationUtils.sendTranslation("Car.CarLocked", event.getPlayer());
        }
    }
}