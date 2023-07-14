package com.avereon.zarra.color;

import javafx.scene.paint.Color;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Disabled;
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
	void testGetBrightness() {
		// Transparent
		assertThat( Colors.getLuminance( Color.color( 0, 0, 0, 0 ) ) ).isEqualTo( 0.0 );
		assertThat( Colors.getLuminance( Color.color( 0.5, 0.5, 0.5, 0 ) ) ).isEqualTo( 0.5, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.color( 1, 1, 1, 0 ) ) ).isEqualTo( 1.0 );

		// Basics
		assertThat( Colors.getLuminance( Color.BLACK ) ).isEqualTo( 0.0 );
		assertThat( Colors.getLuminance( Color.RED ) ).isEqualTo( 0.3 );
		assertThat( Colors.getLuminance( Color.LIME ) ).isEqualTo( 0.59, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.BLUE ) ).isEqualTo( 0.11 );
		assertThat( Colors.getLuminance( Color.CYAN ) ).isEqualTo( 0.7 );
		assertThat( Colors.getLuminance( Color.MAGENTA ) ).isEqualTo( 0.41 );
		assertThat( Colors.getLuminance( Color.YELLOW ) ).isEqualTo( 0.89, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.WHITE ) ).isEqualTo( 1.0 );

		// Grays
		assertThat( Colors.getLuminance( Color.rgb( 127, 127, 127 ) ) ).isEqualTo( 0.4980392158031463 );
		assertThat( Colors.getLuminance( Color.color( 0.5, 0.5, 0.5 ) ) ).isEqualTo( 0.5, Offset.offset( 1e-7 ) );
		assertThat( Colors.getLuminance( Color.rgb( 128, 128, 128 ) ) ).isEqualTo( 0.501960813999176 );
	}

	@Test
	void testSwapColor() {
		Color color = Color.rgb( 32, 64, 128 );

		double sourceLuminance = Colors.getLuminance( color );
		double targetLuminance = Colors.getLuminance( color.invert() );
		assertThat( sourceLuminance ).isEqualTo( 0.24094119071960451 );
		assertThat( targetLuminance ).isEqualTo( 0.759058831334114 );

		// These weights are based on the channel luminance
		double inverseFactor = targetLuminance / sourceLuminance;
		double r = color.getRed() * inverseFactor; // 0.395343141319851
		double g = color.getGreen() * inverseFactor; // 0.790686282639702
		double b = color.getBlue() * inverseFactor; // 1.581372565279404

		System.out.println( "sourceLuminance=" + sourceLuminance + " targetLuminance=" + targetLuminance + " if=" + inverseFactor );

		// In this case the new luminance is 0.759058831334114 (on the high end)
		// This means that we need to find a light blueish with luminance 0.759058831334114
		// Because one of the luminance values is greater than 1 we need to fill in the rest with red and green
		// Blue will account for 0.11 of the new luminance
		// That means that 0.649058831 needs to be accounted for by red and green

		// but with the luminance weights of red(0.3) and green(0.59)

		// red already accounts for 0.395343141319851 * 0.3 = 0.118602942
		// green already accounts for 0.790686282639702 * 0.59 = 0.466504907
		// the remainder of that is 0.649058831 - 0.118602942(red) - 0.466504907(green) = 0.063950982
		// That needs to be split between the colors and added to each channel
		// This formula also predicts that value: 0.043210059 × 0.3 + 0.086420117 × 0.59 = 0.063950887

		// Luminance factor for both red and green together is 0.89
		// n * 0.89[green+blue factors] = 0.649058831
		// n = 0.649058831 / 0.89[green+blue factors]
		// n = 0.729279586
		double remainder = targetLuminance - 0.11;

		// Distribute n between red and green proportionally
		double redFactor = r / (r + g);
		double greenFactor = g / (r + g);
		System.out.println( "rf=" + redFactor + "gf=" + greenFactor );

		// Remove luminance already accounted for by red and green
		remainder -= r * 0.3;
		remainder -= g * 0.59;
		assertThat( remainder ).isEqualTo( 0.06395098218073464 );

		// NEXT Split the remainder along the color channels
		double nr = redFactor * remainder;
		double ng = greenFactor * remainder;
		assertThat( nr + ng ).isEqualTo( remainder );
		System.out.println( "nr=" + nr + "ng=" + ng );

		//double n = remainder / 0.89;
		//assertThat( n ).isEqualTo( 0.07185503615812881 );

		double median = 0.5 * (redFactor + greenFactor);
		double rWeight = redFactor / median;
		double gWeight = greenFactor / median;

		// NOTE R and G already have values, they "just" need to be tweaked
		// r needs 0.043210059 more
		// g needs 0.086420117 more

		//		r = n * rWeight;
		//		g = n * gWeight;

		System.out.println( "rf=" + redFactor + "gf=" + greenFactor + " median=" + median );
		System.out.println( "rw=" + rWeight + "gw=" + gWeight );

		// That should mean that green and blue values of 0.4 / 0.7 should give us the
		// extra 0.4 of luminance

		System.out.println( "color=" + color + " r=" + r + " g=" + g + " b=" + b );
		Color source = Color.rgb( 32, 64, 128 );
		Color target = Color.color( clamp( r ), clamp( g ), clamp( b ), 1 );
		Color target2 = Color.color( 0.395343141319851, 0.790686282639702, 1 );

		// This is close enough pass the test
		Color target3 = Color.color( 0.4385532, 0.8771064, 1 );

		assertThat( Colors.getLuminance( target ) ).isCloseTo( 0.759058831334114, Offset.offset( 1e-7 ) );
	}

	@Test
	void testSwapBrightness() {
		// Transparent
		assertThat( Colors.swapLuminance( Color.color( 0, 0, 0, 0 ) ) ).isEqualTo( Color.color( 1, 1, 1, 0 ) );
		assertThat( Colors.swapLuminance( Color.color( 0.5, 0.5, 0.5, 0 ) ) ).isEqualTo( Color.color( 0.5, 0.5, 0.5, 0 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 1, 1, 0 ) ) ).isEqualTo( Color.color( 0, 0, 0, 0 ) );

		// Basics
		assertThat( Colors.swapLuminance( Color.color( 0, 0, 0 ) ) ).isEqualTo( Color.color( 1, 1, 1 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 0, 0 ) ) ).isEqualTo( Color.color( 1, 0.4 / 0.7, 0.4 / 0.7 ) );
		assertThat( Colors.swapLuminance( Color.color( 0, 1, 0 ) ) ).isEqualTo( Color.color( 0, 0.6949152542372882, 0 ) );
		assertThat( Colors.swapLuminance( Color.color( 0, 0, 1 ) ) ).isEqualTo( Color.color( 0.78 / 0.89, 0.78 / 0.89, 1 ) );
		assertThat( Colors.swapLuminance( Color.color( 0, 1, 1 ) ) ).isEqualTo( Color.color( 0, 0.4285714285714286, 0.4285714285714286 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 0, 1 ) ) ).isEqualTo( Color.color( 1, 0.3050847457627119, 1 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 1, 0 ) ) ).isEqualTo( Color.color( 0.12359550561797754, 0.12359550561797754, 0 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 1, 1 ) ) ).isEqualTo( Color.color( 0, 0, 0 ) );

		// Grays
		assertThat( Colors.swapLuminance( Color.rgb( 127, 127, 127 ) ) ).isEqualTo( Color.color( 0.5019607543945312, 0.5019607543945312, 0.5019607543945312 ) );
		assertThat( Colors.swapLuminance( Color.color( 0.5, 0.5, 0.5 ) ) ).isEqualTo( Color.color( 0.5, 0.5, 0.5 ) );
		assertThat( Colors.swapLuminance( Color.rgb( 128, 128, 128 ) ) ).isEqualTo( Color.color( 0.4980391860008239, 0.4980391860008239, 0.4980391860008239 ) );

		//System.out.println( "b=" + Colors.getLuminance(  Color.color( 0, 0, 1 )) + " sbcb=" + Colors.getLuminance( Color.rgb( 32, 64, 128 )));
		// Others
		assertThat( Colors.swapLuminance( Color.rgb( 32, 64, 128 ) ) ).isEqualTo( Color.rgb( 129, 160, 223 ) );
	}

	@Test
	@Disabled
	void testInvertBrightness() {
		assertInvertedBrightness( Color.rgb( 0, 0, 0, 0 ), Color.rgb( 0, 0, 0, 0 ) );

		assertInvertedBrightness( Color.rgb( 0, 0, 0 ), Color.rgb( 255, 255, 255 ) );
		assertInvertedBrightness( Color.rgb( 128, 128, 128 ), Color.rgb( 126, 126, 126 ) );
		assertInvertedBrightness( Color.rgb( 127, 127, 127 ), Color.rgb( 127, 127, 127 ) );

		// yellow
		assertInvertedBrightness( Color.rgb( 255, 255, 0 ), Color.rgb( 31, 31, 0 ) );

		// red
		// FIXME Red doesn't invert correctly
		assertInvertedBrightness( Color.rgb( 255, 0, 0 ), Color.rgb( 76, 0, 0 ) );

		// green
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

	private static double clamp( double value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 1 ) {
			value = 1;
		}
		return value;
	}

}
