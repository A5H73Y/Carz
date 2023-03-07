package io.github.a5h73y.vehiclez.commands;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.other.AbstractPluginReceiver;
import io.github.a5h73y.vehiclez.other.CommandUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Tab auto-completion for Vehiclez commands.
 */
public class VehiclezAutoTabCompleter extends AbstractPluginReceiver implements TabCompleter {

    private static final List<String> BLOCK_TYPES_LIST = Arrays.asList("climb", "speed", "launch", "placeable");

    private static final List<String> QUESTION_LIST = Arrays.asList("confirm", "cancel");

    public VehiclezAutoTabCompleter(final Vehiclez vehiclez) {
        super(vehiclez);
    }

    /**
     * List of commands will be built based on the configuration and player permissions.
     * {@inheritDoc}
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        final Player player = (Player) sender;
        List<String> allowedCommands = new ArrayList<>();
        List<String> filteredCommands = new ArrayList<>();

        if (args.length == 1) {
            allowedCommands = populateMainCommands(player);

        } else if (args.length == 2) {
            allowedCommands = populateChildCommands(args[0].toLowerCase());
        }

        for (String allowedCommand : allowedCommands) {
            if (allowedCommand.startsWith(args[args.length - 1])) {
                filteredCommands.add(allowedCommand);
            }
        }

        return filteredCommands.isEmpty() ? allowedCommands : filteredCommands;
    }

    private List<String> populateMainCommands(Player player) {
        // if they have an outstanding purchase, make those the only options
        if (vehiclez.getEconomyApi().isPurchasing(player)) {
            return QUESTION_LIST;
        }

        return vehiclez.getVehiclezCommands().getCommandUsages().values().stream()
                .filter(commandUsage -> commandUsage.getEnabledConfig() == null
                        || vehiclez.getConfig().getBoolean(commandUsage.getEnabledConfig()))
                .filter(commandUsage -> commandUsage.getPermission() == null
                        || player.hasPermission(commandUsage.getPermission()))
                .map(CommandUsage::getCommand)
                .collect(Collectors.toList());
    }

    private List<String> populateChildCommands(String command) {
        List<String> allowedCommands = new ArrayList<>();

        switch (command) {
            case "add":
            case "remove":
            case "list":
                allowedCommands = BLOCK_TYPES_LIST;
                break;
            case "purchase":
            case "spawn":
                allowedCommands = new ArrayList<>(vehiclez.getCarController().getCarTypes().keySet());
                break;
            default:
                break;
        }

        return allowedCommands;
    }
}
