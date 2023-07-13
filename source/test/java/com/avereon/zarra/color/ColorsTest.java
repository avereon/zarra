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
	void testSwapRed() {
		Color color = Color.color( 1, 0, 0 );
		double colorLuminance = Colors.getLuminance( color );
		assertThat( colorLuminance ).isEqualTo( 0.3 );

		Color inverse = color.invert();
		double inverseLuminance = Colors.getLuminance( inverse );
		assertThat( inverseLuminance ).isEqualTo( 0.7 );

		// Now, to generate a color that has a luminance of 0.7
		double redLuminance = 0.3 * color.getRed();
		double greenLuminance = 0.59 * color.getGreen();
		double blueLuminance = 0.11 * color.getBlue();

		// These weights are based on the channel luminance
		double inverseFactor = inverseLuminance / colorLuminance;
		double inverseRedLuminance = color.getRed() * inverseFactor; // 2.3333333333333335
		double inverseGreenLuminance = color.getGreen() * inverseFactor; // 0.0
		double inverseBlueLuminance = color.getBlue() * inverseFactor; // 0.0

		System.out.println( "if=" + inverseFactor );

		// In this case the new luminance is 0.7 (on the high end)
		// This means that we need to find a light red with luminance 0.7
		// Because one of the luminance values is greater than 1 we need to fill in the rest with green and blue
		// Red will account for 0.3 of the new luminance
		// That means that 0.4 needs to be accounted for by green and blue
		// but with the luminance weights of green and blue
		// Luminance factor for both green and blue together is 0.7
		// n * 0.7[green+blue factors] = 0.4
		// n = 0.4 / 0.7[green+blue factors]
		// n = 0.571428571
		// That should mean that green and blue values of 0.4 / 0.7 should give us the
		// extra 0.4 of luminance

		Color invertedRed = Color.color( 1, 0.4 / 0.7, 0.4 / 0.7 );
		assertThat( Colors.getLuminance( invertedRed ) ).isCloseTo( 0.7, Offset.offset( 1e-7 ) );
	}

	@Test
	void testSwapYellow() {
		Color color = Color.color( 1, 1, 0 );
		double colorLuminance = Colors.getLuminance( color );
		assertThat( colorLuminance ).isEqualTo( 0.8899999999999999 );

		Color inverse = color.invert();
		double inverseLuminance = Colors.getLuminance( inverse );
		assertThat( inverseLuminance ).isEqualTo( 0.11 );

		// Now, to generate a color that has a luminance of 0.8899999999999999
		double redLuminance = 0.3 * color.getRed();
		double greenLuminance = 0.59 * color.getGreen();
		double blueLuminance = 0.11 * color.getBlue();

		// These weights are based on the channel luminance
		double inverseFactor = inverseLuminance / colorLuminance;
		double inverseRedLuminance = color.getRed() * inverseFactor; // 0.12359550561797754
		double inverseGreenLuminance = color.getGreen() * inverseFactor; // 0.12359550561797754
		double inverseBlueLuminance = color.getBlue() * inverseFactor; // 0.0

		//System.out.println( "inverseRedLuminance=" + inverseRedLuminance );

		// In this case the new luminance is 0.11 (rather low)
		// This means that we need to find a dark color with luminance 0.11
		// All the inverse luminance values are less than 1 so no need to "fill" extra channels
		// Red will account for 0.037078652 of the new luminance
		// Green will account for 0.072921348 of the new luminance
		// Blue will account for 0.0 of the new luminance
		// red = inverseRedLuminance
		// green = inverseGreenLuminance
		// blue = inverseBlueLuminance

		Color inverted = Color.color( inverseRedLuminance, inverseGreenLuminance, inverseBlueLuminance );
		assertThat( Colors.getLuminance( inverted ) ).isCloseTo( 0.11, Offset.offset( 1e-7 ) );
	}

	@Test
	void testSwapBlue() {
		Color color = Color.color( 0, 0, 1 );
		double colorLuminance = Colors.getLuminance( color );
		assertThat( colorLuminance ).isEqualTo( 0.11 );

		Color inverse = color.invert();
		double inverseLuminance = Colors.getLuminance( inverse );
		assertThat( inverseLuminance ).isCloseTo( 0.89, Offset.offset( 1e-7 ) );

		// Now, to generate a color that has a luminance of 0.7
		double redLuminance = 0.3 * color.getRed();
		double greenLuminance = 0.59 * color.getGreen();
		double blueLuminance = 0.11 * color.getBlue();

		// These weights are based on the channel luminance
		double inverseFactor = inverseLuminance / colorLuminance; // 8.09090909090909
		double inverseRedLuminance = color.getRed() * inverseFactor; // 0.0
		double inverseGreenLuminance = color.getGreen() * inverseFactor; // 0.0
		double inverseBlueLuminance = color.getBlue() * inverseFactor; // 8.09090909090909

		//System.out.println( "if=" + inverseBlueLuminance );

		// In this case the new luminance is 0.89 (on the high end)
		// This means that we need to find a light blue with luminance 0.89
		// Because one of the luminance values is greater than 1 we need to fill in the rest with red and green
		// Blue will account for 0.11 of the new luminance
		// That means that 0.78 needs to be accounted for by red and green
		// but with the luminance weights of red and green
		// Luminance factor for both red and green together is 0.89
		// n * 0.89[red+green factors] = 0.78
		// n = 0.78 / 0.89[red+green factors]
		// n = 0.876404494
		// That should mean that red and green values of 0.78 / 0.89 should give us the
		// extra 0.78 of luminance

		Color invertedRed = Color.color( 0.78 / 0.89, 0.78 / 0.89, 1 );
		assertThat( Colors.getLuminance( invertedRed ) ).isCloseTo( 0.89, Offset.offset( 1e-7 ) );
	}

	@Test
	void testSwapGreen() {
		Color color = Color.color( 0, 1, 0 );
		double colorLuminance = Colors.getLuminance( color );
		assertThat( colorLuminance ).isEqualTo( 0.59 );

		Color inverse = color.invert();
		double inverseLuminance = Colors.getLuminance( inverse );
		assertThat( inverseLuminance ).isCloseTo( 0.41, Offset.offset( 1e-7 ) );

		// Now, to generate a color that has a luminance of 0.41
		double redLuminance = 0.3 * color.getRed();
		double greenLuminance = 0.59 * color.getGreen();
		double blueLuminance = 0.11 * color.getBlue();

		// These weights are based on the channel luminance
		double inverseFactor = inverseLuminance / colorLuminance; // 0.6949152542372882
		double inverseRedLuminance = color.getRed() * inverseFactor; // 0.0
		double inverseGreenLuminance = color.getGreen() * inverseFactor; // 0.6949152542372882
		double inverseBlueLuminance = color.getBlue() * inverseFactor; // 0.0

		//System.out.println( "if=" + inverseFactor );

		// In this case the new luminance is 0.41 (on the low end)
		// This means that we need to find a dark green with luminance 0.41
		// All the inverse luminance values are less than 1 so no need to "fill" extra channels
		// Red will account for 0.0 of the new luminance
		// Green will account for 0.41 of the new luminance
		// Blue will account for 0.0 of the new luminance
		// That means that 0.78 needs to be accounted for by red and green
		// but with the luminance weights of red and green
		// Luminance factor for both red and green together is 0.89
		// n * 0.89[red+green factors] = 0.78
		// n = 0.78 / 0.89[red+green factors]
		// n = 0.876404494
		// That should mean that red and green values of 0.78 / 0.89 should give us the
		// extra 0.78 of luminance

		Color inverted = Color.color( inverseRedLuminance, inverseGreenLuminance, inverseBlueLuminance );
		assertThat( Colors.getLuminance( inverted ) ).isCloseTo( 0.41, Offset.offset( 1e-7 ) );
	}

	@Test
	void testSwapBrightness() {
		assertThat( Colors.swapLuminance( Color.color( 1, 0, 0 ) ) ).isEqualTo( Color.color( 1, 0.4 / 0.7, 0.4 / 0.7 ) );
		assertThat( Colors.swapLuminance( Color.color( 0, 0, 1 ) ) ).isEqualTo( Color.color( 0.78 / 0.89, 0.78 / 0.89, 1 ) );
		assertThat( Colors.swapLuminance( Color.color( 0, 1, 0 ) ) ).isEqualTo( Color.color( 0, 0.6949152542372882, 0 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 1, 0 ) ) ).isEqualTo( Color.color( 0.12359550561797754, 0.12359550561797754, 0 ) );

		assertThat( Colors.swapLuminance( Color.color( 0, 1, 1 ) ) ).isEqualTo( Color.color( 0.8764044943820226, 0.8764044943820226, 1 ) );
		assertThat( Colors.swapLuminance( Color.color( 1, 0, 1 ) ) ).isEqualTo( Color.color( 1, 1, 1 ) );
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

}
