package io.github.a5h73y.carz.other;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.Commands;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 * Various Carz Utility commands.
 */
public class PluginUtils {

    /**
     * Check to see if a command is disabled from the config
     * This forces the players to use signs instead
     * @param sender
     * @param command
     * @return boolean
     */
    public static boolean commandEnabled(CommandSender sender, Commands command) {
        boolean enabled = Carz.getInstance().getConfig().getBoolean(command.getConfigPath());

        if (!enabled) {
            TranslationUtils.sendTranslation("Error.CommandDisabled", sender);
        }

        return enabled;
    }

    /**
     * Lookup the matching Material.
     * Use the 1.13 API to lookup the Material,
     * It will fall back to XMaterial if it fails to find it
     * @param materialName
     * @return matching Material
     */
    public static Material lookupMaterial(String materialName) {
        materialName = materialName.toUpperCase();
        Material material = Material.getMaterial(materialName);

        if (material == null) {
            XMaterial lookup = XMaterial.fromString(materialName);

            if (lookup != null) {
                material = lookup.parseMaterial();
            }
        }

        return material;
    }

    /**
     * Used for logging plugin events, varying in severity.
     * 0 - Info; 1 - Warn; 2 - Severe.
     * @param message
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
     * @param rawMaterials
     * @return Set<Material>
     */
    public static Set<Material> convertToValidMaterials(List<String> rawMaterials) {
        Set<Material> validMaterials = new HashSet<>();

        for (String rawMaterial : rawMaterials) {
            Material material = lookupMaterial(rawMaterial);
            if (material != null) {
                validMaterials.add(material);
            } else {
                log("Material '" + rawMaterial + "' is invalid", 2);
            }
        }
        return validMaterials;
    }

    /**
     * Add a ClimbBlock to the list
     * @param player
     * @param args
     */
    public static void addClimbBlock(CommandSender player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Carz.getPrefix() + "Invalid syntax: /carz addcb (material)");
            return;
        }

        Material material = lookupMaterial(args[1]);

        if (material == null) {
            player.sendMessage(Carz.getPrefix() + args[1] + " is not a valid Material!");
            return;
        }

        Carz.getInstance().getSettings().addClimbBlock(material);
        player.sendMessage(Carz.getPrefix() + material.name() + " added to ClimbBlocks!");
    }
}
