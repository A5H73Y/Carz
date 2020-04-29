package io.github.a5h73y.carz.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Commands;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 * Plugin related utility methods.
 */
public class PluginUtils {

    /**
     * Check to see if a command is disabled from the config.
     * This forces the players to use signs instead.
     *
     * @param sender target player
     * @param command requested command {@link Commands}
     * @return command enabled
     */
    public static boolean commandEnabled(CommandSender sender, Commands command) {
        boolean enabled = Carz.getInstance().getConfig().getBoolean(command.getConfigPath());

        if (!enabled) {
            TranslationUtils.sendTranslation("Error.CommandDisabled", sender);
        }

        return enabled;
    }

    /**
     * Used for logging plugin events, varying in severity.
     * 0 - Info; 1 - Warn; 2 - Severe.
     *
     * @param message log message
     * @param severity (0 - 2)
     */
    public static void log(String message, int severity) {
        switch (severity) {
            case 1:
                Carz.getInstance().getLogger().warning(message);
                break;
            case 2:
                Carz.getInstance().getLogger().severe("! " + message);
                break;
            case 0:
            default:
                Carz.getInstance().getLogger().info(message);
                break;
        }
    }

    public static void log(String message) {
        log(message, 0);
    }

    /**
     * Convert a list of material names to a unique set of Materials.
     *
     * @param rawMaterials list of material strings
     * @return matching Materials
     */
    public static Set<Material> convertToValidMaterials(Collection<String> rawMaterials) {
        Set<Material> validMaterials = new HashSet<>();

        for (String rawMaterial : rawMaterials) {
            Material material = Material.getMaterial(rawMaterial.toUpperCase());
            if (material != null) {
                validMaterials.add(material);
            } else {
                log("Material '" + rawMaterial + "' is invalid", 2);
            }
        }
        return validMaterials;
    }

    /**
     * Add a ClimbBlock to the configured list.
     *
     * @param player player requesting
     * @param args command arguments
     */
    public static void addClimbBlock(CommandSender player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Carz.getPrefix() + "Invalid syntax: /carz addcb (material)");
            return;
        }

        Material material = Material.getMaterial(args[1].toUpperCase());

        if (material == null) {
            player.sendMessage(Carz.getPrefix() + args[1] + " is not a valid Material!");
            return;
        }

        Carz.getInstance().getSettings().addClimbBlock(material);
        player.sendMessage(Carz.getPrefix() + material.name() + " added to ClimbBlocks!");
    }

    /**
     * Add a speed block to the configured list.
     *
     * @param player player requesting
     * @param args command arguments
     */
    public static void addSpeedBlock(CommandSender player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Carz.getPrefix() + "Invalid syntax: /carz addspeed (material) (speed)");
            return;
        }

        Material material = Material.getMaterial(args[1].toUpperCase());

        if (material == null) {
            player.sendMessage(Carz.getPrefix() + args[1] + " is not a valid Material!");
            return;
        }

        if (!ValidationUtils.isDouble(args[2])) {
            player.sendMessage(Carz.getPrefix() + args[2] + " is not a valid number!");
            return;
        }

        Carz.getInstance().getSettings().addSpeedBlock(material, Double.parseDouble(args[2]));
        player.sendMessage(Carz.getPrefix() + material.name() + " added as a speed block!");
    }
}
