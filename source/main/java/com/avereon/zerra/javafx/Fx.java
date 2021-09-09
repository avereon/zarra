package com.avereon.zerra.javafx;

import javafx.application.Platform;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Fx {

	public static void startup() {
		try {
			Fx.run( () -> {} );
		} catch( IllegalStateException exception ) {
			Platform.startup( () -> {} );
		}
	}

	// Convenience method to call Platform.runLater
	public static <R extends Runnable> R run( R runnable ) {
		Platform.runLater( runnable );
		return runnable;
	}

	public static boolean isRunning() {
		try {
			Fx.run( () -> {} );
			return true;
		} catch( Throwable throwable ) {
			return false;
		}
	}

	public static boolean isFxThread() {
		return Platform.isFxApplicationThread();
	}

	public static void checkFxThread() {
		if( !isFxThread() ) throw new IllegalStateException( "Not on FX thread; thread=" + Thread.currentThread().getName() );
	}

	public static void waitFor( long timeout ) {
		waitFor( timeout, TimeUnit.MILLISECONDS );
	}

	public static void waitFor( long count, TimeUnit unit ) {
		try {
			doWaitForWithExceptions( count, unit );
		} catch( TimeoutException | InterruptedException exception ) {
			// Intentionally ignore exception
		}
	}

	public static void waitForWithExceptions( long timeout ) throws TimeoutException, InterruptedException {
		waitForWithExceptions( timeout, TimeUnit.MILLISECONDS );
	}

	public static void waitForWithExceptions( long count, TimeUnit unit ) throws TimeoutException, InterruptedException {
		for( int index = 0; index < count; index++ ) {
			doWaitForWithExceptions( count, unit );
		}
	}

	private static void doWaitForWithExceptions( long count, TimeUnit unit ) throws TimeoutException, InterruptedException {
		Semaphore semaphore = new Semaphore( 0 );
		Fx.run( semaphore::release );
		if( !semaphore.tryAcquire( count, unit ) ) throw new TimeoutException( "Timeout waiting for FX" );
	}

}
