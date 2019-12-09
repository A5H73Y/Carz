package io.github.a5h73y.utility;

import org.bukkit.ChatColor;

public class StringUtils {

	/**
	 * Translate colour codes of provided message.
	 * @param message
	 * @return string
	 */
	public static String colour(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * Format and standardize text to a constant case.
	 * Will transform "hElLO" into "Hello"
	 *
	 * @param text
	 * @return standardized input
	 */
	public static String standardizeText(String text) {
		return ValidationUtils.isStringValid(text)
				? text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase())
				: text;
	}

	/**
	 * Return the standardised heading used for Carz
	 * @param headingText
	 * @return standardised Carz heading
	 */
	public static String getStandardHeading(String headingText){
		return "-- " + ChatColor.BLUE + ChatColor.BOLD + headingText + ChatColor.RESET + " --";
	}
}
