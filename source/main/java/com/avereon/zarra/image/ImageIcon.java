package com.avereon.zarra.image;

import javafx.scene.image.Image;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ImageIcon extends RenderedIcon {

	private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

	private String url;

	public ImageIcon() {}

	public ImageIcon( String url ) {
		setUrl( url );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public RenderedIcon copy() {
		ImageIcon image = super.copy();
		image.url = this.url;
		return image;
	}

	public ImageIcon setUrl( String url ) {
		this.url = url;
		return this;
	}

	@Override
	protected void render() {
		drawImage( getImageFromUrl( url ) );
	}

	public Runnable getPreloadRunner() {
		return () -> getImageFromUrl( url );
	}

	public static void main( String[] commands ) {
		ImageIcon icon = new ImageIcon( "https://www.avereon.com/download/latest/xenon/product/icon" );
		proof( icon );
	}

	private Image getImageFromUrl( String url ) {
		int w = (int)getWidth();
		int h = (int)getHeight();
		String key = url + "?w=" + w + "&h=" + h;
		return CACHE.computeIfAbsent( key, ( k ) -> new Image( url, w, h, true, true ) );
	}

}
