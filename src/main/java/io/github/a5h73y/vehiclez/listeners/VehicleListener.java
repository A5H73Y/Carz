package io.github.a5h73y.vehiclez.listeners;

import static io.github.a5h73y.vehiclez.enums.ConfigType.BLOCKS;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_FUEL;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_LOCKED;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.vehiclez.enums.VehicleDetailKey.VEHICLE_TYPE;
import static org.bukkit.Material.AIR;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.configuration.impl.BlocksConfig;
import io.github.a5h73y.vehiclez.controllers.CarController;
import io.github.a5h73y.vehiclez.enums.Permissions;
import io.github.a5h73y.vehiclez.event.EngineStartEvent;
import io.github.a5h73y.vehiclez.event.EngineStopEvent;
import io.github.a5h73y.vehiclez.model.Car;
import io.github.a5h73y.vehiclez.other.AbstractPluginReceiver;
import io.github.a5h73y.vehiclez.other.DelayTasks;
import io.github.a5h73y.vehiclez.utility.CarUtils;
import io.github.a5h73y.vehiclez.utility.PermissionUtils;
import io.github.a5h73y.vehiclez.utility.PlayerUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import io.github.a5h73y.vehiclez.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

/**
 * Vehicle related events.
 * The order of Events is in the typical lifecycle of a Car.
 */
public class VehicleListener extends AbstractPluginReceiver implements Listener {

    public VehicleListener(Vehiclez vehiclez) {
        super(vehiclez);
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

        if (event.getVehicle() instanceof Minecart
                && !ValidationUtils.isRail(event.getVehicle().getLocation().getBlock())
                && !vehiclez.getCarDataPersistence().has(VEHICLE_TYPE, event.getVehicle())
                && vehiclez.getConfig().getBoolean("Other.DriveAnyMinecart")) {
            vehiclez.getCarDataPersistence().setValue(VEHICLE_TYPE, event.getVehicle(), CarController.DEFAULT_CAR);
        }

        if (!ValidationUtils.isAVehiclezVehicle(event.getVehicle())) {
            return;
        }

        Player player = (Player) event.getEntered();

        if (!Vehiclez.getDefaultConfig().isAutomaticLocking() && player.isSneaking()) {
            return;
        }

        if (!PermissionUtils.hasPermission(player, Permissions.START)) {
            return;
        }

        Minecart minecart = (Minecart) event.getVehicle();

        boolean carIsLocked = vehiclez.getConfig().isAutomaticLocking()
                || vehiclez.getCarDataPersistence().has(VEHICLE_LOCKED, minecart);

        if (carIsLocked && vehiclez.getCarDataPersistence().has(VEHICLE_OWNER, minecart)) {
            String owner = vehiclez.getCarDataPersistence().getValue(VEHICLE_OWNER, minecart);
            boolean isOwner = owner.equalsIgnoreCase(player.getName());

            if (!isOwner && !PermissionUtils.hasStrictPermission(player, Permissions.BYPASS_OWNER, false)) {
                TranslationUtils.sendValueTranslation("Error.Owned", owner, player);
                event.setCancelled(true);
                return;

            } else {
                vehiclez.getCarDataPersistence().remove(VEHICLE_LOCKED, minecart);
                TranslationUtils.sendTranslation("Car.CarUnlocked", player);
            }
        } else if (Vehiclez.getDefaultConfig().isOnlyOwnedCarsDrive()) {
            return;
        }

        if (vehiclez.getFuelController().isFuelEnabled()) {
            vehiclez.getFuelController().displayFuelLevel(player);
        }

        if (vehiclez.getConfig().isGiveKeyOnEnter()
                && !player.getInventory().contains(Vehiclez.getDefaultConfig().getKey())) {
            CarUtils.givePlayerKey(player);
        }

        if (vehiclez.getConfig().getBoolean("Other.StartCarOnVehicleEnter")) {
            PlayerInteractEvent interactEvent = new PlayerInteractEvent(player,
                    Action.RIGHT_CLICK_AIR, null, null, BlockFace.SELF);
            Bukkit.getServer().getPluginManager().callEvent(interactEvent);
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

        Minecart vehicle = (Minecart) event.getPlayer().getVehicle();

        if (!ValidationUtils.isAVehiclezVehicle(vehicle)) {
            return;
        }

        if (!PermissionUtils.hasPermission(event.getPlayer(), Permissions.START)) {
            return;
        }

        if (Vehiclez.getDefaultConfig().isOnlyOwnedCarsDrive() && !vehiclez.getCarDataPersistence().has(VEHICLE_OWNER, vehicle)) {
            return;
        }

        if (vehiclez.getConfig().getBoolean("Key.RequireVehiclezKey")
                && PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != Vehiclez.getDefaultConfig().getKey()) {
            return;
        }

        if (!DelayTasks.getInstance().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        Player player = event.getPlayer();
        Car car = vehiclez.getCarController().getCar(vehicle.getEntityId());

        if (vehiclez.getCarController().isDriving(player.getName())) {
            vehiclez.getCarController().removeDriver(player.getName());
            vehicle.setMaxSpeed(0D);
            car.resetSpeed();
            TranslationUtils.sendTranslation("Car.EngineStop", player);
            vehiclez.getCarDataPersistence().setValue(VEHICLE_FUEL, vehicle, car.getCurrentFuel().toString());
            Bukkit.getServer().getPluginManager().callEvent(new EngineStopEvent(player, car));

        } else {
            vehiclez.getCarController().startDriving(player.getName(), vehicle);
            vehicle.setMaxSpeed(1000D);
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

        Player player = CarUtils.getPlayerDrivingVehicle(event.getVehicle());

        if (player == null) {
            return;
        }

        if (!vehiclez.getCarController().isDriving(player.getName())) {
            return;
        }

        if (event.getVehicle().getLocation().getBlock().isLiquid()
                && Vehiclez.getDefaultConfig().isDestroyInLiquid()) {
            vehiclez.getCarController().destroyCar(event.getVehicle());
            player.playEffect(player.getLocation(), Effect.EXTINGUISH, null);
            TranslationUtils.sendTranslation("Car.LiquidDamage", player);
            return;
        }

        if (event.getVehicle().getFallDistance() > 1F && !Vehiclez.getDefaultConfig().isControlCarsWhileFalling()) {
            return;
        }

        Integer carId = event.getVehicle().getEntityId();
        Car drivingCar = vehiclez.getCarController().getCar(carId);

        if (drivingCar.isFuelConsumed()) {
            vehiclez.getCarController().removeDriver(player.getName());
            vehiclez.getCarDataPersistence().setValue(VEHICLE_FUEL, event.getVehicle(), "0");
            TranslationUtils.sendTranslation("Car.FuelEmpty", player);
            return;
        }

        drivingCar.accelerate();

        Vector vehicleVelocity = event.getVehicle().getVelocity();
        Vector playerLocationVelocity = player.getLocation().getDirection();
        Block blockBelow = event.getVehicle().getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
        Material materialBelow = blockBelow.getType();
        BlocksConfig blocksConfig = (BlocksConfig) Vehiclez.getConfig(BLOCKS);

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

        // determine if the Car should start climbing
        boolean isClimbable = calculateIsClimbable(blockBelow, twoBlocksAhead, blocksConfig);

        if (isClimbable) {
            Location above = twoBlocksAhead.add(0, 1, 0);

            // if the block above it is AIR, allow to climb
            if (above.getBlock().getType() == AIR) {
                vehicleVelocity.setY(vehiclez.getConfig().getClimbBlockStrength());

                vehicleVelocity.setX(playerLocationVelocity.getX() / 8.0);
                vehicleVelocity.setZ(playerLocationVelocity.getZ() / 8.0);
            }
        }

        event.getVehicle().setVelocity(vehicleVelocity);
    }

    private boolean calculateIsClimbable(Block blockBelow, Location twoBlocksAhead, BlocksConfig blocksConfig) {
        // if the block ahead isn't solid (i.e. tall grass)
        if (blockBelow.getType() == AIR || !twoBlocksAhead.getBlock().getType().isSolid()) {
            return false;
        }

        // if there are no specified climb blocks, all solid blocks are climbable
        if (blocksConfig.getClimbBlocks().isEmpty()) {
            return true;
        }

        // are slabs climbable
        if (vehiclez.getConfig().isAllSlabsClimb() && blockBelow.getBlockData() instanceof Slab) {
            return true;
        }

        // if there are climb blocks, make sure the material matches the whitelist
        return blocksConfig.getClimbBlocks().contains(twoBlocksAhead.getBlock().getType());
    }

    /**
     * Car destroy event.
     *
     * @param event {@link VehicleDestroyEvent}
     */
    @EventHandler
    public void onCarDestroy(VehicleDestroyEvent event) {
        if (!ValidationUtils.isAVehiclezVehicle(event.getVehicle())) {
            return;
        }

        Minecart minecart = (Minecart) event.getVehicle();

        if (!vehiclez.getCarDataPersistence().has(VEHICLE_OWNER, minecart)) {
            vehiclez.getCarController().destroyCar(minecart);
            return;
        }

        event.setCancelled(true);

        if (event.getAttacker() instanceof Player) {
            String owner = vehiclez.getCarDataPersistence().getValue(VEHICLE_OWNER, minecart);

            if (!event.getAttacker().getName().equals(owner)
                    && !PermissionUtils.hasStrictPermission((Player) event.getAttacker(), Permissions.BYPASS_OWNER, false)) {
                TranslationUtils.sendValueTranslation("Error.Owned", owner, event.getAttacker());

            } else {
                vehiclez.getCarController().stashCar((Player) event.getAttacker(), minecart);
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

        if (!vehiclez.getCarDataPersistence().has(VEHICLE_TYPE, event.getVehicle())) {
            return;
        }

        Minecart vehicle = (Minecart) event.getVehicle();

        if (vehiclez.getCarController().isDriving(player.getName())) {
            vehiclez.getCarController().removeDriver(player.getName());
            TranslationUtils.sendTranslation("Car.EngineStop", player);
        }

        if (Vehiclez.getDefaultConfig().isAutomaticLocking()
                && vehiclez.getCarDataPersistence().has(VEHICLE_OWNER, vehicle)
                && player.getName().equals(vehiclez.getCarDataPersistence().getValue(VEHICLE_OWNER, vehicle))) {
            vehiclez.getCarDataPersistence().setValue(VEHICLE_LOCKED, vehicle, "true");
            TranslationUtils.sendTranslation("Car.CarLocked", player);
        }

        Car car = vehiclez.getCarController().getCar(vehicle.getEntityId());

        // car could be destroyed at this point (i.e. Water damage)
        if (car != null) {
            car.resetSpeed();
            vehiclez.getCarDataPersistence().setValue(VEHICLE_FUEL, vehicle, car.getCurrentFuel().toString());
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
        if (!(event.getVehicle() instanceof Minecart)) {
            return;
        }

        Player player = CarUtils.getPlayerDrivingVehicle(event.getVehicle());

        if (player == null) {
            return;
        }

        if (!vehiclez.getCarController().isDriving(player.getName())) {
            return;
        }

        if (!vehiclez.getConfig().getBoolean("Other.DamageEntities.Enabled")) {
            return;
        }

        if (event.getEntity() instanceof LivingEntity) {
            if (event.getEntity() instanceof Player && vehiclez.getCarController().isDriving(event.getEntity().getName())) {
                return;
            }

            double damage = vehiclez.getConfig().getDouble("Other.DamageEntities.Damage");
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
        if (!(event.getRightClicked() instanceof Vehicle)) {
            return;
        }

        if (!ValidationUtils.isAVehiclezVehicle((Vehicle) event.getRightClicked())) {
            return;
        }

        Minecart vehicle = (Minecart) event.getRightClicked();

        if (!event.getPlayer().isSneaking() || !vehiclez.getConfig().isSneakLockAction()) {
            return;
        }

        if (PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != Vehiclez.getDefaultConfig().getKey()) {
            return;
        }

        if (vehiclez.getCarDataPersistence().has(VEHICLE_OWNER, vehicle)
                && !PermissionUtils.hasStrictPermission(event.getPlayer(), Permissions.BYPASS_OWNER, false)) {
            String owner = vehiclez.getCarDataPersistence().getValue(VEHICLE_OWNER, vehicle);

            if (!owner.equals(event.getPlayer().getName())) {
                TranslationUtils.sendValueTranslation("Error.Owned", owner, event.getPlayer());
                return;
            }
        }

        if (!DelayTasks.getInstance().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        if (vehiclez.getCarDataPersistence().has(VEHICLE_LOCKED, vehicle)) {
            vehiclez.getCarDataPersistence().remove(VEHICLE_LOCKED, vehicle);
            TranslationUtils.sendTranslation("Car.CarUnlocked", event.getPlayer());

        } else {
            vehiclez.getCarDataPersistence().setValue(VEHICLE_LOCKED, vehicle, "true");
            TranslationUtils.sendTranslation("Car.CarLocked", event.getPlayer());
        }
    }
}
