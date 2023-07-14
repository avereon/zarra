package com.avereon.zarra.color;

import javafx.scene.paint.Color;

public class Colors {

	// Per http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
	//	public static final double RED_BRIGHTNESS_FACTOR = 0.2126;
	//	public static final double GREEN_BRIGHTNESS_FACTOR = 0.7152;
	//	public static final double BLUE_BRIGHTNESS_FACTOR = 0.0722;

	// Per com.sun.javafx.util.Utils.calculateBrightness(Color)
	public static final double RED_BRIGHTNESS_FACTOR = 0.3;

	public static final double GREEN_BRIGHTNESS_FACTOR = 0.59;

	public static final double BLUE_BRIGHTNESS_FACTOR = 0.11;

	public static Color parse( String string ) {
		if( string == null ) return null;
		return Color.web( string );
	}

	public static String toString( Color color ) {
		if( color == null ) return null;
		int r = (int)Math.round( color.getRed() * 255.0 );
		int g = (int)Math.round( color.getGreen() * 255.0 );
		int b = (int)Math.round( color.getBlue() * 255.0 );
		int o = (int)Math.round( color.getOpacity() * 255.0 );
		return String.format( "#%02x%02x%02x%02x", r, g, b, o );
	}

	public static Color opaque( Color color ) {
		return new Color( color.getRed(), color.getGreen(), color.getBlue(), 1.0 );
	}

	public static Color translucent( Color color, double factor ) {
		return new Color( color.getRed(), color.getGreen(), color.getBlue(), factor );
	}

	public static Color mix( Color color, Color mixer, double factor ) {
		if( color == null || mixer == null ) return null;

		if( Color.TRANSPARENT.equals( color ) ) return translucent( mixer, factor );
		if( Color.TRANSPARENT.equals( mixer ) ) return translucent( color, factor );

		factor = clamp( factor );
		double mixFactor = 1 - factor;

		double colorR = color.getRed() * mixFactor;
		double colorG = color.getGreen() * mixFactor;
		double colorB = color.getBlue() * mixFactor;
		double colorA = color.getOpacity() * mixFactor;

		double mixerR = mixer.getRed() * factor;
		double mixerG = mixer.getGreen() * factor;
		double mixerB = mixer.getBlue() * factor;
		double mixerA = mixer.getOpacity() * factor;

		double r = colorR + mixerR;
		double g = colorG + mixerG;
		double b = colorB + mixerB;
		double a = colorA + mixerA;

		return new Color( r, g, b, a );
	}

	/**
	 * Invert the brightness of a color while maintaining hue and saturation.
	 *
	 * @param color The color to invert
	 * @return The color with the brightness inverted
	 */
	public static Color invertBrightness( Color color ) {
		int r, g, b;
		double a = color.getOpacity();

		if( a == 0 ) {
			// Handle transparent
			r = g = b = 0;
		} else if( color.getSaturation() == 0 ) {
			// Handle grayscale
			r = g = b = (int)((1 - color.getBrightness()) * 255);
		} else {
			// Handle colors
			double luminance = Colors.getLuminance( color );
			Color baseColor = Color.hsb( color.getHue(), 1, 1 );
			double invertedLuminance = 1.0 - luminance;
			double luminanceFactor = 1 / luminance;

			// FIXME luminanceFactor is really large for the color RED

			double tc = baseColor.getRed() + baseColor.getGreen() + baseColor.getBlue();
			double wr = baseColor.getRed() / tc;
			double wg = baseColor.getGreen() / tc;
			double wb = baseColor.getBlue() / tc;

			System.out.println( "wr=" + wr + " wg=" + wg + " wb=" + wb + " il=" + invertedLuminance + " lf=" + luminanceFactor );

			// Now, how to get the rgb colors to balance and sum to desired luminosity
			double dr = clamp( baseColor.getRed() * invertedLuminance * luminanceFactor );
			double dg = clamp( baseColor.getGreen() * invertedLuminance * luminanceFactor );
			double db = clamp( baseColor.getBlue() * invertedLuminance * luminanceFactor );

			r = (int)(dr * 255);
			g = (int)(dg * 255);
			b = (int)(db * 255);
		}

		Color desiredColor = Color.rgb( r, g, b, a );
		//System.out.println( "desired color intensity=" + getLuminance( desiredColor ) );
		//System.out.printf( "-> r=%d g=%d b=%d\n", r, g, b );
		return desiredColor;
	}

	/**
	 * Generate a toned color by mixing the specified color with white (factor
	 * 1.0) or black (factor -1.0).
	 */
	public static Color getTone( Color color, double factor ) {
		//factor = clamp( factor );
		if( factor > 0 ) return getTint( color, factor );
		if( factor < 0 ) return getShade( color, -factor );
		return color;
	}

	/**
	 * Generate a tinted color by mixing the specified color with white.
	 *
	 * @param color
	 * @param factor
	 * @return
	 */
	public static Color getTint( Color color, double factor ) {
		factor = clamp( factor );
		return mix( color, new Color( 1, 1, 1, color.getOpacity() ), factor );
	}

	/**
	 * Generate a shaded color by mixing the specified color with black.
	 *
	 * @param color
	 * @param factor
	 * @return
	 */
	public static Color getShade( Color color, double factor ) {
		factor = clamp( factor );
		return mix( color, new Color( 0, 0, 0, color.getOpacity() ), factor );
	}

	/**
	 * Get the intensity of the specified color.
	 *
	 * @param color The color for which to find the luminance.
	 * @return A number between 0.0 and 1.0 representing the luminance.
	 */
	public static double getLuminance( Color color ) {
		if( color.getRed() + color.getGreen() + color.getBlue() == 3.0 ) return 1.0;
		return RED_BRIGHTNESS_FACTOR * color.getRed() + GREEN_BRIGHTNESS_FACTOR * color.getGreen() + BLUE_BRIGHTNESS_FACTOR * color.getBlue();
	}

	public static Color swapLuminance( Color color ) {
		double sourceLuminance = Colors.getLuminance( color );
		double targetLuminance = Colors.getLuminance( color.invert() );

		System.out.println( "sourceLuminance=" + sourceLuminance + " targetLuminance=" + targetLuminance );

		if( sourceLuminance == 0 ) return Color.color( 1, 1, 1, color.getOpacity() );
		if( sourceLuminance == 1 ) return Color.color( 0, 0, 0, color.getOpacity() );

		double inverseFactor = targetLuminance / sourceLuminance;
		double r = color.getRed() * inverseFactor;
		double g = color.getGreen() * inverseFactor;
		double b = color.getBlue() * inverseFactor;

		boolean redPegged = r > 1.0;
		boolean greenPegged = g > 1.0;
		boolean bluePegged = b > 1.0;
		boolean rollover = redPegged || greenPegged || bluePegged;

		if( rollover ) {
			double remainingTargetLuminance = targetLuminance;
			if( redPegged ) remainingTargetLuminance -= RED_BRIGHTNESS_FACTOR;
			if( greenPegged ) remainingTargetLuminance -= GREEN_BRIGHTNESS_FACTOR;
			if( bluePegged ) remainingTargetLuminance -= BLUE_BRIGHTNESS_FACTOR;

			double combinedAvailableFactors = 0;
			if( !redPegged ) combinedAvailableFactors += RED_BRIGHTNESS_FACTOR;
			if( !greenPegged ) combinedAvailableFactors += GREEN_BRIGHTNESS_FACTOR;
			if( !bluePegged ) combinedAvailableFactors += BLUE_BRIGHTNESS_FACTOR;

			double n = remainingTargetLuminance / combinedAvailableFactors;

			// TODO n has to be distributed proportionally across available channels
			double sourceR = color.getRed();
			double sourceG = color.getGreen();
			double sourceB = color.getBlue();
			double sourceT = sourceR + sourceG + sourceB;
			double rContribution = sourceR / sourceT;
			double gContribution = sourceG / sourceT;
			double bContribution = sourceB / sourceT;

			System.out.println( "rc=" + rContribution + " gc=" + gContribution + " bc=" + bContribution + " sourceT=" + sourceT );

			// FIXME What to do when contributions are zero; still should result in a factor of 1
			double combinedSourceColor = 0;
			if( !redPegged ) combinedSourceColor += rContribution;
			if( !greenPegged ) combinedSourceColor += gContribution;
			if( !bluePegged ) combinedSourceColor += bContribution;

			double rFactor = 1 + (combinedSourceColor == 0 ? 0 : sourceR / combinedSourceColor);
			double gFactor = 1 + (combinedSourceColor == 0 ? 0 : sourceG / combinedSourceColor);
			double bFactor = 1 + (combinedSourceColor == 0 ? 0 : sourceB / combinedSourceColor);

			System.out.println( "rf=" + rFactor + " gf=" + gFactor + " bf=" + bFactor + " combinedSourceColor=" + combinedSourceColor );

			r = clamp( redPegged ? 1.0 : n * rFactor );
			g = clamp( greenPegged ? 1.0 : n * gFactor );
			b = clamp( bluePegged ? 1.0 : n * bFactor );
		}

		System.out.println( "color=" + color + " r=" + r + " g=" + g + " b=" + b );
		return Color.color( r, g, b, color.getOpacity() );
	}

	private static double clamp( double value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 1 ) {
			value = 1;
		}
		return value;
	}

	private static float clamp( float value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 1 ) {
			value = 1;
		}
		return value;
	}

	private static int clamp( int value ) {
		if( value < 0 ) {
			value = 0;
		} else if( value > 255 ) {
			value = 255;
		}
		return value;
	}

}

