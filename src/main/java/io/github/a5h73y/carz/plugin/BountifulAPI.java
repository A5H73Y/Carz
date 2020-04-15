package io.github.a5h73y.carz.plugin;

import io.github.a5h73y.carz.Carz;
import org.bukkit.entity.Player;

public class BountifulAPI extends PluginWrapper {

	@Override
	public String getPluginName() {
		return "BountifulAPI";
	}

	public void sendTitle(Player player, String message) {
		if (enabled) {
			com.connorlinfoot.bountifulapi.BountifulAPI.sendTitle(player, 10, 40, 10, message, null);
		} else {
			player.sendMessage(Carz.getPrefix() + message);
		}
	}

	public void sendActionBar(Player player, String message) {
		if (enabled) {
			com.connorlinfoot.bountifulapi.BountifulAPI.sendActionBar(player, message, 40);
		} else {
			player.sendMessage(Carz.getPrefix() + message);
		}
	}
}
