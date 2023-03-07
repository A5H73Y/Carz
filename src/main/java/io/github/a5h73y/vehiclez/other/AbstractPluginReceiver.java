package io.github.a5h73y.vehiclez.other;

import io.github.a5h73y.vehiclez.Vehiclez;

/**
 * Ensure the concrete class receives an instance of the Vehiclez plugin.
 */
public abstract class AbstractPluginReceiver {

	protected final Vehiclez vehiclez;

	public AbstractPluginReceiver(final Vehiclez vehiclez) {
		this.vehiclez = vehiclez;
	}

}
