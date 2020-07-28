package com.avereon.zerra.javafx;

import javafx.application.Platform;

public class Fx {

	// Convenience method to call Platform.runLater
	public static void run( Runnable runnable ) {
		Platform.runLater( runnable );
	}

}
