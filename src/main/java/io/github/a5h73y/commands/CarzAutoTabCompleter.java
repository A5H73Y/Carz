package io.github.a5h73y.commands;

import java.util.ArrayList;
import java.util.List;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.utility.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Tab auto-completion for Carz commands.
 */
public class CarzAutoTabCompleter implements TabCompleter {

    private Carz carz;

    public CarzAutoTabCompleter(Carz instance) {
        this.carz = instance;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> commands = new ArrayList<>();

        if (carz.getFuelController().isFuelEnabled()) {
            commands.add("fuel");
            if (carz.getConfig().getBoolean(Commands.REFUEL.getConfigPath())) {
                commands.add("refuel");
            }
        }

        if (carz.getConfig().getBoolean(Commands.PURCHASE.getConfigPath())) {
            commands.add("purchase");
            commands.add("stash");
        }

        if (carz.getConfig().getBoolean(Commands.UPGRADE.getConfigPath())) {
            commands.add("upgrade");
        }

        if (PermissionUtils.hasStrictPermission((Player) sender, Permissions.ADMIN, false)) {
            if (carz.getConfig().getBoolean(Commands.SPAWN.getConfigPath())) {
                commands.add("spawn");
            }

            commands.add("reload");
            commands.add("addCB");
        }

        return commands;
    }
}
