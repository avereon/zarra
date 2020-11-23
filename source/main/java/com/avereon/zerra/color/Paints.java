package com.avereon.zerra.color;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

public class Paints {

	public static String toString( Color color ) {
		return Colors.web( color );
	}

	public static String toString( LinearGradient gradient ) {
		return "[]";
	}

	public static String toString( RadialGradient gradient ) {
		return "()";
	}

	public static Paint parse( String string ) {
		// # - color
		// [] - linear gradient
		// () - radial gradient
		if( string.startsWith( "#" ) ) return Colors.web( string );
		if( string.startsWith( "[" ) ) return Colors.web( string );
		if( string.startsWith( "(" ) ) return Colors.web( string );

		return Colors.web( string );
	}

}
