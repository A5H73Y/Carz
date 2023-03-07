package io.github.a5h73y.vehiclez.gui;

import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.enums.GuiMenu;
import io.github.a5h73y.vehiclez.other.AbstractPluginReceiver;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Gui Manager used to open and manage GUIs.
 */
public class VehiclezGuiManager extends AbstractPluginReceiver {

	private static final String VEHICLEZ_TITLE_PREFIX = "Vehiclez - ";

	public VehiclezGuiManager(Vehiclez vehiclez) {
		super(vehiclez);
	}

	/**
	 * Open the specified vehiclez menu for the player, using an enum.
	 *
	 * @param player requesting player
	 * @param menu requested menu
	 */
	public void showMenu(Player player, GuiMenu menu) {
		showMenu(player, menu.getMenu());
	}

	/**
	 * Open the specified vehiclez menu for the player.
	 *
	 * @param player requesting player
	 * @param menu requested menu
	 */
	public void showMenu(Player player, AbstractMenu menu) {
		InventoryGui gui = new InventoryGui(vehiclez, null, VEHICLEZ_TITLE_PREFIX + menu.getTitle(), menu.getGuiSetup());
		Material material = Material.valueOf(TranslationUtils.getTranslation("CarStore.Material.Filler", false));
		gui.setFiller(new ItemStack(material, 1));

		gui.addElement(menu.getGroupContent(gui, player));

		gui.addElement(new GuiPageElement('f', new ItemStack(Material.BOOK),
				GuiPageElement.PageAction.FIRST, "Go to first page (current: %page%)"));
		gui.addElement(new GuiPageElement('p', new ItemStack(Material.ARROW),
				GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
		gui.addElement(new GuiPageElement('n', new ItemStack(Material.ARROW),
				GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));
		gui.addElement(new GuiPageElement('l', new ItemStack(Material.BOOK),
				GuiPageElement.PageAction.LAST, "Go to last page (%pages%)"));

		gui.show(player);
	}

}
