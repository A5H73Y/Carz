package io.github.a5h73y.other;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CarzHelp {

    /**
     * Display Help menu to player.
     * All enabled and permission granted commands are displayed.
     * @param player
     */
    public static void displayCommands(Player player) {
        FileConfiguration config = Carz.getInstance().getConfig();
        player.sendMessage(StringUtils.getStandardHeading("Carz Commands"));

        if (Carz.getInstance().getFuelController().isFuelEnabled()) {
            displayCommandUsage(player, "fuel", "Display the car's fuel");
            if (config.getBoolean(Commands.REFUEL.getConfigPath())) {
                displayCommandUsage(player, "refuel", "Refuel your car");
            }
        }

        if (config.getBoolean(Commands.SPAWN.getConfigPath()) && player.isOp()) {
            displayCommandUsage(player, "spawn", "Spawn a car at your location");
        }

        if (config.getBoolean(Commands.PURCHASE.getConfigPath())) {
            displayCommandUsage(player, "purchase", "Purchase a car");
            displayCommandUsage(player, "stash", "Stash your owned car back into your inventory");
        }

        if (config.getBoolean(Commands.UPGRADE.getConfigPath())) {
            displayCommandUsage(player, "upgrade", "Upgrade your car");
        }

        if (PermissionUtils.hasStrictPermission(player, Permissions.ADMIN, false)) {
            displayCommandUsage(player, "reload", "Reload the config");
            displayCommandUsage(player, "addCB", "Add a ClimbBlock to the list");
        }
    }

    /**
     * Format and display command usage
     * @param player
     * @param title
     * @param description
     */
    private static void displayCommandUsage(Player player, String title, String description) {
        player.sendMessage(ChatColor.DARK_AQUA + "/carz " + ChatColor.AQUA + title
                + ChatColor.BLACK + " : " + ChatColor.WHITE + description);
    }
}
