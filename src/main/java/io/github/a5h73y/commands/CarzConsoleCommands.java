package io.github.a5h73y.commands;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.other.Utils;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Console-related Carz commands handling.
 */
public class CarzConsoleCommands implements CommandExecutor {

    private final Carz carz;

    public CarzConsoleCommands(Carz carz) {
        this.carz = carz;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(Carz.getPrefix() + "Use /carz instead.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(Carz.getPrefix() + "proudly created by " + ChatColor.AQUA + "A5H73Y");
            TranslationUtils.sendTranslation("Carz.ConsoleCommands", sender);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "spawn":
                if (!Utils.commandEnabled(sender, Commands.SPAWN)) {
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(Carz.getPrefix() + "Please specify a player.");
                    return false;
                }

                Player player = Bukkit.getPlayer(args[1]);

                if (player == null) {
                    sender.sendMessage(Carz.getPrefix() + "Unknown player.");
                    return false;
                }

                Utils.spawnCar(player.getLocation());
                TranslationUtils.sendTranslation("Car.Spawned", sender, player);
                break;

            case "addcb":
            case "addclimbblock":
                Utils.addClimbBlock(sender, args);
                break;

            case "destroyall":
                Utils.destroyAllCars();
                TranslationUtils.sendTranslation("TODO ALL CARS DESTROYED", sender);
                break;

            case "reload":
                carz.getSettings().reload();
                TranslationUtils.sendTranslation("Carz.ConfigReloaded", sender);
                break;

            case "cmds":
                sender.sendMessage("/carzc spawn (player)");
                sender.sendMessage("/carzc addCB");
                sender.sendMessage("/carzc destroyall");
                sender.sendMessage("/carzc reload");
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", sender);
                TranslationUtils.sendTranslation("Carz.ConsoleCommands", sender);
        }

        return true;
    }
}
