package io.github.a5h73y.carz.listeners;

import static io.github.a5h73y.carz.controllers.CarController.DEFAULT_CAR;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.GuiMenu;
import io.github.a5h73y.carz.enums.Permissions;
import io.github.a5h73y.carz.model.Car;
import io.github.a5h73y.carz.other.AbstractPluginReceiver;
import io.github.a5h73y.carz.purchases.CarPurchase;
import io.github.a5h73y.carz.purchases.Purchasable;
import io.github.a5h73y.carz.purchases.RefuelPurchase;
import io.github.a5h73y.carz.purchases.UpgradePurchase;
import io.github.a5h73y.carz.utility.PermissionUtils;
import io.github.a5h73y.carz.utility.StringUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Sign Related Events.
 */
public class SignListener extends AbstractPluginReceiver implements Listener {

    public SignListener(Carz carz) {
        super(carz);
    }

    /**
     * When a Sign is created.
     * Check if it was a Car's related Sign.
     * If it's a valid PurchaseType, display a price.
     *
     * @param event {@link SignChangeEvent}
     */
    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if (!Carz.getDefaultConfig().getStrippedSignHeader().equalsIgnoreCase(event.getLine(0))) {
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
                    TranslationUtils.sendTranslation("Error.UnknownCarType", player);
                    breakSignAndCancelEvent(event);
                    return;
                }
                break;
            case "refuel":
            case "upgrade":
            case "store":
                break;
            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
                player.sendMessage(Carz.getPrefix() + "Valid signs: refuel, purchase, upgrade, store");
                breakSignAndCancelEvent(event);
                return;
        }

        if (ValidationUtils.isStringValid(event.getLine(3))) {
            if (ValidationUtils.isDouble(event.getLine(3))) {
                event.setLine(3, ChatColor.RED + event.getLine(3));

            } else {
                TranslationUtils.sendValueTranslation("Error.InvalidNumber", event.getLine(3), player);
                breakSignAndCancelEvent(event);
                return;
            }
        }

        String title = StringUtils.standardizeText(event.getLine(1));
        TranslationUtils.sendValueTranslation("Carz.SignCreated", title, player);
        event.setLine(0, Carz.getDefaultConfig().getSignHeader());
    }

    /**
     * When a Carz sign is broken.
     * Attempt to protect the sign if invalid.
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onSignBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        if (!carz.getConfig().getBoolean("Other.SignProtection")) {
            return;
        }

        String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(Carz.getDefaultConfig().getStrippedSignHeader())) {
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
     *
     * @param event {@link PlayerInteractEvent}
     */
    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] lines = sign.getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(Carz.getDefaultConfig().getStrippedSignHeader())) {
            return;
        }

        Player player = event.getPlayer();

        if (carz.getEconomyApi().isPurchasing(event.getPlayer())) {
            TranslationUtils.sendTranslation("Error.PurchaseOutstanding", player);
            TranslationUtils.sendTranslation("Purchase.Confirm.Purchase", player);
            return;
        }

        event.setCancelled(true);
        String strippedCostOverride = ChatColor.stripColor(lines[3]);
        double costOverride = ValidationUtils.isDouble(strippedCostOverride)
                ? Double.parseDouble(strippedCostOverride) : 0;

        switch (lines[1].toLowerCase()) {
            case "purchase":
                String carType = ValidationUtils.isStringValid(lines[2]) ? lines[2].toLowerCase() : DEFAULT_CAR;
                if (!ValidationUtils.canPurchaseCar(player, carType, costOverride)) {
                    return;
                }

                carz.getEconomyApi().requestPurchase(player,
                        calculateFinalCost(new CarPurchase(carType), costOverride));
                break;

            case "upgrade":
                if (!ValidationUtils.canPurchaseUpgrade(player, costOverride)) {
                    return;
                }

                Car upgradeCar = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getEconomyApi().requestPurchase(player,
                        calculateFinalCost(new UpgradePurchase(upgradeCar), costOverride));
                break;

            case "refuel":
                if (!ValidationUtils.canPurchaseFuel(player, costOverride)) {
                    return;
                }

                Car refuelCar = carz.getCarController().getCar(player.getVehicle().getEntityId());
                carz.getEconomyApi().requestPurchase(player,
                        calculateFinalCost(new RefuelPurchase(refuelCar), costOverride));
                break;

            case "store":
                if (!PermissionUtils.hasPermission(player, Permissions.PURCHASE)) {
                    return;
                }

                carz.getGuiManager().showMenu(player, GuiMenu.CAR_STORE);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
        }
    }

    private Purchasable calculateFinalCost(Purchasable purchasable, double costOverride) {
        purchasable.setCostOverride(costOverride);
        return purchasable;
    }

    private void breakSignAndCancelEvent(SignChangeEvent event) {
        event.getBlock().breakNaturally();
        event.setCancelled(true);
    }
}
