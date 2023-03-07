package io.github.a5h73y.vehiclez.commands;

import static io.github.a5h73y.vehiclez.controllers.CarController.DEFAULT_CAR;

import com.google.gson.GsonBuilder;
import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.conversation.CreateCarTypeConversation;
import io.github.a5h73y.vehiclez.enums.Commands;
import io.github.a5h73y.vehiclez.enums.GuiMenu;
import io.github.a5h73y.vehiclez.enums.Permissions;
import io.github.a5h73y.vehiclez.model.Car;
import io.github.a5h73y.vehiclez.other.AbstractPluginReceiver;
import io.github.a5h73y.vehiclez.other.CommandUsage;
import io.github.a5h73y.vehiclez.purchases.CarPurchase;
import io.github.a5h73y.vehiclez.purchases.Purchasable;
import io.github.a5h73y.vehiclez.purchases.RefuelPurchase;
import io.github.a5h73y.vehiclez.purchases.UpgradePurchase;
import io.github.a5h73y.vehiclez.utility.CarUtils;
import io.github.a5h73y.vehiclez.utility.PermissionUtils;
import io.github.a5h73y.vehiclez.utility.PluginUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import io.github.a5h73y.vehiclez.utility.ValidationUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Player related Vehiclez commands handling.
 */
public class VehiclezCommands extends AbstractPluginReceiver implements CommandExecutor {

    private final Map<String, CommandUsage> commandUsages = new HashMap<>();

    public VehiclezCommands(final Vehiclez vehiclez) {
        super(vehiclez);
        populateCommandUsages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String... args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Vehiclez.getPrefix() + "'/vehiclez' is only available in game.");
            sender.sendMessage(Vehiclez.getPrefix() + "Use '/vehiclezc' for console commands.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Vehiclez.getPrefix() + "proudly created by " + ChatColor.AQUA + "A5H73Y");
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

                vehiclez.getEconomyApi().requestPurchase(player, new CarPurchase(args.length > 1 ? args[1] : DEFAULT_CAR));
                break;

            case "u":
            case "upgrade":
                if (!PluginUtils.isCommandEnabled(player, Commands.UPGRADE)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseUpgrade(player)) {
                    return false;
                }

                Car upgradeCar = vehiclez.getCarController().getCar((Minecart) player.getVehicle());
                vehiclez.getEconomyApi().requestPurchase(player, new UpgradePurchase(upgradeCar));
                break;

            case "r":
            case "refuel":
                if (!PluginUtils.isCommandEnabled(player, Commands.REFUEL)) {
                    return false;
                }

                if (!ValidationUtils.canPurchaseFuel(player)) {
                    return false;
                }

                Car refuelCar = vehiclez.getCarController().getCar((Minecart) player.getVehicle());
                vehiclez.getEconomyApi().requestPurchase(player, new RefuelPurchase(refuelCar));
                break;

            case "f":
            case "fuel":
                vehiclez.getFuelController().displayFuelLevel(player);
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

            case "start":
                PlayerInteractEvent interactEvent = new PlayerInteractEvent(player,
                        Action.RIGHT_CLICK_AIR, null, null, BlockFace.SELF);
                Bukkit.getServer().getPluginManager().callEvent(interactEvent);
                break;

            case "blockdata":
                player.sendMessage(player.getInventory().getItemInMainHand().getType().createBlockData().getAsString());
                player.sendMessage(player.getLocation().getBlock().getBlockData().getAsString());
                player.sendMessage(player.getLocation().subtract(0, 1, 0).getBlock().getBlockData().getAsString());
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
                vehiclez.getCarController().stashCar(player);
                break;

            case "claim":
                if (!ValidationUtils.canClaimCar(player)) {
                    return false;
                }

                vehiclez.getCarController().claimOwnership(player);
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

                vehiclez.getCarController().giveCar(player, Bukkit.getPlayer(args[1]));
                break;

            case "ro":
            case "removeowner":
                if (!ValidationUtils.canRemoveCarOwnership(player)) {
                    return false;
                }

                vehiclez.getCarController().removeOwnership(player);
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
                vehiclez.getCarController().getCarTypes().keySet().forEach(player::sendMessage);
                break;

            case "yes":
            case "confirm":
                if (!vehiclez.getEconomyApi().isPurchasing(player)) {
                    TranslationUtils.sendTranslation("Error.NoPurchaseOutstanding", player);
                    return false;
                }

                Purchasable purchasing = vehiclez.getEconomyApi().getPurchasing(player);
                if (vehiclez.getEconomyApi().processPurchase(player, purchasing.getCost())) {
                    purchasing.performPurchase(player);
                    vehiclez.getEconomyApi().removePurchase(player);
                }
                break;

            case "no":
            case "cancel":
                if (!vehiclez.getEconomyApi().isPurchasing(player)) {
                    TranslationUtils.sendTranslation("Error.NoPurchaseOutstanding", player);
                    return false;
                }

                vehiclez.getEconomyApi().removePurchase(player);
                TranslationUtils.sendTranslation("Purchase.Cancelled", player);
                break;

            case "e":
            case "economy":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                vehiclez.getEconomyApi().sendEconomyInformation(player);
                break;

            case "store":
                if (!PluginUtils.isCommandEnabled(player, Commands.STORE)) {
                    return false;
                }

                if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
                    return false;
                }

                vehiclez.getGuiManager().showMenu(player, GuiMenu.CAR_STORE);
                break;

            case "help":
                lookupCommandHelp(args, player);
                break;

            case "about":
            case "ver":
            case "version":
                player.sendMessage(Vehiclez.getPrefix() + "Server is running Vehiclez " + ChatColor.GRAY
                        + vehiclez.getDescription().getVersion());
                player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y");
                break;

            case "reload":
                if (!PermissionUtils.hasStrictPermission(player, Permissions.ADMIN)) {
                    return false;
                }

                vehiclez.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Vehiclez.ConfigReloaded", player);
                break;

            case "cmds":
            case "commands":
                displayCommands(player);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", player);
        }

        return true;
    }

    /**
     * Display Help Menu to player.
     * Content is populated based on config and permissions.
     *
     * @param player requesting player
     */
    public void displayCommands(Player player) {
        FileConfiguration config = Vehiclez.getDefaultConfig();
        TranslationUtils.sendHeading("Vehiclez Commands", player);

        getCommandUsages().values().stream()
                .filter(commandUsage -> commandUsage.getEnabledConfig() == null
                        || config.getBoolean(commandUsage.getEnabledConfig()))
                .filter(commandUsage -> commandUsage.getPermission() == null
                        || player.hasPermission(commandUsage.getPermission()))
                .forEach(commandUsage -> commandUsage.displayCommandUsage(player));
    }

    /**
     * Lookup and display the syntax and description for each Vehiclez command.
     *
     * @param args
     * @param sender
     */
    public void lookupCommandHelp(String[] args, CommandSender sender) {
        if (args.length == 1) {
            sender.sendMessage(Vehiclez.getPrefix() + "Find helpful information about any Vehiclez command:");
            sender.sendMessage("             /vehiclez help " + ChatColor.AQUA + "(command)");
            return;
        }

        String command = args[1].toLowerCase();

        Optional<CommandUsage> matching = getCommandUsages().values().stream()
                .filter(commandUsage -> commandUsage.getCommand().equals(command))
                .findAny();

        if (matching.isPresent()) {
            matching.get().displayHelpInformation(sender);

        } else {
            sender.sendMessage(Vehiclez.getPrefix() + "Unrecognised command. Please find all available commands using '/vehiclez cmds'");
        }
    }


    public Map<String, CommandUsage> getCommandUsages() {
        return commandUsages;
    }

    private void populateCommandUsages() {
        String json = new BufferedReader(new InputStreamReader(
                vehiclez.getResource("vehiclezCommands.json"), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        Arrays.asList(new GsonBuilder().create().fromJson(json, CommandUsage[].class))
                .forEach(commandUsage -> commandUsages.put(commandUsage.getCommand(), commandUsage));
    }
}
