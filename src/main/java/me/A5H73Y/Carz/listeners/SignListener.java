package me.A5H73Y.Carz.listeners;

import me.A5H73Y.Carz.Carz;
import me.A5H73Y.Carz.enums.Permissions;
import me.A5H73Y.Carz.enums.PurchaseType;
import me.A5H73Y.Carz.other.Utils;
import me.A5H73Y.Carz.other.Validation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

public class SignListener implements Listener {

    private final Carz carz;

    public SignListener(Carz carz){
        this.carz = carz;
    }

    private List<String> validCommands = Arrays.asList("refuel", "purchase", "upgrade");


    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if (!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase("[carz]"))
            return;

        Player player = event.getPlayer();

        if (!Utils.hasPermission(player, Permissions.CREATE_SIGN))
            return;

        if (!validCommands.contains(event.getLine(1).toLowerCase())) {
            player.sendMessage(Utils.getTranslation("Error.UnknownSignCommand"));
            player.sendMessage(Utils.getTranslation("Help.SignCommands"));
            event.setCancelled(true);
            return;
        }

        String title = Utils.standardizeText(event.getLine(1));
        event.setLine(1, title);
        player.sendMessage(Carz.getPrefix() + title + " sign created");

        /*
        if (carz.getEconomyController().USE_ECONOMY) {
            String cost = String.valueOf(PurchaseType.valueOf(title).getCost());

            if (!event.getLine(3).isEmpty() && Utils.isNumber(event.getLine(3))) {
                cost = event.getLine(3);
            }
            event.setLine(3, ChatColor.RED + cost);
        }*/
        event.setLine(3, ChatColor.RED + String.valueOf(PurchaseType.valueOf(title).getCost()));
    }

    @EventHandler
    public void onSignBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if ((event.getClickedBlock().getType() != Material.SIGN_POST)
                && (event.getClickedBlock().getType() != Material.WALL_SIGN))
            return;

        if (!Carz.getInstance().getConfig().getBoolean("Carz.SignProtection"))
            return;

        String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase("[carz]"))
            return;

        if (!Utils.hasStrictPermission(event.getPlayer(), Permissions.ADMIN)){
            event.getPlayer().sendMessage(Utils.getTranslation("SignProtected"));
            event.setCancelled(true);
        } else {
            event.getClickedBlock().breakNaturally();
            event.getPlayer().sendMessage("Sign Removed!"); //TODO translation
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if ((event.getClickedBlock().getType() != Material.SIGN_POST)
                && (event.getClickedBlock().getType() != Material.WALL_SIGN))
            return;

        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] lines = sign.getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase("[carz]"))
            return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        switch (lines[1]) {
            case "Refuel":
                if (!Validation.canPurchaseFuel(player))
                    return;

                Vehicle car = (Vehicle) player.getVehicle();
                carz.getFuelController().refuel(car.getEntityId());
                player.sendMessage(Utils.getTranslation("Refuelled"));

                break;
            case "Purchase":
                if (!Validation.canPurchaseCar(player))
                    return;

                Utils.givePlayerOwnedCar(player);
                player.sendMessage(Utils.getTranslation("Purchased"));

                break;
            case "Upgrade":
                if (!Validation.canPurchaseUpgrade(player))
                    return;

                carz.getCarController().getUpgradeController().upgradeCarSpeed(player);

                break;
            default:
                player.sendMessage(Utils.getTranslation("Error.UnknownSignCommand"));
        }
    }
}
