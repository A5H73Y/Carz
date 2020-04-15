package io.github.a5h73y.carz.other;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Commands;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.utility.PermissionUtils;
import io.github.a5h73y.carz.utility.StringUtils;
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

        displayCommandUsage(player, "claim", "Claim an unowned car");
        displayCommandUsage(player, "details", "Display the current car's details");

        if (Carz.getInstance().getFuelController().isFuelEnabled()) {
            displayCommandUsage(player, "fuel", "Display the car's remaining fuel");

            if (config.getBoolean(Commands.REFUEL.getConfigPath())) {
                displayCommandUsage(player, "refuel", "Request to refuel your car");
            }
        }

        if (PermissionUtils.hasStrictPermission(player, Permissions.PURCHASE, false)) {
            if (config.getBoolean(Commands.PURCHASE.getConfigPath())) {
                displayCommandUsage(player, "purchase", "Request to purchase a car");
            }

            displayCommandUsage(player, "stash", "Stash your car back into your inventory");
            displayCommandUsage(player, "cartypes", "Displays all the available car types");
        }

        if (config.getBoolean(Commands.UPGRADE.getConfigPath())
                && PermissionUtils.hasStrictPermission(player, Permissions.UPGRADE, false)) {
            displayCommandUsage(player, "upgrade", "Upgrade your car");
        }

        if (PermissionUtils.hasStrictPermission(player, Permissions.ADMIN, false)) {
            if (config.getBoolean(Commands.SPAWN.getConfigPath())) {
                displayCommandUsage(player, "spawn", "Receive an un-owned car");
            }

            displayCommandUsage(player, "addCB", "Add a ClimbBlock to the list");
            displayCommandUsage(player, "createtype", "Create a new Car Type");
            displayCommandUsage(player, "economy", "View Economy information");
            displayCommandUsage(player, "reload", "Reload the config");
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
