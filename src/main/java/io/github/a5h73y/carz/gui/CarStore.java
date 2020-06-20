package io.github.a5h73y.carz.gui;

import java.util.Map;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.model.CarDetails;
import io.github.a5h73y.carz.purchases.CarPurchase;
import io.github.a5h73y.carz.utility.StringUtils;
import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Car Store Gui.
 */
public class CarStore extends AbstractMenu {

	@Override
	public String getTitle() {
		return "Car Store";
	}

	@Override
	public String[] getGuiSetup() {
		return new String[] {
				TranslationUtils.getTranslation("CarStore.Setup.Line1", false),
				TranslationUtils.getTranslation("CarStore.Setup.Line2", false),
				TranslationUtils.getTranslation("CarStore.Setup.Line3", false)
		};
	}

	@Override
	public GuiElementGroup getGroupContent(InventoryGui parent, Player player) {
		GuiElementGroup group = new GuiElementGroup('g');

		for (Map.Entry<String, CarDetails> carType : Carz.getInstance().getCarController().getCarTypes().entrySet()) {
			double cost = Carz.getDefaultConfig().getDouble("CarTypes." + carType.getKey() + ".Cost");
			String displayCost = Carz.getInstance().getEconomyAPI().getCurrencyName(cost) + cost;
			group.addElement(
					new StaticGuiElement('e',
							new ItemStack(Material.MINECART),
							click -> {
								Carz.getInstance().getEconomyAPI()
										.requestPurchase(player, new CarPurchase(carType.getKey()));
								parent.close();
								return true;
							},

							// the car type heading
							StringUtils.standardizeText(carType.getKey()),

							// maximum speed
							TranslationUtils.getValueTranslation("CarDetails.MaxSpeed",
									String.valueOf(carType.getValue().getStartMaxSpeed()), false),

							// acceleration
							TranslationUtils.getValueTranslation("CarDetails.Acceleration",
									String.valueOf(carType.getValue().getAcceleration()), false),

							// fuel usage
							TranslationUtils.getValueTranslation("CarDetails.FuelUsage",
									String.valueOf(carType.getValue().getAcceleration()), false),

							// economy cost
							TranslationUtils.getValueTranslation("CarDetails.Cost", displayCost, false)
					));
		}
		return group;
	}
}
