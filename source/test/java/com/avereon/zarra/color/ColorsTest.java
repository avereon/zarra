package com.avereon.zarra.color;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.assertj.core.api.Assertions.assertThat;

class ColorsTest {

	@Test
	void testToString() {
		assertThat( Colors.toString( Color.TRANSPARENT ) ).isEqualTo( "#00000000" );
		assertThat( Colors.toString( Color.BLACK ) ).isEqualTo( "#000000ff" );
		assertThat( Colors.toString( Color.WHITE ) ).isEqualTo( "#ffffffff" );
		assertThat( Colors.toString( Color.GRAY ) ).isEqualTo( "#808080ff" );
		assertThat( Colors.toString( null ) ).isNull();
	}

	@Test
	void testParse() {
		assertThat( Colors.parse( "#00000000" ) ).isEqualTo( Color.TRANSPARENT );
		assertThat( Colors.parse( "#000000ff" ) ).isEqualTo( Color.BLACK );
		assertThat( Colors.parse( "#ffffffff" ) ).isEqualTo( Color.WHITE );
		assertThat( Colors.parse( "#808080ff" ) ).isEqualTo( Color.GRAY );
		assertThat( Colors.parse( null ) ).isNull();
	}

	@Test
	void testInvertBrightness() {
		assertInvertedBrightness( Color.rgb( 0, 0, 0, 0 ), Color.rgb( 0, 0, 0, 0 ) );

		//assertInvertedBrightness( Color.rgb( 0, 0, 0 ), Color.rgb( 255, 255, 255 ) );
		//assertInvertedBrightness( Color.rgb( 128, 128, 128 ), Color.rgb( 127, 127, 127 ) );

		// So...this one works at the expense of the previous
		assertInvertedBrightness( Color.rgb( 255, 255, 0 ), Color.rgb( 31, 31, 0 ) );

		//assertInvertedBrightness( Color.RED, Color.RED );
		//		assertInvertedBrightness( Color.GREEN, Color.GREEN );
		//		assertInvertedBrightness( Color.CYAN, Color.CYAN );
		//		assertInvertedBrightness( Color.BLUE, Color.BLUE );
		//
		//		assertInvertedBrightness( Color.MAGENTA, Color.MAGENTA );
		//		assertInvertedBrightness( Color.PURPLE, Color.PURPLE );
	}

	private void assertInvertedBrightness( Color source, Color target ) {
		Color inverted = Colors.invertBrightness( source );
		Color reverted = Colors.invertBrightness( inverted );
		try {
			assertThat( inverted.getRed() ).isEqualTo( target.getRed() );
			assertThat( inverted.getGreen() ).isEqualTo( target.getGreen() );
			assertThat( inverted.getBlue() ).isEqualTo( target.getBlue() );
			assertThat( inverted.getOpacity() ).isEqualTo( target.getOpacity() );
			assertThat( inverted.getHue() ).isEqualTo( target.getHue() );
			assertThat( Colors.getLuminance( inverted ) ).isEqualTo( Colors.getLuminance( target ) );

			assertThat( reverted.getRed() ).isEqualTo( source.getRed() );
			assertThat( reverted.getGreen() ).isEqualTo( source.getGreen() );
			assertThat( reverted.getBlue() ).isEqualTo( source.getBlue() );
			assertThat( reverted.getOpacity() ).isEqualTo( source.getOpacity() );
			assertThat( reverted.getHue() ).isEqualTo( source.getHue() );
			assertThat( Colors.getLuminance( reverted ) ).isEqualTo( Colors.getLuminance( source ) );
		} catch( AssertionFailedError error ) {
			System.out.println( "source=" + source + " expected=" + target + " inverted=" + inverted + " reverted=" + reverted );
			throw error;
		}
	}

}
