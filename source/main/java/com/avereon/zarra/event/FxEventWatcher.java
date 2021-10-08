package com.avereon.zarra.event;

import com.avereon.zarra.javafx.Fx;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

public class FxEventWatcher implements EventHandler<Event> {

	private static final long DEFAULT_WAIT_TIMEOUT = 2500;

	private final Queue<Event> events = new ConcurrentLinkedQueue<>();

	private final long timeout;

	public FxEventWatcher() {
		this( DEFAULT_WAIT_TIMEOUT );
	}

	public FxEventWatcher( long timeout ) {
		this.timeout = timeout;
	}

	public long getTimeout() {
		return timeout;
	}

	@Override
	public synchronized void handle( Event event ) {
		events.offer( event );
		notifyAll();
	}

	public List<Event> getEvents() {
		return new ArrayList<>( events );
	}

	public void waitForEvent( EventType<? extends Event> type ) throws InterruptedException, TimeoutException {
		waitForEvent( type, timeout );
		Fx.waitForWithExceptions( timeout );
	}

	@SuppressWarnings( "unused" )
	public void waitForNextEvent( EventType<? extends Event> type ) throws InterruptedException, TimeoutException {
		waitForNextEvent( type, timeout );
	}

	/**
	 * Wait for an event of a specific type to occur. If the event has already occurred this method will return immediately. If the event has not already occurred then this method waits until the next event occurs, or the specified timeout,
	 * whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	public synchronized void waitForEvent( EventType<? extends Event> type, long timeout ) throws InterruptedException, TimeoutException {
		if( timeout <=0 ) return;

		long duration = 0;
		boolean shouldWait = true;
		long start = System.currentTimeMillis();

		while( shouldWait && findNext( type ) == null ) {
			wait( timeout - duration );
			duration = System.currentTimeMillis() - start;
			shouldWait = duration < timeout;
		}

		duration = System.currentTimeMillis() - start;
		if( duration >= timeout ) throw new TimeoutException( "Timeout waiting for event " + type );
	}

	private Event findNext( EventType<? extends Event> type ) {
		Event event;
		while( (event = events.poll()) != null ) {
			if( event.getEventType() == type ) return event;
		}
		return null;
	}

	/**
	 * Wait for the next event of a specific type to occur. This method always waits until the next event occurs, or the specified timeout, whichever comes first.
	 *
	 * @param type The event type to wait for
	 * @param timeout How long, in milliseconds, to wait for the event
	 * @throws InterruptedException If the timeout is exceeded
	 */
	@SuppressWarnings( "SameParameterValue" )
	private synchronized void waitForNextEvent( EventType<? extends Event> type, long timeout ) throws InterruptedException, TimeoutException {
		events.clear();
		waitForEvent( type, timeout );
	}

}
