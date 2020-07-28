package com.avereon.zerra.image;

import javafx.scene.image.Image;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	public String getUrl() {
		return url;
	}

	public ImageIcon setUrl( String url ) {
		this.url = url;
		return this;
	}

	@Override
	protected void render() {
		drawImage( getImage( url ) );
	}

	public Runnable getPreloadRunner() {
		return () -> getImage( url );
	}

	public static void main( String[] commands ) {
		ImageIcon icon = new ImageIcon( "https://www.avereon.com/download/latest/xenon/product/icon" );
		proof( icon );
	}

	private Image getImage( String url ) {
		int w = (int)getWidth();
		int h = (int)getHeight();
		String key = url + "?w=" + w + "&h=" + h;
		Image source = CACHE.computeIfAbsent( key, Image::new );

		// TODO This was an attempt to store pre-scaled images
		//		Image cached = CACHE.computeIfAbsent( key, ( k) -> {
		//			return Images.scaleImage( source, w,h );
		//		} );

		return source;
	}

}
