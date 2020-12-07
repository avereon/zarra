package com.avereon.zerra.color;

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
		return Colors.web( color );
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
		if( string.startsWith( "#" ) ) return Colors.web( string );
		if( string.startsWith( "[" ) ) return Colors.web( string );
		if( string.startsWith( "(" ) ) return Colors.web( string );

		return Colors.web( string );
	}

}
