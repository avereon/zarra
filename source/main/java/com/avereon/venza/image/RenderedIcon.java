package com.avereon.venza.image;

public abstract class RenderedIcon extends RenderedImage implements IconTag {

	protected RenderedIcon() {
		super();
	}

	protected RenderedIcon( double gridX, double gridY ) {
		super( gridX, gridY );
	}

}
