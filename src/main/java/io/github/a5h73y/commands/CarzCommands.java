package io.github.a5h73y.commands;

import io.github.a5h73y.Carz;
import io.github.a5h73y.conversation.CreateCarType;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.enums.VehicleDetailKey;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.other.DelayTasks;
import io.github.a5h73y.other.Help;
import io.github.a5h73y.other.Utils;
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
public class CarzCommands implements CommandExecutor {

    private final Carz carz;

    public CarzCommands(Carz carz) {
        this.carz = carz;
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
            case "spawn":
                if (!Utils.commandEnabled(player, Commands.SPAWN)) {
                    return false;
                }

                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                if (!DelayTasks.getInstance().delayPlayer(player, 4)) {
                    return false;
                }

                Utils.givePlayerOwnedCar(player);
                TranslationUtils.sendTranslation("Car.Spawned", player);
                break;

            case "purchase":
                if (!Utils.commandEnabled(player, Commands.PURCHASE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseCar(player, args)) {
                    return false;
                }

                String carType = args.length > 1 ? args[1] : DEFAULT_CAR;
                Utils.givePlayerOwnedCar(player, carType);
                TranslationUtils.sendTranslation("Car.Purchased", player);
                break;

            case "fuel":
                carz.getFuelController().displayFuelLevel(player);
                break;

            case "refuel":
                if (!Utils.commandEnabled(player, Commands.REFUEL)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseFuel(player)) {
                    return false;
                }

                Car car = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getFuelController().refuel(car, player);
                break;

            case "upgrade":
                if (!Utils.commandEnabled(player, Commands.UPGRADE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseUpgrade(player)) {
                    return false;
                }

                carz.getCarController().upgradeCarSpeed(player);
                break;

            case "stash":
                if (!Utils.commandEnabled(player, Commands.PURCHASE)) {
                    return false;
                }

                carz.getCarController().stashCar(player);
                break;

            case "addcb":
            case "addclimbblock":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                Utils.addClimbBlock(player, args);
                break;

            case "createtype":
                new CreateCarType(player).begin();
                break;

            case "cartypes":
                carz.getCarController().getCarTypes().keySet().forEach(player::sendMessage);
                break;

            case "details":
                if (carz.getCarController().isDriving(player.getName())) {
                    player.sendMessage(carz.getCarController().getCar(player.getVehicle().getEntityId()).toString());
                }
                break;

            case "test":
                carz.getItemMetaUtils().setValue(VehicleDetailKey.VEHICLE_TYPE, player.getVehicle(), args[1]);
                player.sendMessage("done: " + args[1]);
                break;

            case "reload":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                carz.getSettings().reload();
                TranslationUtils.sendTranslation("Carz.ConfigReloaded", player);
                break;

            case "cmds":
                Help.displayCommands(player);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Carz.Commands", player);
        }

        return true;
    }
}
