package com.avereon.zerra.color;

import javafx.scene.paint.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

class PaintsTest {

	@Test
	void testToString() {
		assertThat( Paints.toString( Color.TRANSPARENT ) ).isEqualTo( "#00000000" );
		assertThat( Paints.toString( Color.BLACK ) ).isEqualTo( "#000000ff" );
		assertThat( Paints.toString( Color.WHITE ) ).isEqualTo( "#ffffffff" );
		assertThat( Paints.toString( Color.GRAY ) ).isEqualTo( "#808080ff" );
		assertThat( Paints.toString( (Color)null ) ).isNull();
	}

	@Test
	void testToStringWithLinearGradient() {
		// given
		LinearGradient gradient = new LinearGradient( 0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop( 1, Color.RED ), new Stop( 0.5, Color.GREEN ), new Stop( 1, Color.BLUE ) );

		// when
		String result = gradient.toString();

		// then
		assertThat( result ).isEqualTo( "linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, 0x008000ff 0.0%, 0x008000ff 50.0%, 0xff0000ff 100.0%)" );
	}

	@Test
	void testToStringWithRadialGradient() {
		// given
		RadialGradient gradient = new RadialGradient( 0, 0, 1, 1, 1, true, CycleMethod.NO_CYCLE, new Stop( 1, Color.RED ), new Stop( 0.5, Color.GREEN ), new Stop( 1, Color.BLUE ) );

		// when
		String result = gradient.toString();

		// then
		assertThat( result ).isEqualTo( "radial-gradient(focus-angle 0.0deg, focus-distance 0.0% , center 100.0% 100.0%, radius 100.0%, 0x008000ff 0.0%, 0x008000ff 50.0%, 0xff0000ff 100.0%)" );
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
	void testParseWithLinearGradient() {
		// given
		String source = "linear-gradient(from 0.0% 0.0% to 100.0% 100.0%, 0x008000ff 0.0%, 0x008000ff 50.0%, 0xff0000ff 100.0%)";

		// when
		Paint result = Paints.parse( source );

		// then
		assertThat( result ).isEqualTo( new LinearGradient( 0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop( 1, Color.RED ), new Stop( 0.5, Color.GREEN ), new Stop( 1, Color.BLUE ) ) );
	}

	@Test
	void testParseWithRadialGradient() {
		// given
		String source = "radial-gradient(focus-angle 0.0deg, focus-distance 0.0% , center 100.0% 100.0%, radius 100.0%, 0x008000ff 0.0%, 0x008000ff 50.0%, 0xff0000ff 100.0%)";

		// when
		Paint result = Paints.parse( source );

		// then
		assertThat( result ).isEqualTo( new RadialGradient( 0, 0, 1, 1, 1, true, CycleMethod.NO_CYCLE, new Stop( 1, Color.RED ), new Stop( 0.5, Color.GREEN ), new Stop( 1, Color.BLUE ) ) );
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
