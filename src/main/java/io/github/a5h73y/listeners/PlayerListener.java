package io.github.a5h73y.listeners;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.other.DelayTasks;
import io.github.a5h73y.other.Utils;
import io.github.a5h73y.other.XMaterial;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.PlayerUtils;
import io.github.a5h73y.utility.TranslationUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final Carz carz;

    public PlayerListener(Carz carz) {
        this.carz = carz;
    }

    /**
     * When the player places a Minecart.
     * Determine if it's an owned Car.
     * @param event
     */
    @EventHandler
    public void onPlaceMinecart(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (PlayerUtils.getMaterialInPlayersHand(event.getPlayer()) != Material.MINECART) {
            return;
        }

        if (event.getClickedBlock().getType() == XMaterial.RAIL.parseMaterial()
                || event.getClickedBlock().getType() == XMaterial.POWERED_RAIL.parseMaterial()
                || event.getClickedBlock().getType() == XMaterial.DETECTOR_RAIL.parseMaterial()) {
            return;
        }

        Player player = event.getPlayer();

        if (!PermissionUtils.hasPermission(player, Permissions.PLACE)) {
            return;
        }

        if (!DelayTasks.getInstance().delayPlayer(player, 3)) {
            return;
        }

        ItemStack carInHand = PlayerUtils.getItemStackInPlayersHand(player);

        if (carInHand.hasItemMeta() && carInHand.getItemMeta().hasDisplayName()) {
            if (carInHand.getItemMeta().getDisplayName().contains(player.getName())) {
                Utils.spawnOwnedCar(event.getClickedBlock().getLocation(), player);
            } else {
                TranslationUtils.sendTranslation("Error.Owned", player);
                return;
            }
        } else {
            Utils.spawnCar(event.getClickedBlock().getLocation());
        }

        PlayerUtils.reduceItemStackInPlayersHand(player);
    }
}
