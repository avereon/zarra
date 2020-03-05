package com.avereon.venza.image;

public abstract class ProgramIcon extends ProgramImage {

	public ProgramIcon() {
		super();
		getStyleClass().add( "xe-icon" );
	}

	public double getSize() {
		return getWidth();
	}

}
