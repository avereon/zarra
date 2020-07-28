package com.avereon.zerra.image;

import javafx.scene.paint.Color;

public class BrokenIcon extends RenderedIcon {

	@Override
	public void render() {
		double min = g( 8 );
		double max = g( 24 );

		setDrawPaint( Color.RED );
		setDrawWidth( g( 6 ) );

		startPath();
		moveTo(min, min );
		lineTo(max, max);
		moveTo(max, min );
		lineTo( min, max );
		draw();
	}

	public static void main( String[] commands ) {
		proof( new BrokenIcon() );
	}

}
