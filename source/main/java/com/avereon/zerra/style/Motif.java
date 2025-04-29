package com.avereon.zerra.style;

public enum Motif {

	DARK( "-fx-text-background-color: #E0E0E0FF;" ),
	LIGHT( "-fx-text-background-color: #202020FF;" );

	private final String style;

	Motif( String style ) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

}
