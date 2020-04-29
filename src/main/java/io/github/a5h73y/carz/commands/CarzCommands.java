package io.github.a5h73y.carz.commands;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.conversation.CreateCarTypeConversation;
import io.github.a5h73y.carz.enums.Commands;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.enums.VehicleDetailKey;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.other.CarzHelp;
import io.github.a5h73y.carz.other.DelayTasks;
import io.github.a5h73y.carz.purchases.CarPurchase;
import io.github.a5h73y.carz.purchases.Purchasable;
import io.github.a5h73y.carz.purchases.RefuelPurchase;
import io.github.a5h73y.carz.purchases.UpgradePurchase;
import io.github.a5h73y.carz.utility.CarUtils;
import io.github.a5h73y.carz.utility.PermissionUtils;
import io.github.a5h73y.carz.utility.PluginUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import static io.github.a5h73y.carz.controllers.CarController.DEFAULT_CAR;

/**
 * Player related Carz commands handling.
 */
public class CarzCommands extends AbstractPluginReceiver implements CommandExecutor {

    public CarzCommands(final Carz carz) {
        super(carz);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Carz.getPrefix() + "'/carz' is only available in game.");
            sender.sendMessage(Carz.getPrefix() + "Use '/carzc' for console commands.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(Carz.getPrefix() + "proudly created by " + ChatColor.AQUA + "A5H73Y");
            TranslationUtils.sendTranslation("Carz.Commands", player);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "fuel":
                carz.getFuelController().displayFuelLevel(player);
                break;

            case "claim":
                if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Minecart)
                        || !ValidationUtils.isACarzVehicle((Vehicle) player.getVehicle())) {
                    TranslationUtils.sendTranslation("Error.NotInCar", player);
                    return false;
                }

                if (carz.getItemMetaUtils().has(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle())) {
                    String owner = carz.getItemMetaUtils()
                            .getValue(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle());
                    player.sendMessage(TranslationUtils.getTranslation("Error.Owned")
                            .replace("%PLAYER%", owner));
                    return false;
                }

                carz.getItemMetaUtils().setValue(VehicleDetailKey.VEHICLE_OWNER, player.getVehicle(), player.getName());
                TranslationUtils.sendTranslation("Car.Claimed", player);
                break;

            case "spawn":
                if (!PluginUtils.commandEnabled(player, Commands.SPAWN)) {
                    return false;
                }

                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                if (!DelayTasks.getInstance().delayPlayer(player, 4)) {
                    return false;
                }

                CarUtils.givePlayerCar(player, args.length > 1 ? args[1] : DEFAULT_CAR);
                TranslationUtils.sendTranslation("Car.Spawned", player);
                break;

            case "purchase":
                if (!PluginUtils.commandEnabled(player, Commands.PURCHASE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseCar(player, args.length > 1 ? args[1] : DEFAULT_CAR)) {
                    return false;
                }

                carz.getEconomyAPI().requestPurchase(player, new CarPurchase(args.length > 1 ? args[1] : DEFAULT_CAR));
                break;

            case "upgrade":
                if (!PluginUtils.commandEnabled(player, Commands.UPGRADE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseUpgrade(player)) {
                    return false;
                }

                Car upgradeCar = carz.getCarController().getCar((Minecart) player.getVehicle());
                carz.getEconomyAPI().requestPurchase(player, new UpgradePurchase(upgradeCar));
                break;

            case "refuel":
                if (!PluginUtils.commandEnabled(player, Commands.REFUEL)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseFuel(player)) {
                    return false;
                }

                Car refuelCar = carz.getCarController().getCar((Minecart) player.getVehicle());
                carz.getEconomyAPI().requestPurchase(player, new RefuelPurchase(refuelCar));
                break;

            case "stash":
                if (!PluginUtils.commandEnabled(player, Commands.PURCHASE)) {
                    return false;
                }

                carz.getCarController().stashCar(player);
                break;

            case "addcb":
            case "addclimb":
            case "addclimbblock":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                PluginUtils.addClimbBlock(player, args);
                break;

            case "addsb":
            case "addspeed":
            case "addspeedblock":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                PluginUtils.addSpeedBlock(player, args);
                break;

            case "createtype":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                new CreateCarTypeConversation(player).begin();
                break;

            case "cartypes":
                if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
                    return false;
                }

                TranslationUtils.sendHeading("Car Types", player);
                carz.getCarController().getCarTypes().keySet().forEach(player::sendMessage);
                break;

            case "details":
                if (player.isInsideVehicle()
                        && carz.getCarController().getCar(player.getVehicle().getEntityId()) != null) {
                    TranslationUtils.sendHeading("Car Details", player);
                    player.sendMessage(carz.getCarController().getCar(player.getVehicle().getEntityId()).toString());

                } else if (player.getInventory().getItemInMainHand().getType() == Material.MINECART) {
                    TranslationUtils.sendHeading("Car Details", player);
                    carz.getItemMetaUtils().sendDataDetails(player,
                            player.getInventory().getItemInMainHand().getItemMeta());

                } else {
                    TranslationUtils.sendTranslation("Error.NotInCar", player);
                    return false;
                }

                break;

            case "confirm":
                if (!carz.getEconomyAPI().isPurchasing(player)) {
                    TranslationUtils.sendTranslation("Error.NoPurchaseOutstanding", player);
                    return false;
                }

                Purchasable purchasing = carz.getEconomyAPI().getPurchasing(player);
                if (carz.getEconomyAPI().processPurchase(player, purchasing.getCost())) {
                    purchasing.performPurchase(player);
                    carz.getEconomyAPI().removePurchase(player);
                }
                break;

            case "cancel":
                if (!carz.getEconomyAPI().isPurchasing(player)) {
                    TranslationUtils.sendTranslation("Error.NoPurchaseOutstanding", player);
                    return false;
                }

                carz.getEconomyAPI().removePurchase(player);
                TranslationUtils.sendTranslation("Purchase.Cancelled", player);
                break;

            case "economy":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                carz.getEconomyAPI().sendEconomyInformation(player);
                break;

            case "reload":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                carz.getSettings().reload();
                TranslationUtils.sendTranslation("Carz.ConfigReloaded", player);
                break;

            case "cmds":
                CarzHelp.displayCommands(player);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Carz.Commands", player);
        }

        return true;
    }
}
