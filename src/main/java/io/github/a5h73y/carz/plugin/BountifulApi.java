package io.github.a5h73y.carz.plugin;

import io.github.a5h73y.carz.Carz;
import org.bukkit.entity.Player;

/**
 * {@link com.connorlinfoot.bountifulapi.BountifulAPI} integration.
 * Allow for Titles and ActionBar messages.
 */
public class BountifulApi extends PluginWrapper {

	@Override
	public String getPluginName() {
		return "BountifulAPI";
	}

	/**
	 * Attempt to send a main title message to the player.
	 * Fallback to sending the player a normal chat message.
	 *
	 * @param player target player
	 * @param message message
	 */
	public void sendTitle(Player player, String message) {
		if (isEnabled()) {
			com.connorlinfoot.bountifulapi.BountifulAPI.sendTitle(player, 10, 40, 10, message, null);
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
		if (isEnabled()) {
			com.connorlinfoot.bountifulapi.BountifulAPI.sendActionBar(player, message, 40);
		} else {
			player.sendMessage(Carz.getPrefix() + message);
		}
	}
}
