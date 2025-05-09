package com.avereon.zerra.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.TimeoutException;

public final class JavaFxStarter extends Application {

	private final static Object startLock = new Object();

	private static Throwable throwable;

	private static boolean started;

	public JavaFxStarter() {}

	private static void setStarted( boolean started ) {
		synchronized( startLock ) {
			JavaFxStarter.started = started;
			startLock.notifyAll();
		}
	}

	public static void startAndWait( long timeout ) throws RuntimeException {
		long limit = System.currentTimeMillis() + timeout;

		synchronized( startLock ) {
			if( started ) return;

			new Thread( () -> {
				try {
					JavaFxStarter.launch();
				} catch( IllegalStateException exception ) {
					// Platform was already started by a different class
					setStarted( true );
				} catch( Throwable throwable ) {
					JavaFxStarter.throwable = throwable;
				}
			} ).start();

			while( !started && throwable == null ) {
				try {
					startLock.wait( timeout );
					if( System.currentTimeMillis() >= limit && throwable == null && !started ) {
						throw new RuntimeException( new TimeoutException( "FX platform start timeout after " + timeout + " ms" ) );
					}
				} catch( Throwable throwable ) {
					JavaFxStarter.throwable = throwable;
				}
			}

			if( throwable != null ) throw new RuntimeException( throwable );
		}
	}

	@Override
	public void start( Stage primaryStage ) {
		setStarted( true );
	}

}
