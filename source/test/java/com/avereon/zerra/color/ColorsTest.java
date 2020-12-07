package com.avereon.zerra.color;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

class ColorsTest {

	@Test
	void testToString() {
		assertThat( Colors.toString( Color.TRANSPARENT ), is( "#00000000"));
		assertThat( Colors.toString( Color.BLACK ), is( "#000000ff"));
		assertThat( Colors.toString( Color.WHITE ), is( "#ffffffff"));
		assertThat( Colors.toString( Color.GRAY ), is( "#808080ff"));
		assertNull( Colors.toString( null ) );
	}

	@Test
	void testParse() {
		assertThat( Colors.parse( "#00000000"), is( Color.TRANSPARENT ));
		assertThat( Colors.parse( "#000000ff"), is( Color.BLACK ));
		assertThat( Colors.parse( "#ffffffff"), is( Color.WHITE ));
		assertThat( Colors.parse( "#808080ff"), is( Color.GRAY ));
		assertNull( Colors.parse( null ) );
	}

}
