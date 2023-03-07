package io.github.a5h73y.vehiclez.conversation;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.utility.TranslationUtils;
import io.github.a5h73y.vehiclez.utility.ValidationUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CreateCarTypeConversation extends VehiclezConversation {

	private static final String SESSION_CAR_TYPE = "carType";

	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
	private static final Pattern MINECRAFT_MATERIAL_DATA_PATTERN = Pattern.compile("^[A-Za-z_:=\\[\\]\"]+$");

	private static final List<CarDetailQuestion> carTypeConversion = Arrays.asList(
			new CarDetailQuestion("StartMaxSpeed", NUMBER_PATTERN),
			new CarDetailQuestion("MaxUpgradeSpeed", NUMBER_PATTERN),
			new CarDetailQuestion("Acceleration", NUMBER_PATTERN),
			new CarDetailQuestion("FuelUsage", NUMBER_PATTERN),
			new CarDetailQuestion("FillMaterialData", MINECRAFT_MATERIAL_DATA_PATTERN)
	);

	public CreateCarTypeConversation(Conversable player) {
		super(player);
	}

	@Override
	public Prompt getEntryPrompt() {
		return new ChooseCarType();
	}

	private static class ChooseCarType extends StringPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return TranslationUtils.getTranslation("CarType.Create.Name", false);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			List<String> existingCarTypes = Vehiclez.getDefaultConfig().getStringList("CarTypes");

			if (!MINECRAFT_MATERIAL_DATA_PATTERN.matcher(input).matches()) {
				sendErrorMessage(context, TranslationUtils
						.getTranslation("CarType.Error.InvalidName", false));
				return this;
			}

			if (existingCarTypes.contains(input.toLowerCase())) {
				sendErrorMessage(context, TranslationUtils
						.getTranslation("CarType.Error.AlreadyExists", false));
				return this;
			}
			context.setSessionData(SESSION_CAR_TYPE, input.toLowerCase());
			return new ChooseCarDetails();
		}
	}

	private static class ChooseCarDetails extends StringPrompt {

		private int progress = 0;
		private final Map<String, String> answers = new HashMap<>();

		@Override
		public String getPromptText(ConversationContext context) {
			CarDetailQuestion question = carTypeConversion.get(progress);
			return TranslationUtils.getTranslation("CarType.Create." + question.getConfigEntry(), false);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String choice) {
			CarDetailQuestion question = carTypeConversion.get(progress);

			if (!question.matchesExpected(choice)) {
				sendErrorMessage(context, TranslationUtils
						.getTranslation("CarType.Error.InvalidValue", false));
				return this;
			}

			answers.put(question.getConfigEntry(), choice);
			progress++;

			if (progress != carTypeConversion.size()) {
				return this;

			} else {
				String carTypeName = (String) context.getSessionData(SESSION_CAR_TYPE);
				FileConfiguration config = Vehiclez.getDefaultConfig();

				answers.forEach((configKey, value) ->
						config.set("CarTypes." + carTypeName + "." + configKey, calculateValue(value)));
				Vehiclez.getInstance().saveConfig();

				if (Vehiclez.getInstance().getEconomyApi().isEnabled()) {
					return new ChooseCarCost();

				} else {
					Vehiclez.getInstance().getCarController().populateCarTypes();
					context.getForWhom().sendRawMessage(TranslationUtils
							.getValueTranslation("CarType.Create.Success", carTypeName, false));
					return Prompt.END_OF_CONVERSATION;
				}
			}
		}

		private Object calculateValue(String value) {
			if (ValidationUtils.isDouble(value)) {
				return Double.valueOf(value);
			}
			return value;
		}
	}

	private static class ChooseCarCost extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return TranslationUtils.getTranslation("CarType.Create.Cost", false);
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
			String carTypeName = (String) context.getSessionData(SESSION_CAR_TYPE);

			Vehiclez.getDefaultConfig().set("CarTypes." + carTypeName + ".Cost", input.doubleValue());
			Vehiclez.getDefaultConfig().save();

			Vehiclez.getInstance().getCarController().populateCarTypes();
			context.getForWhom().sendRawMessage(TranslationUtils
					.getValueTranslation("CarType.Create.Success", carTypeName, false));
			return Prompt.END_OF_CONVERSATION;
		}
	}

	private static class CarDetailQuestion {

		private final String configEntry;
		private final Pattern expectedInput;

		public CarDetailQuestion(String configEntry, Pattern expectedInput) {
			this.configEntry = configEntry;
			this.expectedInput = expectedInput;
		}

		public boolean matchesExpected(String input) {
			return expectedInput.matcher(input).matches();
		}

		public String getConfigEntry() {
			return configEntry;
		}

	}
}
