package me.A5H73Y.Carz.other;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Commands;
import me.A5H73Y.Carz.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Help {

    public static void displayCommands(Player player) {
        FileConfiguration config = Carz.getInstance().getConfig();
        player.sendMessage(Utils.getStandardHeading("Carz Commands"));

        if (Carz.getInstance().getFuelController().isFuelEnabled()) {
            displayCommandUsage(player, "fuel", "Display the car's fuel");
            if (config.getBoolean(Commands.REFUEL.getConfigPath()))
                displayCommandUsage(player, "refuel", "Refuel your car");
        }
        if (config.getBoolean(Commands.SPAWN.getConfigPath()) && player.isOp())
            displayCommandUsage(player, "spawn", "Spawn a car at your location");

        if (config.getBoolean(Commands.PURCHASE.getConfigPath())) {
            displayCommandUsage(player, "purchase", "Purchase a car");
            displayCommandUsage(player, "stash", "Stash your owned car back into your inventory");
        }

        if (config.getBoolean(Commands.UPGRADE.getConfigPath()))
            displayCommandUsage(player, "upgrade", "Upgrade your car");

        if (Utils.hasStrictPermission(player, Permissions.ADMIN)) {
            displayCommandUsage(player, "reload", "Reload the config");
            displayCommandUsage(player, "addCB", "Add a ClimbBlock to the list");
        }
    }

    /**
     * Format and display command usage
     *
     * @param player
     * @param title
     * @param description
     */
    private static void displayCommandUsage(Player player, String title, String description) {
        player.sendMessage(ChatColor.DARK_AQUA + "/carz " + ChatColor.AQUA + title +
                ChatColor.BLACK + " : " + ChatColor.WHITE + description);
    }
}
