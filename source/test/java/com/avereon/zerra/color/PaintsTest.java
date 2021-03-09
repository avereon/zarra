package com.avereon.zerra.color;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class PaintsTest {

	@Test
	void testToString() {
		assertThat( Paints.toString( Color.TRANSPARENT ), is( "#00000000" ) );
		assertThat( Paints.toString( Color.BLACK ), is( "#000000ff" ) );
		assertThat( Paints.toString( Color.WHITE ), is( "#ffffffff" ) );
		assertThat( Paints.toString( Color.GRAY ), is( "#808080ff" ) );
		assertNull( Paints.toString( (Color)null ) );
	}

	@Test
	void testParse() {
		assertThat( Paints.parse( "#00000000" ), is( Color.TRANSPARENT ) );
		assertThat( Paints.parse( "#000000ff" ), is( Color.BLACK ) );
		assertThat( Paints.parse( "#ffffffff" ), is( Color.WHITE ) );
		assertThat( Paints.parse( "#808080ff" ), is( Color.GRAY ) );
		assertNull( Paints.parse( null ) );
	}

	@Test
	void testParseWithInvalidColor() {
		try {
			Paints.parse( "invalid" );
			fail( "Should throw an IllegalArgumentException" );
		} catch( IllegalArgumentException exception ) {
			assertThat( exception, instanceOf( IllegalArgumentException.class ) );
		}
	}

	@Test
	void testParseWithNullOnExceptionWithInvalidColor() {
		assertNull( Paints.parseWithNullOnException( "invalid" ) );
	}

}
