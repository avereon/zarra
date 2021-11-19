package com.avereon.zarra.color;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;
import static org.testfx.assertions.api.Assertions.assertThat;

public class PaintsTest {

	@Test
	void testToString() {
		assertThat( Paints.toString( Color.TRANSPARENT ) ).isEqualTo( "#00000000" );
		assertThat( Paints.toString( Color.BLACK ) ).isEqualTo( "#000000ff" );
		assertThat( Paints.toString( Color.WHITE ) ).isEqualTo( "#ffffffff" );
		assertThat( Paints.toString( Color.GRAY ) ).isEqualTo( "#808080ff" );
		assertThat( Paints.toString( (Color)null ) ).isNull();
	}

	@Test
	void testParse() {
		assertThat( Paints.parse( "#00000000" ) ).isEqualTo( Color.TRANSPARENT );
		assertThat( Paints.parse( "#000000ff" ) ).isEqualTo( Color.BLACK );
		assertThat( Paints.parse( "#ffffffff" ) ).isEqualTo( Color.WHITE );
		assertThat( Paints.parse( "#808080ff" ) ).isEqualTo( Color.GRAY );
		assertThat( Paints.parse( null ) ).isNull();
	}

	@Test
	void testParseWithInvalidColor() {
		try {
			Paints.parse( "invalid" );
			fail( "Should throw an IllegalArgumentException" );
		} catch( IllegalArgumentException exception ) {
			assertThat( exception ).isInstanceOf( IllegalArgumentException.class );
		}
	}

	@Test
	void testParseWithNullOnExceptionWithInvalidColor() {
		assertThat( Paints.parseWithNullOnException( "invalid" ) ).isNull();
	}

}
