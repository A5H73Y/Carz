package io.github.a5h73y.carz.commands;

import static io.github.a5h73y.carz.controllers.CarController.DEFAULT_CAR;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.conversation.CreateCarTypeConversation;
import io.github.a5h73y.carz.enums.Commands;
import io.github.a5h73y.carz.enums.GuiMenu;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.other.CarzHelp;
import io.github.a5h73y.carz.purchases.CarPurchase;
import io.github.a5h73y.carz.purchases.Purchasable;
import io.github.a5h73y.carz.purchases.RefuelPurchase;
import io.github.a5h73y.carz.purchases.UpgradePurchase;
import io.github.a5h73y.carz.utility.CarUtils;
import io.github.a5h73y.carz.utility.PermissionUtils;
import io.github.a5h73y.carz.utility.PluginUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

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

        if (args.length == 0) {
            player.sendMessage(Carz.getPrefix() + "proudly created by " + ChatColor.AQUA + "A5H73Y");
            TranslationUtils.sendTranslation("Help.Commands", player);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "p":
            case "purchase":
                if (!PluginUtils.isCommandEnabled(player, Commands.PURCHASE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseCar(player, args.length > 1 ? args[1] : DEFAULT_CAR)) {
                    return false;
                }

                carz.getEconomyApi().requestPurchase(player, new CarPurchase(args.length > 1 ? args[1] : DEFAULT_CAR));
                break;

            case "u":
            case "upgrade":
                if (!PluginUtils.isCommandEnabled(player, Commands.UPGRADE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseUpgrade(player)) {
                    return false;
                }

                Car upgradeCar = carz.getCarController().getCar((Minecart) player.getVehicle());
                carz.getEconomyApi().requestPurchase(player, new UpgradePurchase(upgradeCar));
                break;

            case "r":
            case "refuel":
                if (!PluginUtils.isCommandEnabled(player, Commands.REFUEL)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseFuel(player)) {
                    return false;
                }

                Car refuelCar = carz.getCarController().getCar((Minecart) player.getVehicle());
                carz.getEconomyApi().requestPurchase(player, new RefuelPurchase(refuelCar));
                break;

            case "f":
            case "fuel":
                carz.getFuelController().displayFuelLevel(player);
                break;

            case "s":
            case "spawn":
                if (!PluginUtils.isCommandEnabled(player, Commands.SPAWN)) {
                    return false;
                }

                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                CarUtils.givePlayerCar(player, args.length > 1 ? args[1] : DEFAULT_CAR);
                TranslationUtils.sendTranslation("Car.Spawned", player);
                break;

            case "d":
            case "details":
                if (!player.isInsideVehicle() && player.getInventory().getItemInMainHand().getType() != Material.MINECART) {
                    TranslationUtils.sendTranslation("Error.NotInCar", player);
                    return false;
                }

                CarUtils.showCarDetails(player, args);
                break;

            case "a":
            case "add":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;

                } else if (!PluginUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                PluginUtils.addBlockType(player, args);
                break;

            case "re":
            case "remove":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;

                } else if (!PluginUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                PluginUtils.removeBlockType(player, args);
                break;

            case "l":
            case "list":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;

                } else if (!PluginUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                PluginUtils.listBlockType(player, args);
                break;

            case "stash":
                carz.getCarController().stashCar(player);
                break;

            case "claim":
                if (!ValidationUtils.canClaimCar(player)) {
                    return false;
                }

                carz.getCarController().claimOwnership(player);
                break;

            case "g":
            case "give":
                if (!PluginUtils.isCommandEnabled(player, Commands.GIVE)) {
                    return false;

                } else if (!PluginUtils.validateArgs(player, args, 2)) {
                    return false;

                } else if (!ValidationUtils.canGiveCar(player, args[1])) {
                    return false;
                }

                carz.getCarController().giveCar(player, Bukkit.getPlayer(args[1]));
                break;

            case "ro":
            case "removeowner":
                if (!ValidationUtils.canRemoveCarOwnership(player)) {
                    return false;
                }

                carz.getCarController().removeOwnership(player);
                break;

            case "ct":
            case "cartype":
            case "createtype":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                new CreateCarTypeConversation(player).begin();
                break;

            case "rt":
            case "removetype":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;

                } else if (!PluginUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                PluginUtils.removeCarType(player, args[1]);
                break;

            case "types":
            case "cartypes":
                if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
                    return false;
                }

                TranslationUtils.sendHeading(TranslationUtils.getTranslation("CarType.Heading", false), player);
                carz.getCarController().getCarTypes().keySet().forEach(player::sendMessage);
                break;

            case "yes":
            case "confirm":
                if (!carz.getEconomyApi().isPurchasing(player)) {
                    TranslationUtils.sendTranslation("Error.NoPurchaseOutstanding", player);
                    return false;
                }

                Purchasable purchasing = carz.getEconomyApi().getPurchasing(player);
                if (carz.getEconomyApi().processPurchase(player, purchasing.getCost())) {
                    purchasing.performPurchase(player);
                    carz.getEconomyApi().removePurchase(player);
                }
                break;

            case "no":
            case "cancel":
                if (!carz.getEconomyApi().isPurchasing(player)) {
                    TranslationUtils.sendTranslation("Error.NoPurchaseOutstanding", player);
                    return false;
                }

                carz.getEconomyApi().removePurchase(player);
                TranslationUtils.sendTranslation("Purchase.Cancelled", player);
                break;

            case "e":
            case "economy":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                carz.getEconomyApi().sendEconomyInformation(player);
                break;

            case "store":
                if (!PluginUtils.isCommandEnabled(player, Commands.STORE)) {
                    return false;
                }

                if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
                    return false;
                }

                carz.getGuiManager().showMenu(player, GuiMenu.CAR_STORE);
                break;

            case "help":
                CarzHelp.lookupCommandHelp(args, player);
                break;

            case "about":
            case "ver":
            case "version":
                player.sendMessage(Carz.getPrefix() + "Server is running Carz " + ChatColor.GRAY
                        + carz.getDescription().getVersion());
                player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y");
                break;

            case "reload":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                carz.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Carz.ConfigReloaded", player);
                break;

            case "cmds":
            case "commands":
                CarzHelp.displayCommands(player);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", player);
        }

        return true;
    }
}
