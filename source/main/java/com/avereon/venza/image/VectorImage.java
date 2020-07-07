package com.avereon.venza.image;

import com.avereon.venza.style.Theme;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public abstract class VectorImage extends Canvas {

	public static final double DEFAULT_SIZE = 256;

	private final double gridWidth;

	private final double gridHeight;

	private Theme theme;

	VectorImage() {
		this( 1.0, 1.0 );
	}

	VectorImage( double gridWidth, double gridHeight ) {
		resize( DEFAULT_SIZE );
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		setTheme( Theme.DARK );
		getStyleClass().add( "xe-image" );
		if( this instanceof DefIcon ) DefIcon.asIcon( this );
		parentProperty().addListener( ( v, o, n ) -> { if( n != null ) doRender(); } );
	}

	protected void doRender() {}

	public void setTheme( Theme theme ) {
		this.theme = theme;
	}

	public Theme getTheme() {
		return theme;
	}

	public boolean isIcon() {
		return this instanceof DefIcon;
	}

	public double getSize() {
		return getWidth();
	}

	@SuppressWarnings( "unchecked" )
	public <T extends VectorImage> T resize( double size ) {
		resize( size, size );
		return (T)this;
	}

	@Override
	public void resize( double width, double height ) {
		setWidth( width );
		setHeight( height );
	}

	public double getScaleWidth() {
		return getWidth() / gridWidth;
	}

	public double getScaleHeight() {
		return getHeight() / gridHeight;
	}

	public Image getImage() {
		return getImage( getWidth(), getHeight() );
	}

	/**
	 * Get an image of the rendered ProgramImage. A new image of size width x
	 * height is created and the ProgramImage is rendered on the new image at the
	 * ProgramImage's location.
	 *
	 * @param width The width of the new image, not the ProgramImage width
	 * @param height The height of the new image, not the ProgramImage height
	 * @return An image with the rendered image on it
	 */
	public Image getImage( double width, double height ) {
		// Note that just returning the WritableImage that the snapshot() method
		// creates did not work when used as a Stage icon. However, creating a new
		// WritableImage from the snapshot image seemed to solve the problem. That
		// is why a new Writable image is created instead of just returning the
		// snapshot image.
		WritableImage snapshot = Images.getImageScene( this, width, height, null ).snapshot( new WritableImage( (int)width, (int)height ) );
		return new WritableImage( snapshot.getPixelReader(), (int)snapshot.getWidth(), (int)snapshot.getHeight() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends VectorImage> T copy() {
		VectorImage copy = null;

		try {
			copy = getClass().getDeclaredConstructor().newInstance();
			copy.getProperties().putAll( this.getProperties() );
			copy.setHeight( getHeight() );
			copy.setWidth( getWidth() );
			copy.setStyle( getStyle() );
			//copy.setTheme( getTheme() );
		} catch( Exception exception ) {
			exception.printStackTrace();
		}

		return (T)copy;
	}

	public static void proof( VectorImage image ) {
		Proof.proof( image );
	}

	void setAndApplyTheme( Theme theme ) {
		setTheme( theme );
		applyTheme();
	}

	private void applyTheme() {
		String style = removeTheme();
		Theme theme = this.theme == null ? Theme.DARK : this.theme;
		setStyle( style == null ? theme.getStyle() : style + theme.getStyle() );
	}

	private String removeTheme() {
		String style = getStyle();
		for( Theme t : Theme.values() ) {
			int index = style.indexOf( t.getStyle() );
			if( index > -1 ) style = style.replace( t.getStyle(), "" );
		}
		setStyle( style );
		return style;
	}

}
