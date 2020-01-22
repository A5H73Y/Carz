package io.github.a5h73y.utility;

import io.github.a5h73y.Carz;
import org.bukkit.command.CommandSender;

import static io.github.a5h73y.utility.StringUtils.colour;

public class TranslationUtils {

	/**
	 * Get translation of string key.
	 * The string parameter will be matched to an entry in the Strings.yml
	 * The boolean will determine whether to display the Carz prefix
	 *
	 * @param translationKey to translate
	 * @param prefix display Carz prefix
	 * @return String of appropriate translation
	 */
	public static String getTranslation(String translationKey, boolean prefix) {
		if (!ValidationUtils.isStringValid(translationKey)) {
			return "Invalid translation.";
		}

		String translated = Carz.getInstance().getSettings().getStringsConfig().getString(translationKey);
		translated = translated != null ? colour(translated) : "String not found: " + translationKey;
		return prefix ? Carz.getPrefix().concat(translated) : translated;
	}

	/**
	 * Override method, but with a default of an enabled Carz prefix.
	 *
	 * @param translationKey to translate
	 * @return String of appropriate translation
	 */
	public static String getTranslation(String translationKey) {
		return getTranslation(translationKey, true);
	}

	/**
	 * Send the translated message to the player(s).
	 * @param translationKey to translate
	 * @param players to receive the message
	 */
	public static void sendTranslation(String translationKey, CommandSender... players) {
		String translation = getTranslation(translationKey);
		for (CommandSender player : players) {
			player.sendMessage(translation);
		}
	}
}
