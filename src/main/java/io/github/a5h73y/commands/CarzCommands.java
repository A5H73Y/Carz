package io.github.a5h73y.commands;

import io.github.a5h73y.Carz;
import io.github.a5h73y.conversation.CreateCarType;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.other.AbstractPluginReceiver;
import io.github.a5h73y.other.CarzHelp;
import io.github.a5h73y.other.DelayTasks;
import io.github.a5h73y.other.PluginUtils;
import io.github.a5h73y.purchases.CarPurchase;
import io.github.a5h73y.purchases.Purchasable;
import io.github.a5h73y.purchases.RefuelPurchase;
import io.github.a5h73y.purchases.UpgradePurchase;
import io.github.a5h73y.utility.CarUtils;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.TranslationUtils;
import io.github.a5h73y.utility.ValidationUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.a5h73y.controllers.CarController.DEFAULT_CAR;

/**
 * Player-related Carz commands handling.
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

                if (!ValidationUtils.canPurchaseCar(player, args)) {
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

                Car upgradeCar = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getEconomyAPI().requestPurchase(player, new UpgradePurchase(upgradeCar));
                break;

            case "refuel":
                if (!PluginUtils.commandEnabled(player, Commands.REFUEL)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseFuel(player)) {
                    return false;
                }

                Car refuelCar = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getEconomyAPI().requestPurchase(player, new RefuelPurchase(refuelCar));
                break;

            case "stash":
                if (!PluginUtils.commandEnabled(player, Commands.PURCHASE)) {
                    return false;
                }

                carz.getCarController().stashCar(player);
                break;

            case "addcb":
            case "addclimbblock":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                PluginUtils.addClimbBlock(player, args);
                break;

            case "createtype":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                new CreateCarType(player).begin();
                break;

            case "cartypes":
                if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
                    return false;
                }

                carz.getCarController().getCarTypes().keySet().forEach(player::sendMessage);
                break;

            case "details":
                if (carz.getCarController().isDriving(player.getName())) {
                    player.sendMessage(carz.getCarController().getCar(player.getVehicle().getEntityId()).toString());
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

                carz.getEconomyAPI().sendInformation(player);
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
