package me.A5H73Y.Carz.commands;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Commands;
import me.A5H73Y.Carz.enums.Permissions;
import me.A5H73Y.Carz.other.Utils;
import me.A5H73Y.Carz.other.Validation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

            Player player = (Player)sender;

            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (!Utils.commandEnabled(player, Commands.SPAWN))
                        return false;

                    if (!Utils.hasStrictPermission(player, Permissions.ADMIN))
                        return false;

                    Utils.spawnCar(player.getLocation());
                    player.sendMessage(Utils.getTranslation("Spawned"));

                } else if (args[0].equalsIgnoreCase("purchase")) {
                    if (!Utils.commandEnabled(player, Commands.PURCHASE))
                        return false;

                    if (!Validation.canPurchaseCar(player))
                        return false;

                    Utils.givePlayerOwnedCar(player);
                    player.sendMessage(Utils.getTranslation("Purchased"));

                } else if (args[0].equalsIgnoreCase("fuel")) {
                    carz.getFuelController().displayFuelLevel(player);

                } else if (args[0].equalsIgnoreCase("refuel")) {
                    if (!Utils.commandEnabled(player, Commands.REFUEL))
                        return false;

                    if (!Validation.canPurchaseFuel(player))
                        return false;

                    carz.getFuelController().refuel(player.getVehicle().getEntityId());
                    player.sendMessage(Utils.getTranslation("Refuelled"));

                } else if (args[0].equalsIgnoreCase("upgrade")) {
                    if (!Utils.commandEnabled(player, Commands.UPGRADE))
                        return false;

                    if (!Validation.canPurchaseUpgrade(player))
                        return false;

                    carz.getCarController().getUpgradeController().upgradeCarSpeed(player);

                } else if (args[0].equalsIgnoreCase("cmds")) {
                    FileConfiguration config = carz.getConfig();
                    player.sendMessage("== Carz Commands ==");

                    if (config.getBoolean("Fuel.Enable")) {
                        player.sendMessage("/carz fuel");
                        if (config.getBoolean("Command.Refuel")) player.sendMessage("/carz refuel");
                    }
                    if (config.getBoolean("Command.Spawn")) player.sendMessage("/carz spawn"); //TODO if admin
                    if (config.getBoolean("Command.Purchase")) player.sendMessage("/carz purchase");
                    if (config.getBoolean("Command.Upgrade")) player.sendMessage("/carz upgrade");
                } else {
                    player.sendMessage(Utils.getTranslation("Error.UnknownCommand"));
                    player.sendMessage(Utils.getTranslation("Commands"));
                }
            } else {
                player.sendMessage("Plugin proudly created by " + ChatColor.AQUA + "A5H73Y");
                player.sendMessage(Utils.getTranslation("Commands"));
            }
        }
        return false;
    }
}
