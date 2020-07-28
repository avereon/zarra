module com.avereon.venza {
	requires transitive org.hamcrest;
	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.swing;
	requires image4j;

	exports com.avereon.venza.color;
	exports com.avereon.venza.event;
	exports com.avereon.venza.font;
	exports com.avereon.venza.image;
	exports com.avereon.venza.javafx;
	exports com.avereon.venza.style;
	exports com.avereon.venza.test;
}
