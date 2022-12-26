package com.avereon.zarra.font;

import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FontMetricsTest {

	@Test
	void testComputeStringWidth() {
		assertThat( new FontMetrics( Font.getDefault() ).computeStringWidth( "Hello World" ) ).isGreaterThan( 0 );
	}

}
