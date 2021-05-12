package io.github.a5h73y.carz.other;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.utility.TranslationUtils;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CarzHelp {

    /**
     * Display Help Menu to player.
     * Content is populated based on config and permissions.
     *
     * @param player requesting player
     */
    public static void displayCommands(Player player) {
        FileConfiguration config = Carz.getDefaultConfig();
        TranslationUtils.sendHeading("Carz Commands", player);

        Carz.getInstance().getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getEnabledConfig() == null
                        || config.getBoolean(commandUsage.getEnabledConfig()))
                .filter(commandUsage -> commandUsage.getPermission() == null
                        || player.hasPermission(commandUsage.getPermission()))
                .forEach(commandUsage -> commandUsage.displayCommandUsage(player));
    }

    /**
     * Lookup and display the syntax and description for each Carz command.
     *
     * @param args
     * @param sender
     */
    public static void lookupCommandHelp(String[] args, CommandSender sender) {
        if (args.length == 1) {
            sender.sendMessage(Carz.getPrefix() + "Find helpful information about any Carz command:");
            sender.sendMessage("             /carz help " + ChatColor.AQUA + "(command)");
            return;
        }

        String command = args[1].toLowerCase();

        Optional<CommandUsage> matching = Carz.getInstance().getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getCommand().equals(command))
                .findAny();

        if (matching.isPresent()) {
            matching.get().displayHelpInformation(sender);

        } else {
            sender.sendMessage(Carz.getPrefix() + "Unrecognised command. Please find all available commands using '/carz cmds'");
        }
    }
}
