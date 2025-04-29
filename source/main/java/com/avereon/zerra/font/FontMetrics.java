package com.avereon.zerra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import lombok.Getter;

public class FontMetrics {

	private final Text text;

	@Getter
	private final double lead;

	@Getter
	private final double ascent;

	@Getter
	private final double descent;

	@Getter
	private final double spacing;

	public FontMetrics( Font font ) {
		text = new Text();
		text.setFont( font );

		text.setBoundsType( TextBoundsType.LOGICAL );
		Bounds bounds = text.getLayoutBounds();

		// Lead will be negative
		lead = -bounds.getMinX();
		// Ascent will be negative
		ascent = -bounds.getMinY();
		// Descent will be negative
		descent = -bounds.getMaxY();
		// Spacing will be positive
		spacing = bounds.getHeight();

		text.setBoundsType( TextBoundsType.VISUAL );
	}

	public Bounds computeStringBounds( String text ) {
		this.text.setText( text );
		return this.text.getLayoutBounds();
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
