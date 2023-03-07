package io.github.a5h73y.vehiclez.commands;

import static io.github.a5h73y.vehiclez.controllers.CarController.DEFAULT_CAR;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.conversation.CreateCarTypeConversation;
import io.github.a5h73y.vehiclez.enums.Commands;
import io.github.a5h73y.vehiclez.other.AbstractPluginReceiver;
import io.github.a5h73y.vehiclez.utility.CarUtils;
import io.github.a5h73y.vehiclez.utility.PluginUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Console related Vehiclez commands handling.
 */
public class VehiclezConsoleCommands extends AbstractPluginReceiver implements CommandExecutor {

    public VehiclezConsoleCommands(final Vehiclez vehiclez) {
        super(vehiclez);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player || !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Vehiclez.getPrefix() + "Use '/vehiclez' for player commands.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(Vehiclez.getPrefix() + "proudly created by " + ChatColor.AQUA + "A5H73Y");
            TranslationUtils.sendTranslation("Vehiclez.ConsoleCommands", sender);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "spawn":
                if (!PluginUtils.isCommandEnabled(sender, Commands.SPAWN)) {
                    return false;
                }

                if (args.length < 2) {
                    TranslationUtils.sendTranslation("Error.SpecifyPlayer", sender);
                    return false;
                }

                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
                    return false;
                }

                CarUtils.givePlayerCar(player, args.length > 2 ? args[2] : DEFAULT_CAR);
                TranslationUtils.sendTranslation("Car.Spawned", sender, player);
                break;

            case "add":
                if (!PluginUtils.validateArgs(sender, args, 3, 4)) {
                    return false;
                }

                PluginUtils.addBlockType(sender, args);
                break;

            case "remove":
                PluginUtils.removeBlockType(sender, args);
                break;

            case "createtype":
                new CreateCarTypeConversation((ConsoleCommandSender) sender).begin();
                break;

            case "removetype":
                if (!PluginUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                PluginUtils.removeCarType(sender, args[1]);
                break;

            case "cartypes":
                TranslationUtils.sendHeading(TranslationUtils.getTranslation("CarType.Heading", false), sender);
                vehiclez.getCarController().getCarTypes().keySet().forEach(sender::sendMessage);
                break;

            case "economy":
                vehiclez.getEconomyApi().sendEconomyInformation(sender);
                break;

            case "destroyall":
                CarUtils.destroyAllCars();
                TranslationUtils.sendTranslation("Vehiclez.CarsDestroyed", sender);
                break;

            case "reload":
                Vehiclez.getInstance().getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Vehiclez.ConfigReloaded", sender);
                break;

            case "cmds":
                sender.sendMessage("/vehiclezc spawn (player)");
                sender.sendMessage("/vehiclezc add (type) (material) [amount]");
                sender.sendMessage("/vehiclezc remove (type) (material)");
                sender.sendMessage("/vehiclezc createtype");
                sender.sendMessage("/vehiclezc removetype");
                sender.sendMessage("/vehiclezc cartypes");
                sender.sendMessage("/vehiclezc economy");
                sender.sendMessage("/vehiclezc destroyall");
                sender.sendMessage("/vehiclezc reload");
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", sender);
                TranslationUtils.sendTranslation("Vehiclez.ConsoleCommands", sender);
        }

        return true;
    }
}
