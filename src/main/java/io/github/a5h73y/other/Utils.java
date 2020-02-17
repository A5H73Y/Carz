package io.github.a5h73y.other;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Commands;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.a5h73y.controllers.CarController.DEFAULT_CAR;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_OWNER;
import static io.github.a5h73y.enums.VehicleDetailKey.VEHICLE_TYPE;

/**
 * Various Carz Utility commands.
 */
public class Utils {

    /**
     * Destroy all Minecarts on the server.
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
     * Spawn a vehicle at the given location.
     * If a player is provided, they will be declared the owner
     * @param player
     */
    public static void giveOwnedCar(Player player) {
//        TODO make configurable
//        BlockData data = Bukkit.createBlockData(Material.WET_SPONGE);
//        spawnedCar.setDisplayBlockData(data);
    }

    /**
     * Place an Minecart in the player's inventory with their name on it.
     * @param player
     */
    public static void givePlayerOwnedCar(Player player, String carType) {
        ItemStack itemStack = new ItemStack(Material.MINECART);

        Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_TYPE, itemStack, carType);
        Carz.getInstance().getItemMetaUtils().setValue(VEHICLE_OWNER, itemStack, player.getName());

        setOwnerDisplayName(itemStack, player);

        player.getInventory().addItem(itemStack);
        player.updateInventory();
    }

    /**
     * Place an Minecart in the player's inventory with their name on it.
     * @param player
     */
    public static void givePlayerOwnedCar(Player player) {
        givePlayerOwnedCar(player, DEFAULT_CAR);
    }

    public static void givePlayerOwnedCar(Player player, Vehicle vehicle) {
        ItemStack itemStack = new ItemStack(Material.MINECART);

        ItemMeta itemMeta = itemStack.getItemMeta();
        Carz.getInstance().getItemMetaUtils().transferNamespaceKeyValues(vehicle, itemMeta);
        itemStack.setItemMeta(itemMeta);

        setOwnerDisplayName(itemStack, player);

        player.getInventory().addItem(itemStack);
        player.updateInventory();
    }

    public static void setOwnerDisplayName(ItemStack itemStack, Player player) {
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(TranslationUtils.getTranslation("Car.PlayerCar", false)
                    .replace("%PLAYER%", player.getName()));
            itemStack.setItemMeta(itemMeta);
        }
    }

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
            Material material = Utils.lookupMaterial(rawMaterial);
            if (material != null) {
                validMaterials.add(material);
            } else {
                Utils.log("Material '" + rawMaterial + "' is invalid", 2);
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

        Material material = Utils.lookupMaterial(args[1]);

        if (material == null) {
            player.sendMessage(Carz.getPrefix() + args[1] + " is not a valid Material!");
            return;
        }

        Carz.getInstance().getSettings().addClimbBlock(material);
        player.sendMessage(Carz.getPrefix() + material.name() + " added to ClimbBlocks!");
    }
}
