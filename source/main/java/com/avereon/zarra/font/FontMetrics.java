package com.avereon.zarra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FontMetrics {

	private final Text text;

	private final double ascent;

	private final double descent;

	private final double spacing;

	public FontMetrics( Font font ) {
		text = new Text();
		text.setFont( font );

		// Ascent and decent are inverted
		Bounds bounds = text.getLayoutBounds();
		ascent = -bounds.getMinY();
		descent = -bounds.getMaxY();
		spacing = bounds.getHeight();
	}

	public double getAscent() {
		return ascent;
	}

	public double getDescent() {
		return descent;
	}

	public double getSpacing() {
		return spacing;
	}

	public float computeStringWidth( String text ) {
		this.text.setText( text );
		return (float)this.text.getLayoutBounds().getWidth();
	}

	@Override
	public String toString() {
		return "FontMetrics [ascent: " + ascent + ", decent: " + descent + ", spacing: " + spacing + "]";
	}

}
