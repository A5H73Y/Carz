package io.github.a5h73y.carz.commands;

import java.util.ArrayList;
import java.util.List;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Commands;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.utility.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Tab auto-completion for Carz commands.
 */
public class CarzAutoTabCompleter extends AbstractPluginReceiver implements TabCompleter {

    public CarzAutoTabCompleter(final Carz carz) {
        super(carz);
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

        if (args.length > 1) {
            return new ArrayList<>();
        }

        final Player player = (Player) sender;
        List<String> allowedCommands = new ArrayList<>();
        List<String> filteredCommands = new ArrayList<>();

        allowedCommands.add("cmds");
        allowedCommands.add("claim");
        allowedCommands.add("details");

        if (carz.getFuelController().isFuelEnabled()) {
            allowedCommands.add("fuel");

            if (carz.getConfig().getBoolean(Commands.REFUEL.getConfigPath())) {
                allowedCommands.add("refuel");
            }
        }

        if (PermissionUtils.hasStrictPermission(player, Permissions.PURCHASE, false)) {
            allowedCommands.add("stash");
            allowedCommands.add("cartypes");

            if (carz.getConfig().getBoolean(Commands.PURCHASE.getConfigPath())) {
                allowedCommands.add("purchase");
            }
        }

        if (carz.getConfig().getBoolean(Commands.UPGRADE.getConfigPath())
                && PermissionUtils.hasStrictPermission(player, Permissions.UPGRADE, false)) {
            allowedCommands.add("upgrade");
        }

        if (PermissionUtils.hasStrictPermission(player, Permissions.ADMIN, false)) {
            allowedCommands.add("addclimb");
            allowedCommands.add("addspeed");
            allowedCommands.add("removeclimb");
            allowedCommands.add("removespeed");
            allowedCommands.add("createtype");
            allowedCommands.add("economy");
            allowedCommands.add("reload");

            if (carz.getConfig().getBoolean(Commands.SPAWN.getConfigPath())) {
                allowedCommands.add("spawn");
            }
        }

        for (String allowedCommand : allowedCommands) {
            if (allowedCommand.startsWith(args[args.length - 1])) {
                filteredCommands.add(allowedCommand);
            }
        }

        return filteredCommands.isEmpty() ? allowedCommands : filteredCommands;
    }
}
