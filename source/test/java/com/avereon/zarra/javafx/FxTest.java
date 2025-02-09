package com.avereon.zarra.javafx;

import javafx.geometry.BoundingBox;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class FxTest extends FxPlatformTestCase {

	@Test
	void emptyBounds() {
		assertThat( Fx.EMPTY_BOUNDS ).isEqualTo( new BoundingBox( 0, 0, 0, 0, 0, 0 ) );
	}

	@Test
	void testRun() throws Exception {
		// given
		String message = "Made it!";
		AtomicReference<String> reference = new AtomicReference<>();

		// when
		Fx.run( () -> reference.set( message ) );
		Fx.waitForWithExceptions( 1, TimeUnit.SECONDS );

		// then
		assertThat( reference.get() ).isEqualTo( "Made it!" );
	}

	@Test
	void testRunWithException() throws Exception {
		// given
		String exceptionMessage = "catch me if you can";
		UnhandledExceptionCatcher handler = new UnhandledExceptionCatcher();
		Fx.run( () -> Thread.currentThread().setUncaughtExceptionHandler( handler ) );

		// when
		Fx.run( (Runnable)() -> {throw new RuntimeException( exceptionMessage );} );
		Fx.waitForWithExceptions( 1, TimeUnit.SECONDS );

		// then
		assertThat( handler.getThrowable().getMessage() ).isEqualTo( exceptionMessage );
	}

	@Test
	void testCall() throws Exception {
		// given
		String message = "Made it!";

		// when
		String result = Fx.call( () -> message );

		// then
		assertThat( result ).isEqualTo( "Made it!" );
	}

	@Test
	void testCallWithException() {
		// given
		String exceptionMessage = "catch me if you can";
		UnhandledExceptionCatcher handler = new UnhandledExceptionCatcher();
		Fx.run( () -> Thread.currentThread().setUncaughtExceptionHandler( handler ) );

		// when
		try {
			Fx.call( () -> {throw new RuntimeException( exceptionMessage );} );
			fail();
		} catch( Exception exception ) {
			assertThat( exception.getMessage() ).isEqualTo( exceptionMessage );
		}
	}

	@Getter
	private static class UnhandledExceptionCatcher implements Thread.UncaughtExceptionHandler {

		private Thread thread;

		private Throwable throwable;

		@Override
		public void uncaughtException( Thread thread, Throwable throwable ) {
			this.thread = thread;
			this.throwable = throwable;
		}

	}

}
