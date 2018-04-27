package me.A5H73Y.Carz.other;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Commands;
import me.A5H73Y.Carz.enums.Permissions;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

    /**
     * Lookup and return the translation of a key
     * This will add the Carz prefix by default
     * @param string
     * @return translation
     */
    public static String getTranslation(String string) {
        return getTranslation(string, true);
    }

    /**
     * Lookup and return the translation of a key
     * You have to option to specify if the prefix should display
     * @param string
     * @param prefix
     * @return translation
     */
    public static String getTranslation(String string, boolean prefix) {
        if (string == null || string.isEmpty())
            return "Invalid translation.";

        String translated = Carz.getInstance().getConfig().getString("Message." + string);
        translated = translated != null ? colour(translated) : "String not found: " + string;
        return prefix ? Carz.getPrefix().concat(translated) : translated;
    }

    /**
     * Check if the player has the specified permission
     * This will return true if the permissions are disabled
     * @param player
     * @param permission
     * @return boolean
     */
    public static boolean hasPermission(Player player, Permissions permission) {
        if (!Carz.getInstance().getConfig().getBoolean("Other.UsePermissions"))
            return true;

        return hasStrictPermission(player, permission);
    }

    /**
     * Check if the player has the specified permission
     * This will strictly check if the player has permission / op
     * @param player
     * @param permission
     * @return
     */
    public static boolean hasStrictPermission(Player player, Permissions permission) {
        if (player.hasPermission(permission.getPermission())
                || player.hasPermission(Permissions.ALL.getPermission())
                || player.isOp())
            return true;

        player.sendMessage(Utils.getTranslation("Error.NoPermission").replace("%PERMISSION%", permission.getPermission()));
        return false;
    }

    /**
     * Remove all instances of minecarts from across all worlds
     */
    public static void destroyAllCars() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Minecart) {
                    entity.remove();
                }
            }
        }
    }

    /**
     * Spawn a vehicle at the given location
     * If a player is provided, they will be declared the owner
     * @param location
     * @param player
     */
    public static void spawnOwnedCar(Location location, Player player) {
        location.add(0, 1, 0);
        Minecart spawnedCar = location.getWorld().spawn(location, Minecart.class);

        if (player != null) {
            Carz.getInstance().getCarController().declareOwnership(spawnedCar.getEntityId(), player.getName());
        }
    }

    /**
     * Spawn a owner-less vehicle at the given location
     * @param location
     */
    public static void spawnCar(Location location) {
        spawnOwnedCar(location, null);
    }

    /**
     * Place an Minecart in the player's inventory with their name on it
     * @param player
     */
    public static void givePlayerOwnedCar(Player player) {
        ItemStack s = new ItemStack(Material.MINECART);
        ItemMeta m = s.getItemMeta();
        m.setDisplayName(Utils.getTranslation("PlayerCar", false).replace("%PLAYER%", player.getName()));
        s.setItemMeta(m);
        player.getInventory().addItem(s);

        player.updateInventory();
    }

    /**
     * Translate colour codes of provided message
     * @param message
     * @return string
     */
    public static String colour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Format and standardize text to a constant case.
     * Will transform "hElLO" into "Hello"
     *
     * @param text
     * @return standardized input
     */
    public static String standardizeText(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
    }

    /**
     * Return the standardised heading used for Carz
     * @param headingText
     * @return standardised Carz heading
     */
    public static String getStandardHeading(String headingText){
        return "-- " + ChatColor.BLUE + ChatColor.BOLD + headingText + ChatColor.RESET + " --";
    }

    /**
     * Check if the argument is numeric
     * "1" - true, "Hi" - false
     *
     * @param text
     * @return whether the input is numeric
     */
    public static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Check to see if a command is disabled from the config
     * This forces the players to use signs instead
     * @param player
     * @param command
     * @return boolean
     */
    public static boolean commandEnabled(Player player, Commands command) {
        boolean enabled = Carz.getInstance().getConfig().getBoolean(command.getConfigPath());

        if (!enabled)
            player.sendMessage(Utils.getTranslation("Error.CommandDisabled"));

        return enabled;
    }

    /**
     * Made because < 1.8
     * @param player
     * @return
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getItemStackInPlayersHand(Player player) {
        ItemStack stack;

        try {
            stack = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError ex) {
            stack = player.getItemInHand();
        }

        return stack;
    }

    public static Material getMaterialInPlayersHand(Player player) {
        return getItemStackInPlayersHand(player).getType();
    }

    public static void reduceItemStackInPlayersHand(Player player) {
        getItemStackInPlayersHand(player).setAmount(getItemStackInPlayersHand(player).getAmount() - 1);
    }
}
