package com.avereon.zerra.javafx;

import com.avereon.event.EventWatcher;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

public class FxEventWatcher extends EventWatcher implements EventHandler<Event> {

	private final Queue<Event> events = new ConcurrentLinkedQueue<>();

	public FxEventWatcher() {
		super();
	}

	public FxEventWatcher( long timeout ) {
		super( timeout );
	}

	@Override
	public synchronized void handle( Event event ) {
		events.offer( event );
		notifyAll();
	}

	public void waitForEvent( EventType<? extends Event> type ) throws InterruptedException, TimeoutException {
		waitForEvent( type, getTimeout() );
	}

	public void waitForNextEvent( EventType<? extends Event> type ) throws InterruptedException, TimeoutException {
		waitForNextEvent( type, getTimeout() );
	}

	/**
	 * Wait for an event of a specific class to occur. If the event has already
	 * occurred this method will return immediately. If the event has not
	 * already occurred then this method waits until the next event occurs, or
	 * the specified timeout, whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	public synchronized void waitForEvent( EventType<? extends Event> type, long timeout ) throws InterruptedException, TimeoutException {
		boolean shouldWait = timeout > 0;
		long start = System.currentTimeMillis();
		long duration = 0;

		while( shouldWait && findNext( type ) == null ) {
			wait( timeout - duration );
			duration = System.currentTimeMillis() - start;
			shouldWait = duration < timeout;
		}
		duration = System.currentTimeMillis() - start;

		if( duration >= timeout ) throw new TimeoutException( "Timeout waiting for event " + type );
	}

	/**
	 * Wait for the next event of a specific class to occur. This method always
	 * waits until the next event occurs, or the specified timeout, whichever
	 * comes first.
	 *
	 * @param type The event class to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	public synchronized void waitForNextEvent( EventType<? extends Event> type, long timeout ) throws InterruptedException, TimeoutException {
		findNext( type );
		waitForEvent( type, timeout );
	}

	private Event findNext( EventType<? extends Event> type ) {
		Event event;
		while( (event = events.poll()) != null ) {
			if( event.getEventType() ==  type ) return event;
		}
		return null;
	}

}
