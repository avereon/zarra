package com.avereon.zarra.color;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColorsTest {

	@Test
	void testToString() {
		assertThat( Colors.toString( Color.TRANSPARENT )).isEqualTo( "#00000000");
		assertThat( Colors.toString( Color.BLACK )).isEqualTo( "#000000ff");
		assertThat( Colors.toString( Color.WHITE )).isEqualTo( "#ffffffff");
		assertThat( Colors.toString( Color.GRAY )).isEqualTo( "#808080ff");
		assertThat( Colors.toString( null ) ).isNull();
	}

	@Test
	void testParse() {
		assertThat( Colors.parse( "#00000000")).isEqualTo( Color.TRANSPARENT );
		assertThat( Colors.parse( "#000000ff")).isEqualTo( Color.BLACK );
		assertThat( Colors.parse( "#ffffffff")).isEqualTo( Color.WHITE );
		assertThat( Colors.parse( "#808080ff")).isEqualTo( Color.GRAY );
		assertThat( Colors.parse( null ) ).isNull();
	}

}
