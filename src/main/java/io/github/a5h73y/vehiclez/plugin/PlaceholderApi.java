package io.github.a5h73y.vehiclez.plugin;

import io.github.a5h73y.vehiclez.Vehiclez;
import io.github.a5h73y.vehiclez.VehiclezPlaceholders;

/**
 * {@link me.clip.placeholderapi.PlaceholderAPI} integration.
 * Allow for usage of Vehiclez placeholders.
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
			new VehiclezPlaceholders(Vehiclez.getInstance()).register();
		}
	}
}
