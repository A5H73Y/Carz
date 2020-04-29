package io.github.a5h73y.carz.conversation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.utility.TranslationUtils;
import io.github.a5h73y.carz.utility.ValidationUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CreateCarTypeConversation extends CarzConversation {

	private static final String SESSION_CAR_TYPE = "carType";

	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
	private static final Pattern STRING_PATTERN = Pattern.compile("^[A-Za-z]+$");

	private static final List<CarDetailQuestion> carTypeConversion = Arrays.asList(
			new CarDetailQuestion("StartMaxSpeed", NUMBER_PATTERN),
			new CarDetailQuestion("MaxUpgradeSpeed", NUMBER_PATTERN),
			new CarDetailQuestion("Acceleration", NUMBER_PATTERN),
			new CarDetailQuestion("FuelUsage", NUMBER_PATTERN),
			new CarDetailQuestion("FillMaterial", STRING_PATTERN)
	);

	public CreateCarTypeConversation(Conversable player) {
		super(player);
	}

	@Override
	public Prompt getEntryPrompt() {
		return new ChooseCarType();
	}

	private class ChooseCarType extends StringPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return TranslationUtils.getTranslation("CarType.Create.Name", false);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			List<String> existingCarTypes = Carz.getInstance().getConfig().getStringList("CarTypes");

			if (!STRING_PATTERN.matcher(input).matches()) {
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

	private class ChooseCarDetails extends StringPrompt {

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

			if (question.getConfigEntry().equals("FillMaterial")) {
				if (Material.getMaterial(choice) == null) {
					sendErrorMessage(context, TranslationUtils
							.getTranslation("Error.UnknownMaterial", false) + choice.toUpperCase());
					return this;
				}
				choice = choice.toUpperCase();
			}

			answers.put(question.getConfigEntry(), choice);
			progress++;

			if (progress != carTypeConversion.size()) {
				return this;

			} else {
				String carTypeName = (String) context.getSessionData(SESSION_CAR_TYPE);
				FileConfiguration config = Carz.getInstance().getConfig();

				answers.forEach((configKey, value) ->
						config.set("CarTypes." + carTypeName + "." + configKey, calculateValue(value)));
				Carz.getInstance().saveConfig();

				if (Carz.getInstance().getEconomyAPI().isEnabled()) {
					return new ChooseCarCost();

				} else {
					Carz.getInstance().getCarController().populateCarTypes();
					context.getForWhom().sendRawMessage("All done, '" + carTypeName + "' created.");
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

			Carz.getInstance().getConfig().set("CarTypes." + carTypeName + ".Cost", input.doubleValue());
			Carz.getInstance().saveConfig();

			Carz.getInstance().getCarController().populateCarTypes();
			context.getForWhom().sendRawMessage(TranslationUtils
					.getTranslation("CarType.Create.Success", false)
					.replace("%VALUE%", carTypeName));
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
