package io.github.a5h73y.conversation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.github.a5h73y.Carz;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CreateCarType extends CarzConversation {

	private static final String SESSION_CAR_TYPE = "carType";

	private static final Pattern DOUBLE_PATTERN = Pattern.compile("\\d+\\.\\d+");
	private static final Pattern STRING_PATTERN = Pattern.compile("^[A-Za-z]+$");

	private static List<CarDetailQuestion> carTypeConversion = Arrays.asList(
			new CarDetailQuestion("Car's Start Speed", "StartMaxSpeed", 1.0, DOUBLE_PATTERN),
			new CarDetailQuestion("Car's Max Upgrade Speed", "MaxUpgradeSpeed", 5.0, DOUBLE_PATTERN),
			new CarDetailQuestion("Car's Acceleration", "Acceleration", 1.0, DOUBLE_PATTERN),
			new CarDetailQuestion("Fuel Usage", "FuelUsage", 1.0, DOUBLE_PATTERN),
			new CarDetailQuestion("Fill Material", "FillMaterial", "AIR", STRING_PATTERN)
	);

	public CreateCarType(Conversable player) {
		super(player);
	}

	@Override
	public Prompt getEntryPrompt() {
		return new ChooseCarType();
	}

	private class ChooseCarType extends StringPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return "What would you like this car to be called?";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			List<String> existingCarTypes = Carz.getInstance().getConfig().getStringList("CarTypes");

			if (existingCarTypes.contains(input)) {
				sendErrorMessage(context, "This car type is already taken");
				return this;
			}

			if (STRING_PATTERN.matcher(input).matches()) {
				sendErrorMessage(context, "Invalid Car name");
				return this;
			}
			context.setSessionData(SESSION_CAR_TYPE, input);
			return new ChooseCarDetails();
		}
	}

	private class ChooseCarDetails extends StringPrompt {

		private int progress = 0;
		private Map<String, String> answers = new HashMap<>();

		@Override
		public String getPromptText(ConversationContext context) {
			CarDetailQuestion question = carTypeConversion.get(progress);
			return ChatColor.LIGHT_PURPLE + " What should the " + question.getTitle() + " be?\n" +
					ChatColor.GREEN + " (default = " + question.getDefaultValue() + ")";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String choice) {
			CarDetailQuestion question = carTypeConversion.get(progress);

			if (!question.matchesExpected(choice)) {
				sendErrorMessage(context, "Invalid value");
				return this;
			}

			if (question.getConfigEntry().equals("FillMaterial")) {
				if (Material.getMaterial(choice) == null) {
					sendErrorMessage(context, "Unknown Material: " + choice.toUpperCase());
					return this;
				}
				choice = choice.toUpperCase();
			}

			answers.put(question.getConfigEntry(), choice);
			context.getForWhom().sendRawMessage("Set " + question.getTitle() + " to " + choice);

			progress++;

			if (progress != carTypeConversion.size()) {
				return this;

			} else {
				String carTypeName = (String) context.getSessionData(SESSION_CAR_TYPE);
				FileConfiguration config = Carz.getInstance().getConfig();

				answers.forEach((configKey, value) -> {
					config.set("CarTypes." + carTypeName + "." + configKey, value);
				});
				Carz.getInstance().saveConfig();
				return Prompt.END_OF_CONVERSATION;
			}
		}
	}

	private static class CarDetailQuestion {

		private String title;
		private String configEntry;
		private String defaultValue;
		private Pattern expectedInput;

		public CarDetailQuestion(String title, String configEntry, String defaultValue, Pattern expectedInput) {
			this.title = title;
			this.configEntry = configEntry;
			this.defaultValue = defaultValue;
			this.expectedInput = expectedInput;
		}

		public CarDetailQuestion(String title, String configEntry, double defaultValue, Pattern expectedInput) {
			this(title, configEntry, String.valueOf(defaultValue), expectedInput);
		}

		public boolean matchesExpected(String input) {
			return expectedInput.matcher(input).matches();
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getConfigEntry() {
			return configEntry;
		}

		public void setConfigEntry(String configEntry) {
			this.configEntry = configEntry;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
}
