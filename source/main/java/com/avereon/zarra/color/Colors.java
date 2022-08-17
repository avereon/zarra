package com.avereon.zarra.color;

import javafx.scene.paint.Color;

public class Colors {

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
	 * <p>
	 * Derived from: http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
	 *
	 * @param color The color for which to find the luminance.
	 * @return A number between 0.0 and 1.0 representing the luminance.
	 */
	public static double getLuminance( Color color ) {
		// Per http://stackoverflow.com/questions/596216/formula-to-determine-brightness-of-rgb-color
		//return  (0.2126*color.getRed()) + (0.7152*color.getGreen()) + (0.0722*color.getBlue());

		// Per com.sun.javafx.util.Utils.calculateBrightness(Color)
		return (0.3 * color.getRed()) + (0.59 * color.getGreen()) + (0.11 * color.getBlue());
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

