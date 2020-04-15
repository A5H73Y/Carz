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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;
        List<String> allowedCommands = new ArrayList<>();
        List<String> filteredCommands = new ArrayList<>();

        allowedCommands.add("cmds");
        allowedCommands.add("claim");
        allowedCommands.add("details");

        if (Carz.getInstance().getFuelController().isFuelEnabled()) {
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
            allowedCommands.add( "upgrade");
        }

        if (PermissionUtils.hasStrictPermission(player, Permissions.ADMIN, false)) {
            allowedCommands.add("addCB");
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
