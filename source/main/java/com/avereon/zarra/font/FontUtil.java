package com.avereon.zarra.font;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CustomLog
public final class FontUtil {

	private static List<String> monoFontFamilyList;

	private static final String SEPARATOR = "|";

	public static String encode( Font font ) {
		if( font == null ) return null;
		return font.getFamily() + SEPARATOR + font.getStyle() + SEPARATOR + font.getSize();
	}

	public static Font decode( String string ) {
		if( string == null ) return null;

		int index;
		int start = 0;
		List<String> strings = new ArrayList<>();
		while( (index = string.indexOf( SEPARATOR, start )) > -1 ) {
			strings.add( string.substring( start, index ) );
			start = index + SEPARATOR.length();
		}
		strings.add( string.substring( start ) );

		String family = string;
		String style = FontPosture.REGULAR.name();
		String sizeString = "-1";
		switch( strings.size() ) {
			case 2 -> {
				family = strings.get( 0 );
				sizeString = strings.get( 1 );
			}
			case 3 -> {
				family = strings.get( 0 );
				style = strings.get( 1 );
				sizeString = strings.get( 2 );
			}
		}

		double size = -1;
		try {
			size = Double.parseDouble( sizeString );
		} catch( NumberFormatException exception ) {
			log.atWarning().withCause( exception ).log( "Error parsing font size" );
		}

		return javafx.scene.text.Font.font( family, getFontWeight( style ), getFontPosture( style ), size );
	}

	/**
	 * Pass in the font style and this will return the font weight.
	 *
	 * @param string The font style string
	 * @return The font weight
	 */
	public static FontWeight getFontWeight( String string ) {
		string = string.toUpperCase();
		for( FontWeight weight : FontWeight.values() ) {
			if( string.startsWith( weight.name() ) ) return weight;
		}
		return FontWeight.NORMAL;
	}

	/**
	 * Pass in the font style and this will return the font posture.
	 *
	 * @param string The font style string
	 * @return The font posture
	 */
	public static FontPosture getFontPosture( String string ) {
		string = string.toUpperCase();
		for( FontPosture posture : FontPosture.values() ) {
			if( string.endsWith( posture.name() ) ) return posture;
		}

		return FontPosture.REGULAR;
	}

	public static Font fromMap( Map<String, Object> map ) {
		// "font"={"family"="System", "name"="System Regular", "size"=13.0, "style"="Regular"}
		String family = String.valueOf( map.get( "family" ) );
		FontWeight weight = FontUtil.getFontWeight( String.valueOf( map.getOrDefault( "style", FontWeight.NORMAL ) ) );
		FontPosture posture = FontUtil.getFontPosture( String.valueOf( map.getOrDefault( "style", FontPosture.REGULAR ) ) );
		double size = Double.parseDouble( String.valueOf( map.get( "size" ) ) );
		return Font.font( family, weight, posture, size );
	}

	/**
	 * Return a list of all the monospaced fonts on the system.
	 *
	 * @return An observable list of all the monospaced fonts on the system.
	 */
	public static List<String> getMonoFontFamilyNames() {
		if( monoFontFamilyList != null ) return monoFontFamilyList;

		// Compare the layout widths of two strings. One string is composed
		// of "thin" characters, the other of "wide" characters. In monospaced
		// fonts the widths should be the same.

		final Text thinText = new Text( "1 l" ); // note the space
		final Text thickText = new Text( "MWX" );

		List<String> fontFamilyList = Font.getFamilies();
		List<String> monoFamilyList = new ArrayList<>();

		Font font;

		for( String fontFamilyName : fontFamilyList ) {
			font = Font.font( fontFamilyName, FontWeight.NORMAL, FontPosture.REGULAR, 14.0d );
			thinText.setFont( font );
			thickText.setFont( font );
			if( thinText.getLayoutBounds().getWidth() == thickText.getLayoutBounds().getWidth() ) monoFamilyList.add( fontFamilyName );
		}

		return monoFontFamilyList = Collections.unmodifiableList( monoFamilyList );
	}

}
