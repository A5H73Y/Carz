package me.A5H73Y.Carz.other;

import me.A5H73Y.Carz.Carz;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Help {

    public static void displayCommands(Player player) {
        FileConfiguration config = Carz.getInstance().getConfig();
        player.sendMessage(Utils.getStandardHeading("Carz Commands"));

        if (config.getBoolean("Fuel.Enable")) {
            displayCommandUsage(player, "fuel", "Display the car's fuel");
            if (config.getBoolean("Command.Refuel"))
                displayCommandUsage(player, "refuel", "Refuel your car");
        }
        if (config.getBoolean("Command.Spawn") && player.isOp())
            displayCommandUsage(player, "spawn", "Spawn a car at your location");

        if (config.getBoolean("Command.Purchase"))
            displayCommandUsage(player, "purchase", "Purchase a car");

        if (config.getBoolean("Command.Upgrade"))
            displayCommandUsage(player, "upgrade", "Upgrade your car");
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
