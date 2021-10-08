package com.avereon.zarra.image;

import javafx.scene.paint.Color;

public interface IconTag {

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
