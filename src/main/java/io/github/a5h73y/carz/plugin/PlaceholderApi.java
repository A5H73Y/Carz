package io.github.a5h73y.carz.plugin;

import io.github.a5h73y.carz.Carz;
import io.github.a5h73y.carz.CarzPlaceholders;

/**
 * {@link me.clip.placeholderapi.PlaceholderAPI} integration.
 * Allow for usage of Carz placeholders.
 */
public class PlaceholderApi extends PluginWrapper {

	@Override
	public String getPluginName() {
		return "PlaceholderAPI";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (isEnabled()) {
			new CarzPlaceholders(Carz.getInstance()).register();
		}
	}
}
