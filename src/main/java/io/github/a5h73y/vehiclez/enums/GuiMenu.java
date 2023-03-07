package io.github.a5h73y.vehiclez.enums;

import io.github.a5h73y.vehiclez.gui.AbstractMenu;
import io.github.a5h73y.vehiclez.gui.CarStore;

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
