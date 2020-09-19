package io.github.a5h73y.carz.plugin;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.utility.PluginUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * {@link com.connorlinfoot.bountifulapi.BountifulAPI} integration.
 * Allow for Titles and ActionBar messages.
 */
public class BountifulApi extends PluginWrapper {

	private boolean useSpigotMethods;

	@Override
	public String getPluginName() {
		return "BountifulAPI";
	}

	@Override
	protected void initialise() {
		super.initialise();
		useSpigotMethods = PluginUtils.getMinorServerVersion() > 9;
	}

	/**
	 * Attempt to send a main title message to the player.
	 * Fallback to sending the player a normal chat message.
	 *
	 * @param player target player
	 * @param message message
	 */
	public void sendTitle(Player player, String message) {
		if (useSpigotMethods) {
			player.sendTitle(message, "", 10, 40, 10);

		} else if (isEnabled()) {
			BountifulAPI.sendTitle(player, 10, 40, 10, message, null);

		} else {
			player.sendMessage(Carz.getPrefix() + message);
		}
	}

	/**
	 * Attempt to send a action bar message to the player.
	 * Fallback to sending the player a normal chat message.
	 *
	 * @param player target player
	 * @param message message
	 */
	public void sendActionBar(Player player, String message) {
		if (useSpigotMethods) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));

		} else if (isEnabled()) {
			BountifulAPI.sendActionBar(player, message, 40);

		} else {
			player.sendMessage(Carz.getPrefix() + message);
		}
	}
}
