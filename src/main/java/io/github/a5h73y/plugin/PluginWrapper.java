package io.github.a5h73y.plugin;

import io.github.a5h73y.Carz;
import io.github.a5h73y.other.PluginUtils;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

/**
 * 3rd party Plugin Wrapper.
 * Created to wrap start-up functionality of the plugins.
 */
public abstract class PluginWrapper {

	protected boolean enabled = false;

	/**
	 * What is the name of the 3rd party plugin.
	 * @return plugin name.
	 */
	public abstract String getPluginName();

	/**
	 * Initialise the startup of the plugin on Construction of object.
	 */
	public PluginWrapper() {
		initialise();
	}

	/**
	 * Flag to indicate if the plugin started correctly.
	 * @return plugin enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Initialise the setup of the 3rd party plugin.
	 */
	protected void initialise() {
		// if the config prevents integration, don't start setup.
		if (!Carz.getInstance().getConfig().getBoolean("Other." + getPluginName() + ".Enabled")) {
			return;
		}

		// try to find the plugin running on the server.
		Plugin externalPlugin = getServer().getPluginManager().getPlugin(getPluginName());

		// if the plugin is found and enabled, allow usage
		// otherwise display error and disable plugin usage.
		if (externalPlugin != null && externalPlugin.isEnabled()) {
			enabled = true;
			PluginUtils.log("[" + getPluginName() + "] Successfully linked. Version: " + externalPlugin.getDescription().getVersion(), 0);

		} else {
			PluginUtils.log("[" + getPluginName() + "] Plugin is missing, disabling config option.", 1);
			Carz.getInstance().getConfig().set("Other." + getPluginName() + ".Enabled", false);
			Carz.getInstance().saveConfig();
		}
	}
}
