package com.avereon.venza.image;

import javafx.scene.paint.Color;

public interface DefIcon {

	static void asIcon( VectorImage image ) {
		image.getStyleClass().add( "xe-icon" );
	}

	static void proof( VectorImage icon ) {
		Proof.proof( icon );
	}

	static void proof( VectorImage icon, Color darkFill, Color lightFill ) {
		Proof.proof( icon, darkFill, lightFill );
	}

}
