module com.avereon.venza {
	requires transitive com.avereon.zevra;
	requires java.logging;
	requires javafx.controls;
	requires javafx.swing;
	requires org.slf4j;

	exports com.avereon.venza.color;
	exports com.avereon.venza.font;
	exports com.avereon.venza.image;
	exports com.avereon.venza.javafx;
}
