package me.A5H73Y.Carz.controllers;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.other.Utils;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class UpgradeController {

    private final Map<Integer, Double> carSpeed = new HashMap<>();

    /**
     * Check to see if the entity ID has been registered as a car
     * A car will have a default upgrade of start speed
     * @param carID
     * @return
     */
    public boolean isCarRegistered(Integer carID) {
        return carSpeed.containsKey(carID);
    }

    public Double getCarSpeed(Integer carID) {
        return carSpeed.get(carID);
    }

    /**
     * Set the vehicles speed to the start speed
     * @param carID
     */
    public void setDefaultCarSpeed(Integer carID) {
        carSpeed.put(carID, Carz.getInstance().getSettings().getStartSpeed());
    }

    /**
     * Upgrade the vehicle the player is in
     * Validation is done before this stage, in case this needs to be called regardless
     * @param player
     */
    public void upgradeCarSpeed(Player player) {
        int carId = player.getVehicle().getEntityId();
        upgradeCarSpeed(carId);
        player.playEffect(player.getLocation(), Effect.ZOMBIE_CHEW_WOODEN_DOOR, null);
        player.sendMessage(Utils.getTranslation("UpgradeSpeed")
                .replace("%SPEED%", getCarSpeed(carId).toString()));
    }

    /**
     * Apply a speed upgrade to the vehicle
     * The current speed of the car will be increased by the upgrade speed
     * until the current speed reaches the maximum upgrade limit.
     * @param carID
     */
    private void upgradeCarSpeed(int carID) {
        Double currentSpeed = getCarSpeed(carID);
        Double upgradeBy = Carz.getInstance().getSettings().getUpgradeSpeed();
        Double maxSpeed = Carz.getInstance().getSettings().getUpgradeMaxSpeed();

        if ((currentSpeed + upgradeBy) > maxSpeed) //&& !event.getPlayer().hasPermission("Carz.Admin"))
            return;

        carSpeed.put(carID, currentSpeed + upgradeBy);
    }
}
