package io.github.a5h73y.carz.other;

import io.github.a5h73y.carz.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandUsage {

	private String command;

	private String title;

	private String arguments;

	private String example;

	private String description;

	private String permission;

	private String enabledConfig;

	private String consoleSyntax;

	public void displayHelpInformation(CommandSender sender) {
		TranslationUtils.sendHeading(title, sender);
		String commandSyntax = arguments != null ? command + " " + arguments : command;
		sender.sendMessage(TranslationUtils.getValueTranslation("Help.CommandSyntax", commandSyntax, false));
		sender.sendMessage(TranslationUtils.getValueTranslation("Help.CommandExample", example, false));
		TranslationUtils.sendHeading("Description", sender);
		sender.sendMessage(description);
	}

	public void displayCommandUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + "/carz " + ChatColor.AQUA + command +
				(arguments != null ? ChatColor.YELLOW + " " + arguments : "") +
				ChatColor.BLACK + " : " + ChatColor.WHITE + title);
	}

	public String getCommand() {
		return command;
	}

	public String getTitle() {
		return title;
	}

	public String getArguments() {
		return arguments;
	}

	public String getExample() {
		return example;
	}

	public String getDescription() {
		return description;
	}

	public String getPermission() {
		return permission;
	}

	public String getEnabledConfig() {
		return enabledConfig;
	}

	public String getConsoleSyntax() {
		return consoleSyntax;
	}
}
