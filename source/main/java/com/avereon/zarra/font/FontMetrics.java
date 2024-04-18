package com.avereon.zarra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;

public class FontMetrics {

	private final Text text;

	@Getter
	private final double ascent;

	@Getter
	private final double descent;

	@Getter
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

	public double computeStringWidth( String text ) {
		this.text.setText( text );
		return this.text.getLayoutBounds().getWidth();
	}

	public double computeStringHeight( String text ) {
		this.text.setText( text );
		return this.text.getLayoutBounds().getHeight();
	}

	@Override
	public String toString() {
		return "FontMetrics [ascent: " + ascent + ", decent: " + descent + ", spacing: " + spacing + "]";
	}

}
