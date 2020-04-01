package io.github.a5h73y.listeners;

import io.github.a5h73y.Carz;
import io.github.a5h73y.enums.Permissions;
import io.github.a5h73y.model.Car;
import io.github.a5h73y.other.AbstractPluginReceiver;
import io.github.a5h73y.purchases.CarPurchase;
import io.github.a5h73y.purchases.RefuelPurchase;
import io.github.a5h73y.purchases.UpgradePurchase;
import io.github.a5h73y.utility.PermissionUtils;
import io.github.a5h73y.utility.StringUtils;
import io.github.a5h73y.utility.TranslationUtils;
import io.github.a5h73y.utility.ValidationUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static io.github.a5h73y.controllers.CarController.DEFAULT_CAR;

public class SignListener extends AbstractPluginReceiver implements Listener {

    public SignListener(Carz carz) {
        super(carz);
    }

    /**
     * When a Sign is created.
     * Check if it was a Car's related Sign.
     * If it's a valid PurchaseType, display a price.
     * @param event
     */
    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        //TODO this needs a major refactor

        if (!event.getLine(0).equalsIgnoreCase("[carz]")) {
            return;
        }

        Player player = event.getPlayer();

        if (!PermissionUtils.hasPermission(player, Permissions.CREATE_SIGN)) {
            breakSignAndCancelEvent(event);
            return;
        }

        // if it's a valid command, break, otherwise it's unknown, so cancel
        switch (event.getLine(1).toLowerCase()) {
            case "purchase":
                if (ValidationUtils.isStringValid(event.getLine(2))
                        && !carz.getCarController().doesCarTypeExist(event.getLine(2))) {
                    player.sendMessage(Carz.getPrefix() + "That doesn't exist.");
                    breakSignAndCancelEvent(event);
                    return;
                }
            case "refuel":
            case "upgrade":
                break;
            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
                player.sendMessage(Carz.getPrefix() + "Valid signs: refuel, purchase, upgrade");
                breakSignAndCancelEvent(event);
                return;
        }

        if (ValidationUtils.isStringValid(event.getLine(3))) {
            if (ValidationUtils.isDouble(event.getLine(3))) {
                event.setLine(3, ChatColor.RED + event.getLine(3));

            } else {
                player.sendMessage(Carz.getPrefix() + "The Cost override must be numeric");
                breakSignAndCancelEvent(event);
                return;
            }
        }

        String title = StringUtils.standardizeText(event.getLine(1));
        player.sendMessage(Carz.getPrefix() + title + " sign created");
        event.setLine(0, Carz.getInstance().getSettings().getSignHeader());
    }

    /**
     * When a Carz sign is broken.
     * Attempt to protect the sign if invalid.
     * @param event
     */
    @EventHandler
    public void onSignBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!(event.getClickedBlock().getBlockData() instanceof Sign)
                && !(event.getClickedBlock().getBlockData() instanceof WallSign)) {
            return;
        }

        if (!Carz.getInstance().getConfig().getBoolean("Other.SignProtection")) {
            return;
        }

        String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase("[carz]")) {
            return;
        }

        if (!PermissionUtils.hasStrictPermission(event.getPlayer(), Permissions.ADMIN)) {
            TranslationUtils.sendTranslation("Error.SignProtected", event.getPlayer());
            event.setCancelled(true);

        } else {
            event.getClickedBlock().breakNaturally();
            TranslationUtils.sendTranslation("Carz.SignRemoved", event.getPlayer());
        }
    }

    /**
     * On Carz sign interaction.
     * Attempt to process a purchase if requested.
     * @param event
     */
    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!(event.getClickedBlock().getBlockData() instanceof Sign)
                && !(event.getClickedBlock().getBlockData() instanceof WallSign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] lines = sign.getLines();

        String signHeader = ChatColor.stripColor(
                StringUtils.colour(Carz.getInstance().getSettings().getSignHeader()));

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(signHeader)) {
            return;
        }

        Player player = event.getPlayer();

        if (carz.getEconomyAPI().isPurchasing(event.getPlayer())) {
            TranslationUtils.sendTranslation("Error.PurchaseOutstanding", player);
            TranslationUtils.sendTranslation("Purchase.Confirm.Purchase", player);
            return;
        }

        event.setCancelled(true);
        boolean hasOverriddenPrice = ValidationUtils.isDouble(lines[3]);

        switch (lines[1].toLowerCase()) {
            case "purchase":
                String carType = ValidationUtils.isStringValid(lines[2]) ? lines[2].toLowerCase() : DEFAULT_CAR;
                if (!ValidationUtils.canPurchaseCar(player, carType, hasOverriddenPrice)) {
                    return;
                }

                carz.getEconomyAPI().requestPurchase(player, new CarPurchase(carType));
                break;

            case "upgrade":
                if (!ValidationUtils.canPurchaseUpgrade(player, hasOverriddenPrice)) {
                    return;
                }

                Car upgradeCar = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getEconomyAPI().requestPurchase(player, new UpgradePurchase(upgradeCar));
                break;

            case "refuel":
                if (!ValidationUtils.canPurchaseFuel(player, hasOverriddenPrice)) {
                    return;
                }

                Car refuelCar = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getEconomyAPI().requestPurchase(player, new RefuelPurchase(refuelCar));
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
                return;
        }

        if (hasOverriddenPrice && carz.getEconomyAPI().isPurchasing(player)) {
            carz.getEconomyAPI().getPurchasing(player).setCostOverride(Double.parseDouble(lines[3]));
        }
    }

    private void breakSignAndCancelEvent(SignChangeEvent event) {
        event.getBlock().breakNaturally();
        event.setCancelled(true);
    }
}
