package com.avereon.zarra.font;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FontUtilTest {

	@Test
	void testEncode() {
		// Negative checks
		assertThat( Font.font( "SansSerif", 18.0 ) ).isNotEqualTo( Font.font( "System", 18.0 ) );

		// Positive checks
		assertThat( FontUtil.encode( Font.font( "SansSerif", 18.0 ) ) ).isEqualTo( "SansSerif|Regular|18.0" );
		assertThat( FontUtil.encode( Font.font( "SansSerif", FontWeight.BOLD, 18.0 ) ) ).isEqualTo( "SansSerif|Bold|18.0" );
		assertThat( FontUtil.encode( Font.font( "SansSerif", FontWeight.BOLD, FontPosture.ITALIC, 18.0 ) ) ).isEqualTo( "SansSerif|Bold Italic|18.0" );
	}

	@Test
	void testDecode() {
		// Negative checks
		assertThat( FontUtil.decode( "SansSerif|18.0" ) ).isNotEqualTo( Font.font( "System", 18.0 ) );

		// Positive checks
		assertThat( FontUtil.decode( "SansSerif|18.0" ) ).isEqualTo( Font.font( "SansSerif", 18.0 ) );
		assertThat( FontUtil.decode( "SansSerif|Bold|18.0" ) ).isEqualTo( Font.font( "SansSerif", FontWeight.BOLD, 18.0 ) );
		assertThat( FontUtil.decode( "SansSerif|Bold Italic|18.0" ) ).isEqualTo( Font.font( "SansSerif", FontWeight.BOLD, FontPosture.ITALIC, 18.0 ) );
	}

	@Test
	void testGetMonoFontFamilyNames() {
		assertThat( FontUtil.getMonoFontFamilyNames().size() ).isGreaterThan( 0 );
	}

}
