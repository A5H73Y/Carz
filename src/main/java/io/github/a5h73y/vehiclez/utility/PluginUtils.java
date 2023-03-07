package io.github.a5h73y.vehiclez.utility;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.configuration.impl.BlocksConfig;
import io.github.a5h73y.vehiclez.controllers.CarController;
import io.github.a5h73y.vehiclez.enums.BlockType;
import io.github.a5h73y.vehiclez.enums.Commands;
import io.github.a5h73y.vehiclez.enums.ConfigType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
    public static boolean isCommandEnabled(CommandSender sender, Commands command) {
        boolean enabled = Vehiclez.getDefaultConfig().getBoolean(command.getConfigPath());

        if (!enabled) {
            TranslationUtils.sendTranslation("Error.CommandDisabled", sender);
        }

        return enabled;
    }

    /**
     * Validate the length of the arguments before allowing it to be processed further.
     *
     * @param sender command sender
     * @param args command arguments
     * @param required required args length
     * @return whether the arguments match the criteria
     */
    public static boolean validateArgs(CommandSender sender, String[] args, int required) {
        return validateArgs(sender, args, required, required);
    }

    /**
     * Validate the range of the arguments before allowing it to be processed further.
     *
     * @param sender command sender
     * @param args command arguments
     * @param minimum minimum args length
     * @param maximum maximum args length
     * @return whether the arguments match the criteria
     */
    public static boolean validateArgs(CommandSender sender, String[] args, int minimum, int maximum) {
        if (args.length > maximum) {
            TranslationUtils.sendValueTranslation("Error.TooMany", String.valueOf(maximum), sender);
            TranslationUtils.sendValueTranslation("Help.Command", args[0].toLowerCase(), sender);
            return false;

        } else if (args.length < minimum) {
            TranslationUtils.sendValueTranslation("Error.TooLittle", String.valueOf(minimum), sender);
            TranslationUtils.sendValueTranslation("Help.Command", args[0].toLowerCase(), sender);
            return false;
        }
        return true;
    }

    /**
     * Log plugin events, varying in severity.
     * 0 - Info; 1 - Warn; 2 - Severe.
     *
     * @param message log message
     * @param severity (0 - 2)
     */
    public static void log(String message, int severity) {
        switch (severity) {
            case 1:
                Vehiclez.getInstance().getLogger().warning(message);
                break;
            case 2:
                Vehiclez.getInstance().getLogger().severe("! " + message);
                break;
            case 0:
            default:
                Vehiclez.getInstance().getLogger().info(message);
                break;
        }
    }

    /**
     * Log plugin info message.
     *
     * @param message log message
     */
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
     * Add a new Material to a Block Type list.
     *
     * @param player requesting player
     * @param args command arguments
     */
    public static void addBlockType(CommandSender player, String[] args) {
        BlockType chosenType;

        try {
            chosenType = BlockType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            TranslationUtils.sendTranslation("Error.BlockTypes.Invalid", player);
            return;
        }

        Material material = Material.getMaterial(args[2].toUpperCase());

        if (material == null) {
            TranslationUtils.sendValueTranslation("Error.UnknownMaterial", args[2], player);
            return;
        }

        String chosenTypeName = StringUtils.standardizeText(chosenType.name());
        BlocksConfig config = (BlocksConfig) Vehiclez.getConfig(ConfigType.BLOCKS);

        if (config.alreadyExists(chosenType, material)) {
            player.sendMessage(TranslationUtils.getTranslation("Error.BlockTypes.AlreadyExists")
                    .replace("%MATERIAL%", material.name())
                    .replace("%TYPE%", chosenTypeName));
            return;
        }

        if (chosenType.isHasAmount()) {
            if (args.length != 4) {
                TranslationUtils.sendValueTranslation("Error.BlockTypes.SpecifyAmount",
                        chosenTypeName.toLowerCase(), player);
                return;
            }

            if (!ValidationUtils.isDouble(args[3])) {
                TranslationUtils.sendValueTranslation("Error.InvalidNumber", args[3], player);
                return;
            }

            double amount = Double.parseDouble(args[3]);

            if (amount < 0 || amount > 100) {
                player.sendMessage(Vehiclez.getPrefix() + "Invalid Amount.");
                player.sendMessage(Vehiclez.getPrefix() + "If you are sure this is what you want, "
                        + "edit the blocks.yml file manually.");
                return;
            }

            // launch blocks are divided because the best numbers are from 0.1 to 1.0, anything more can be manually adjusted
            if (chosenTypeName.toLowerCase().equals("launch")) {
                amount /= 100;
            }

            config.setBlock(chosenType, material, amount);
            player.sendMessage(TranslationUtils.getTranslation("BlockTypes.Added.Amount")
                    .replace("%MATERIAL%", material.name())
                    .replace("%TYPE%", chosenTypeName)
                    .replace("%AMOUNT%", String.valueOf(amount)));

        } else {
            config.addBlock(chosenType, material);
            player.sendMessage(TranslationUtils.getTranslation("BlockTypes.Added.List")
                    .replace("%MATERIAL%", material.name())
                    .replace("%TYPE%", chosenTypeName));
        }
    }

    /**
     * Remove a Material from a Block Type list.
     *
     * @param player requesting player
     * @param args command arguments
     */
    public static void removeBlockType(CommandSender player, String[] args) {
        BlockType chosenType;

        try {
            chosenType = BlockType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            TranslationUtils.sendTranslation("Error.BlockTypes.Invalid", player);
            return;
        }

        Material material = Material.getMaterial(args[2].toUpperCase());

        if (material == null) {
            TranslationUtils.sendValueTranslation("Error.UnknownMaterial", args[2], player);
            return;
        }

        String chosenTypeName = StringUtils.standardizeText(chosenType.name());
        BlocksConfig config = (BlocksConfig) Vehiclez.getConfig(ConfigType.BLOCKS);

        if (!config.alreadyExists(chosenType, material)) {
            player.sendMessage(material.name() + " isn't a " + chosenTypeName + " block.");
            return;
        }

        if (chosenType.isHasAmount()) {
            config.setBlock(chosenType, material, null);

        } else {
            config.removeBlock(chosenType, material);
        }

        player.sendMessage(TranslationUtils.getTranslation("BlockTypes.Removed")
                .replace("%MATERIAL%", material.name())
                .replace("%TYPE%", chosenTypeName));
    }

    /**
     * List out the contents of the Block Type.
     *
     * @param player requesting player
     * @param args command arguments
     */
    public static void listBlockType(Player player, String[] args) {
        BlockType chosenType;

        try {
            chosenType = BlockType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            TranslationUtils.sendTranslation("Error.BlockTypes.Invalid", player);
            return;
        }

        BlocksConfig config = (BlocksConfig) Vehiclez.getConfig(ConfigType.BLOCKS);
        List<String> rawMaterials = new ArrayList<>();

        if (chosenType.isHasAmount()) {
            ConfigurationSection typeSection = config.getConfigurationSection(chosenType.getConfigPath());
            rawMaterials = typeSection != null ? new ArrayList<>(typeSection.getKeys(false)) : rawMaterials;
        } else {
            rawMaterials = config.getStringList(chosenType.getConfigPath());
        }

        TranslationUtils.sendHeading(StringUtils.standardizeText(chosenType.name()), player);
        if (rawMaterials.isEmpty()) {
            player.sendMessage("No results found.");
        } else {
            for (String rawMaterial : rawMaterials) {
                player.sendMessage("* " + rawMaterial);
            }
        }
    }

    /**
     * Remove a Car Type from available car types.
     * Utility method to avoid manual config changes.
     *
     * @param player requesting person
     * @param carTypeName requested car type to delete
     */
    public static void removeCarType(CommandSender player, String carTypeName) {
        if (!Vehiclez.getInstance().getCarController().doesCarTypeExist(carTypeName)
                || CarController.DEFAULT_CAR.equals(carTypeName.toLowerCase())) {
            TranslationUtils.sendTranslation("Error.UnknownCarType", player);
            return;
        }

        Vehiclez.getDefaultConfig().set("CarTypes." + carTypeName.toLowerCase(), null);
        Vehiclez.getDefaultConfig().save();

        Vehiclez.getInstance().getCarController().populateCarTypes();
        TranslationUtils.sendValueTranslation("CarType.Removed", carTypeName, player);
    }

    /**
     * Get the Server's minor version.
     * Will strip the Bukkit version to just the distinguishable version (14, 15, etc.)
     *
     * @return server version
     */
    public static int getMinorServerVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1];
        return Integer.parseInt(version);
    }
}
