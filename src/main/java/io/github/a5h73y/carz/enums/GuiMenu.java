package io.github.a5h73y.carz.enums;

import io.github.a5h73y.carz.gui.AbstractMenu;
import io.github.a5h73y.carz.gui.CarStore;

public enum GuiMenu {

	CAR_STORE(new CarStore());

	private final AbstractMenu menu;

	GuiMenu(AbstractMenu menu) {
		this.menu = menu;
	}

	public AbstractMenu getMenu() {
		return menu;
	}
}
