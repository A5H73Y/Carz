package io.github.a5h73y.carz.utility;

import java.util.regex.Pattern;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.enums.ConfigType;
import org.bukkit.command.CommandSender;

import static io.github.a5h73y.carz.utility.StringUtils.colour;

/**
 * Translations related utility methods.
 */
public class TranslationUtils {

	private static final Pattern valuePlaceholder = Pattern.compile("%(?i)value%");

	/**
	 * Get translation of string key.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 * The boolean will determine whether to display the Carz prefix.
	 *
	 * @param translationKey to translate
	 * @param prefix display Carz prefix
	 * @return String of appropriate translation
	 */
	public static String getTranslation(String translationKey, boolean prefix) {
		if (!ValidationUtils.isStringValid(translationKey)) {
			return "Invalid translation.";
		}

		String translated = Carz.getConfig(ConfigType.STRINGS).getString(translationKey);
		translated = translated != null ? colour(translated) : "String not found: " + translationKey;
		return prefix ? Carz.getPrefix().concat(translated) : translated;
	}

	/**
	 * Get translation of string key with prefix.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 *
	 * @param translationKey to translate
	 * @return String of appropriate translation
	 */
	public static String getTranslation(String translationKey) {
		return getTranslation(translationKey, true);
	}

	public static String getValueTranslation(String translationKey, String value, boolean prefix) {
		return valuePlaceholder.matcher(getTranslation(translationKey, prefix))
				.replaceAll(value == null ? "" : value);
	}

	/**
	 * Send the translated message to the player(s).
	 *
	 * @param translationKey to translate
	 * @param prefix display prefix
	 * @param players targets to receive the message
	 */
	public static void sendTranslation(String translationKey, boolean prefix, CommandSender... players) {
		String translation = getTranslation(translationKey, prefix);
		for (CommandSender player : players) {
			player.sendMessage(translation);
		}
	}

	/**
	 * Send the translated message to the player(s) with prefix.
	 *
	 * @param translationKey translationKey to translate
	 * @param players to receive the message
	 */
	public static void sendTranslation(String translationKey, CommandSender... players) {
		sendTranslation(translationKey, true, players);
	}

	/**
	 * Send the translated message to the player(s), replacing a value placeholder.
	 *
	 * @param translationKey to translate
	 * @param value to replace
	 * @param prefix display prefix
	 * @param players targets to receive the message
	 */
	public static void sendValueTranslation(String translationKey, String value, boolean prefix, CommandSender... players) {
		String translation = getValueTranslation(translationKey, value, prefix);
		for (CommandSender player : players) {
			player.sendMessage(translation);
		}
	}

	/**
	 * Send the translated message to the player with a heading template.
	 *
	 * @param message to display
	 * @param player to receive the message
	 */
	public static void sendHeading(String message, CommandSender player) {
		sendValueTranslation("Carz.Heading", message, false, player);
	}
}
