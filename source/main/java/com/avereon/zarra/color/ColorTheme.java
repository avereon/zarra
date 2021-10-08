package com.avereon.zarra.color;

import javafx.scene.paint.Color;

public class ColorTheme {

	private final Color primary;

	private final Color complement;

	public ColorTheme( Color color ) {
		this.primary = color;
		this.complement = getComplement( color, 180 );
	}

	public Color getPrimary() {
		return primary;
	}

	public Color getComplement() {
		return complement;
	}

	private Color getComplement( Color color, double offset ) {
		double h = color.getHue();
		double s = color.getSaturation();
		double b = color.getBrightness();
		double a = color.getOpacity();

		h += offset;
		h %= 100;
		if( h < 0 ) h += 180;

		return Color.hsb( h, s, b, a );
	}

}
