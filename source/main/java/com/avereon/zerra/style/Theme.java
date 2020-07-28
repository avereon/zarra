package com.avereon.zerra.style;

public enum Theme {

	DARK( "-fx-text-background-color: #E0E0E0FF;" ),
	LIGHT( "-fx-text-background-color: #202020FF;" );

	private final String style;

	Theme( String style ) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

}
