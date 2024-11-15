package com.avereon.zarra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FontMetricsTest {

	// Load a specific font for these tests
	private final Font font = Font.loadFont( getClass().getResourceAsStream( "WorkSans-Regular.ttf" ), 12 );

	private final FontMetrics metrics = new FontMetrics( font );

	@Test
	void testFontMetrics() {
		assertThat( metrics ).isNotNull();
		assertThat( metrics.getLead() ).isEqualTo( 0.0 );
		assertThat( metrics.getAscent() ).isEqualTo( 13.897000312805176 );
		assertThat( metrics.getDescent() ).isEqualTo( -3.8090009689331055 );
		assertThat( metrics.getSpacing() ).isEqualTo( 17.70600128173828 );
	}

	@Test
	void testComputeStringBounds() {
		Bounds bounds = metrics.computeStringBounds( "Hello World" );
		assertThat( bounds.getMinX() ).isEqualTo( 1.2610000371932983 );
		assertThat( bounds.getMinY() ).isEqualTo( -9.880000114440918 );
		assertThat( bounds.getWidth() ).isEqualTo( 69.2249984741211 );
		assertThat( bounds.getHeight() ).isEqualTo( 10.010000228881836 );
	}

	@Test
	void testComputeStringWidth() {
		assertThat( metrics.computeStringWidth( "Hello World" ) ).isEqualTo( 69.2249984741211 );
	}

	@Test
	void testComputeStringHeight() {
		assertThat( metrics.computeStringHeight( "Hello World" ) ).isEqualTo( 10.010000228881836 );
	}

}
