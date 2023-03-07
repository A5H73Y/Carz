package io.github.a5h73y.vehiclez.gui;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.model.CarDetails;
import io.github.a5h73y.vehiclez.purchases.CarPurchase;
import io.github.a5h73y.vehiclez.utility.StringUtils;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Car Store Gui.
 */
public class CarStore extends AbstractMenu {

	@Override
	public String getTitle() {
		return TranslationUtils.getTranslation("CarStore.Heading", false);
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
		Map<String, CarDetails> results = Vehiclez.getInstance().getCarController().getCarTypes();

		for (Map.Entry<String, CarDetails> carType : results.entrySet()) {
			double cost = Vehiclez.getDefaultConfig().getDouble("CarTypes." + carType.getKey() + ".Cost");
			String displayCost = Vehiclez.getInstance().getEconomyApi().getCurrencyName(cost) + cost;
			CarDetails details = carType.getValue();
			group.addElement(
					new StaticGuiElement('e',
							new ItemStack(Material.MINECART),
							click -> {
								Vehiclez.getInstance().getEconomyApi().requestPurchase(
										player, new CarPurchase(carType.getKey()));
								parent.close();
								return true;
							},

							// the car type heading
							StringUtils.standardizeText(carType.getKey()),

							// maximum speed
							TranslationUtils.getValueTranslation("CarDetails.MaxSpeed",
									String.valueOf(details.getStartMaxSpeed()), false),

							// acceleration
							TranslationUtils.getValueTranslation("CarDetails.Acceleration",
									String.valueOf(details.getAcceleration()), false),

							// fuel usage
							TranslationUtils.getValueTranslation("CarDetails.FuelUsage",
									String.valueOf(details.getFuelUsage()), false),

							// economy cost
							TranslationUtils.getValueTranslation("CarDetails.Cost",
									displayCost, false)
					));
		}
		return group;
	}
}
