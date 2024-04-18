package com.avereon.zarra.font;

import javafx.scene.text.Font;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FontMetricsTest {

	@Test
	void testComputeStringWidth() {
		assertThat( new FontMetrics( Font.getDefault() ).computeStringWidth( "Hello World" ) ).isCloseTo( 75, Percentage.withPercentage( 5 ) );
	}

	@Test
	void testComputeStringHeight() {
		assertThat( new FontMetrics( Font.getDefault() ).computeStringHeight( "Hello World" ) ).isCloseTo( 15, Percentage.withPercentage( 5 ) );
	}

}
