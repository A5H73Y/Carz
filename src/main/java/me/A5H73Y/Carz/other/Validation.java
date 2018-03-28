package me.A5H73Y.Carz.other;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Permissions;
import me.A5H73Y.Carz.enums.PurchaseType;
import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

public class Validation {

    /**
     * Validate that the vehicle is a valid Carz vehicle
     * Checks to see if the vehicle is a Minecart that isn't on rails
     * @param cart
     * @return boolean
     */
    public static boolean isACarzVehicle(Vehicle cart){
        if (!(cart instanceof Minecart))
            return false;

        Material material = cart.getLocation().getBlock().getType();
        return (material != Material.POWERED_RAIL) && (material != Material.RAILS) && (material != Material.DETECTOR_RAIL);
    }

    /**
     * Check to see if the player is currently able to purchase a car
     * This includes checking the permission status
     * @param player
     * @return boolean
     */
    public static boolean canPurchaseCar(Player player) {
        if (player.isInsideVehicle()) {
            player.sendMessage(Utils.getTranslation("Error.InCar"));
            return false;
        }

        if (player.getInventory().contains(Material.MINECART)) {
            player.sendMessage(Utils.getTranslation("Error.HaveCar"));
            return false;
        }

        if (!Utils.hasPermission(player, Permissions.PURCHASE))
            return false;

        return Carz.getInstance().getEconomyController().processPurchase(player, PurchaseType.CAR);
    }

    /**
     * Check to see if the player is currently able to purchase an upgrade
     * This includes checking the permission status
     * @param player
     * @return boolean
     */
    public static boolean canPurchaseUpgrade(Player player) {
        if (!player.isInsideVehicle()) {
            player.sendMessage(Utils.getTranslation("Error.NotInCar"));
            return false;
        }

        if (!isACarzVehicle((Vehicle) player.getVehicle())) {
            return false;
        }

        if (Utils.hasPermission(player, Permissions.UPGRADE))
            return false;

        double currentSpeed = Carz.getInstance().getCarController().getUpgradeController().getCarSpeed(player.getVehicle().getEntityId());

        if (currentSpeed >= Carz.getInstance().getSettings().getUpgradeMaxSpeed()) {
            player.sendMessage(Utils.getTranslation("Error.FullyUpgraded"));
            return false;
        }

        return Carz.getInstance().getEconomyController().processPurchase(player, PurchaseType.UPGRADE);
    }

    /**
     * Check to see if the player is currently able to purchase fuel
     * There will be no permission node to purchase fuel, as this would be silly
     * @param player
     * @return boolean
     */
    public static boolean canPurchaseFuel(Player player) {
        if (!player.isInsideVehicle()) {
            player.sendMessage(Utils.getTranslation("Error.NotInCar"));
            return false;
        }

        if (!isACarzVehicle((Vehicle) player.getVehicle())) {
            return false;
        }

        return Carz.getInstance().getEconomyController().processPurchase(player, PurchaseType.FUEL);
    }
}
