package io.github.a5h73y.commands;

import java.util.ArrayList;
import java.util.List;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.other.AbstractPluginReceiver;
import io.github.a5h73y.utility.PermissionUtils;
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> allowedCommands = new ArrayList<>();
        List<String> filteredCommands = new ArrayList<>();

        if (carz.getFuelController().isFuelEnabled()) {
            allowedCommands.add("fuel");
            if (carz.getConfig().getBoolean(Commands.REFUEL.getConfigPath())) {
                allowedCommands.add("refuel");
            }
        }

        if (carz.getConfig().getBoolean(Commands.PURCHASE.getConfigPath())) {
            allowedCommands.add("purchase");
            allowedCommands.add("stash");
        }

        if (carz.getConfig().getBoolean(Commands.UPGRADE.getConfigPath())) {
            allowedCommands.add("upgrade");
        }

        if (PermissionUtils.hasStrictPermission((Player) sender, Permissions.ADMIN, false)) {
            if (carz.getConfig().getBoolean(Commands.SPAWN.getConfigPath())) {
                allowedCommands.add("spawn");
            }

            allowedCommands.add("reload");
            allowedCommands.add("addCB");
        }

        for (String allowedCommand : allowedCommands) {
            if (allowedCommand.startsWith(args[args.length - 1])) {
                filteredCommands.add(allowedCommand);
            }
        }

        return filteredCommands.isEmpty() ? allowedCommands : filteredCommands;
    }
}
