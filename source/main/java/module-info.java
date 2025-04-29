module com.avereon.zerra {
	requires static lombok;

	requires com.avereon.zevra;
	requires javafx.controls;
	requires javafx.swing;
	requires image4j;

	exports com.avereon.zerra.color;
	exports com.avereon.zerra.event;
	exports com.avereon.zerra.font;
	exports com.avereon.zerra.image;
	exports com.avereon.zerra.javafx;
	exports com.avereon.zerra.style;
}
