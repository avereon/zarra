package com.avereon.zarra.font;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FontMetricsTest {

	private FontMetrics metrics;

	@BeforeEach
	void setup() throws Exception {
		// Load a specific font for these tests
		try( InputStream fontInputStream = getClass().getResourceAsStream( "/WorkSans-Regular.ttf" ) ) {
			assertThat( fontInputStream ).isNotNull();
			Font font = Font.loadFont( fontInputStream, 12 );
			assertThat( font ).isNotNull();
			metrics = new FontMetrics( font );
		}
	}

	@Test
	void testFontMetrics() {
		assertThat( metrics ).isNotNull();
		assertThat( metrics.getLead() ).isEqualTo( 0.0 );
		assertThat( metrics.getAscent() ).isEqualTo( 11.15999984741211 );
		assertThat( metrics.getDescent() ).isEqualTo( -2.9160003662109375 );
		assertThat( metrics.getSpacing() ).isEqualTo( 14.076000213623047 );
	}

	@Test
	void testComputeStringBounds() {
		Bounds bounds = metrics.computeStringBounds( "Hello World" );
		assertThat( bounds.getMinX() ).isEqualTo( 1.2960000038146973 );
		assertThat( bounds.getMinY() ).isEqualTo( -8.760000228881836 );
		assertThat( bounds.getWidth() ).isEqualTo( 66.7800064086914 );
		assertThat( bounds.getHeight() ).isEqualTo( 8.880000114440918 );
	}

	@Test
	void testComputeStringWidth() {
		assertThat( metrics.computeStringWidth( "Hello World" ) ).isEqualTo( 66.7800064086914 );
	}

	@Test
	void testComputeStringHeight() {
		assertThat( metrics.computeStringHeight( "Hello World" ) ).isEqualTo( 8.880000114440918 );
	}

}
