package com.avereon.zarra.javafx;

import com.avereon.zerra.test.FxPlatformTestCase;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.testfx.assertions.api.Assertions.assertThat;

public class FxTest extends FxPlatformTestCase {

	@Test
	void testRunWithException() throws Exception {
		String exceptionMessage = "catch me if you can";
		UnhandledExceptionCatcher handler = new UnhandledExceptionCatcher();
		Fx.run( () -> Thread.currentThread().setUncaughtExceptionHandler( handler ) );
		Fx.run( () -> {throw new RuntimeException( exceptionMessage );} );
		Fx.waitForWithExceptions( 1, TimeUnit.SECONDS );

		assertThat( handler.getThrowable().getMessage() ).isEqualTo( exceptionMessage );
	}

	private static class UnhandledExceptionCatcher implements Thread.UncaughtExceptionHandler {

		private Thread thread;

		private Throwable throwable;

		@Override
		public void uncaughtException( Thread thread, Throwable throwable ) {
			this.thread = thread;
			this.throwable = throwable;
		}

		public Thread getThread() {
			return thread;
		}

		public Throwable getThrowable() {
			return throwable;
		}
	}
}
