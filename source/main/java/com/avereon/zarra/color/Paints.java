package com.avereon.zarra.color;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

public class Paints {

	public static String toString( Paint paint ) {
		if( paint == null ) return null;
		if( paint instanceof Color ) return toString( (Color)paint );
		if( paint instanceof LinearGradient ) return toString( (LinearGradient)paint );
		if( paint instanceof RadialGradient ) return toString( (RadialGradient)paint );
		throw new IllegalArgumentException( "Unknown paint type: " + paint.getClass().getName() );
	}

	public static String toString( Color color ) {
		if( color == null ) return null;
		return Colors.toString( color );
	}

	public static String toString( LinearGradient gradient ) {
		return "[]";
	}

	public static String toString( RadialGradient gradient ) {
		return "()";
	}

	public static Paint parse( String string ) {
		if( string == null ) return null;

		// # - color
		// [] - linear gradient
		// () - radial gradient
		if( string.startsWith( "#" ) ) return Colors.parse( string );
		if( string.startsWith( "[" ) ) return Colors.parse( string );
		if( string.startsWith( "(" ) ) return Colors.parse( string );

		return Colors.parse( string );
	}

	public static Paint parseWithNullOnException( String string ) {
		try {
			return parse( string );
		} catch( IllegalArgumentException exception ) {
			return null;
		}
	}

}
