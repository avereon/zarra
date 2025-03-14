module com.avereon.zarra {
	requires static lombok;

	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.swing;
	requires image4j;

	exports com.avereon.zarra.color;
	exports com.avereon.zarra.event;
	exports com.avereon.zarra.font;
	exports com.avereon.zarra.image;
	exports com.avereon.zarra.javafx;
	exports com.avereon.zarra.style;
}
