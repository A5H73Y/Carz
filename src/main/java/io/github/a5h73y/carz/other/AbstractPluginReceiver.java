package io.github.a5h73y.carz.other;

import io.github.a5h73y.carz.Carz;

/**
 * Ensure the concrete class receives an instance of the Carz plugin.
 */
public abstract class AbstractPluginReceiver {

	protected final Carz carz;

	public AbstractPluginReceiver(final Carz carz) {
		this.carz = carz;
	}

}
