package com.avereon.zerra.event;

import com.avereon.event.Event;
import com.avereon.event.EventType;
import com.avereon.util.JavaUtil;
import lombok.Getter;

@Getter
public class FxEventWrapper extends Event {

	private final javafx.event.Event fxEvent;

	public FxEventWrapper( javafx.event.Event fxEvent ) {
		super( fxEvent.getSource(), Event.ANY );
		this.fxEvent = fxEvent;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public EventType<? extends FxEventWrapper> getEventType() {
		return (EventType<? extends FxEventWrapper>)super.getEventType();
	}

	@Override
	public String toString() {
		return JavaUtil.getClassName( this ) + ": " + fxEvent;
	}

}
