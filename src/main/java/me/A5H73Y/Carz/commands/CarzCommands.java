package me.A5H73Y.Carz.commands;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Commands;
import me.A5H73Y.Carz.enums.Permissions;
import me.A5H73Y.Carz.other.DelayTasks;
import me.A5H73Y.Carz.other.Help;
import me.A5H73Y.Carz.other.Utils;
import me.A5H73Y.Carz.other.Validation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CarzCommands implements CommandExecutor {

    private final Carz carz;

    public CarzCommands(Carz carz) {
        this.carz = carz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("carz")) {

            if (!(sender instanceof Player)) {
                sender.sendMessage(Carz.getPrefix() + "Commands are only available in game.");
                return false;
            }

            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage(Carz.getPrefix() + "proudly created by " + ChatColor.AQUA + "A5H73Y");
                player.sendMessage(Utils.getTranslation("Commands"));
                return false;
            }

            switch (args[0].toLowerCase()) {
                case "spawn":
                    if (!Utils.commandEnabled(player, Commands.SPAWN))
                        return false;

                    if (!Utils.hasStrictPermission(player, Permissions.ADMIN))
                        return false;

                    if (!DelayTasks.getInstance().delayPlayer(player, 4))
                        return false;

                    Utils.spawnCar(player.getLocation());
                    player.sendMessage(Utils.getTranslation("Spawned"));
                    break;

                case "purchase":
                    if (!Utils.commandEnabled(player, Commands.PURCHASE))
                        return false;

                    if (!Validation.canPurchaseCar(player))
                        return false;

                    Utils.givePlayerOwnedCar(player);
                    player.sendMessage(Utils.getTranslation("Purchased"));
                    break;

                case "fuel":
                    carz.getFuelController().displayFuelLevel(player);
                    break;

                case "refuel":
                    if (!Utils.commandEnabled(player, Commands.REFUEL))
                        return false;

                    if (!Validation.canPurchaseFuel(player))
                        return false;

                    carz.getFuelController().refuel(player.getVehicle().getEntityId(), player);
                    break;

                case "upgrade":
                    if (!Utils.commandEnabled(player, Commands.UPGRADE))
                        return false;

                    if (!Validation.canPurchaseUpgrade(player))
                        return false;

                    carz.getCarController().getUpgradeController().upgradeCarSpeed(player);
                    break;

                case "cmds":
                    Help.displayCommands(player);
                    break;

                default:
                    player.sendMessage(Utils.getTranslation("Error.UnknownCommand"));
                    player.sendMessage(Utils.getTranslation("Commands"));
            }
        }
        return true;
    }
}
