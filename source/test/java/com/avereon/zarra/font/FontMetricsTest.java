package com.avereon.zarra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FontMetricsTest {

	private final FontMetrics metrics = new FontMetrics( Font.getDefault() );

	@Test
	void testFontMetrics() {
		assertThat( metrics ).isNotNull();
		assertThat( metrics.getLead() ).isCloseTo( 0.0, Percentage.withPercentage( 1 ) );
		assertThat( metrics.getAscent() ).isCloseTo( 12.06689453125, Percentage.withPercentage( 1 ) );
		assertThat( metrics.getDescent() ).isCloseTo( -3.06591796875, Percentage.withPercentage( 1 ) );
		assertThat( metrics.getSpacing() ).isCloseTo( 15.1328125, Percentage.withPercentage( 1 ) );
	}

	@Test
	void testComputeStringBounds() {
		Bounds bounds = metrics.computeStringBounds( "Hello World" );
		assertThat( bounds.getMinX() ).isCloseTo( 1.27587890625, Percentage.withPercentage( 1 ) );
		assertThat( bounds.getMinY() ).isCloseTo( -9.876953125, Percentage.withPercentage( 1 ) );
		assertThat( bounds.getWidth() ).isCloseTo( 72.642578125, Percentage.withPercentage( 1 ) );
		assertThat( bounds.getHeight() ).isCloseTo( 10.06103515625, Percentage.withPercentage( 1 ) );
	}

	@Test
	void testComputeStringWidth() {
		assertThat( metrics.computeStringWidth( "Hello World" ) ).isCloseTo( 72.642578125, Percentage.withPercentage( 1 ) );
	}

	@Test
	void testComputeStringHeight() {
		assertThat( metrics.computeStringHeight( "Hello World" ) ).isCloseTo( 10.06103515625, Percentage.withPercentage( 1 ) );
	}

}
