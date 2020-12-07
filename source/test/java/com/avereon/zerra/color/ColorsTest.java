package com.avereon.zerra.color;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

class ColorsTest {

	@Test
	void testWebWithColor() {
		assertThat( Colors.web( Color.TRANSPARENT ), is( "#00000000"));
		assertThat( Colors.web( Color.BLACK ), is( "#000000ff"));
		assertThat( Colors.web( Color.WHITE ), is( "#ffffffff"));
		assertThat( Colors.web( Color.GRAY ), is( "#808080ff"));
		assertNull( Colors.web( (Color)null ) );
	}

	@Test
	void testWebWithString() {
		assertThat( Colors.web( "#00000000"), is( Color.TRANSPARENT ));
		assertThat( Colors.web( "#000000ff"), is( Color.BLACK ));
		assertThat( Colors.web( "#ffffffff"), is( Color.WHITE ));
		assertThat( Colors.web( "#808080ff"), is( Color.GRAY ));
		assertNull( Colors.web( (String)null ) );
	}

}
