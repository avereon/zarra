package com.avereon.venza.image;

import com.avereon.venza.style.Theme;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;

import java.util.List;

public abstract class VectorImage extends Canvas {

	public static final double DEFAULT_SIZE = 256;

	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	private static final CssMetaData<VectorImage, Number> CSS_STROKE_WIDTH;

	private static final CssMetaData<VectorImage, Paint> CSS_STROKE_PAINT;

	private static final CssMetaData<VectorImage, Paint> CSS_PRIMARY_PAINT;

	private static final CssMetaData<VectorImage, Paint> CSS_SECONDARY_PAINT;

	private static final CssMetaData<VectorImage, Font> CSS_FONT;

	private static final double DEFAULT_STROKE_WIDTH = 4.0 / 32.0;

	// This is set to a bright color to reveal when style is not working right
	private static final Paint DEFAULT_STROKE_PAINT = Color.MAGENTA;

	// This is set to a bright color to reveal when style is not working right
	private static final Paint DEFAULT_PRIMARY_PAINT = Color.RED;

	// This is set to a bright color to reveal when style is not working right
	private static final Paint DEFAULT_SECONDARY_PAINT = Color.YELLOW;

	private static final Font DEFAULT_FONT = Font.getDefault();

	private DoubleProperty strokeWidth;

	private Double strokeWidthOverride;

	private ObjectProperty<Paint> strokePaint;

	private Paint strokePaintOverride;

	private ObjectProperty<Paint> primaryPaint;

	private Paint primaryPaintOverride;

	private ObjectProperty<Paint> secondaryPaint;

	private Paint secondaryPaintOverride;

	private ObjectProperty<Font> font;

	private Font fontOverride;

	private final double gridWidth;

	private final double gridHeight;

	private Theme theme;

	static {
		// Don't forget to update the test style sheets
		CSS_STROKE_WIDTH = new CssMetaData<>( "-fx-stroke-width", StyleConverter.getSizeConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.strokePaint == null || !styleable.strokePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Number> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Number>)styleable.strokeWidthProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_STROKE_PAINT = new CssMetaData<>( "-fx-stroke", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.strokePaint == null || !styleable.strokePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Paint>)styleable.strokePaintProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_PRIMARY_PAINT = new CssMetaData<>( "-fx-primary", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.primaryPaint == null || !styleable.primaryPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Paint>)styleable.primaryPaintProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_SECONDARY_PAINT = new CssMetaData<>( "-fx-secondary", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.secondaryPaint == null || !styleable.secondaryPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Paint>)styleable.secondaryPaintProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_FONT = new CssMetaData<>( "-fx-font", StyleConverter.getFontConverter() ) {

			@Override
			public boolean isSettable( VectorImage styleable ) {
				return styleable.font == null || !styleable.font.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Font> getStyleableProperty( VectorImage styleable ) {
				return (StyleableProperty<Font>)styleable.fontProperty();
			}

		};

		STYLEABLES = List.of( CSS_STROKE_WIDTH, CSS_STROKE_PAINT, CSS_PRIMARY_PAINT, CSS_SECONDARY_PAINT, CSS_FONT );
	}

	VectorImage() {
		this( 1.0, 1.0 );
	}

	VectorImage( double gridWidth, double gridHeight ) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		resize( DEFAULT_SIZE );
		setTheme( Theme.DARK );
		getStyleClass().add( "xe-image" );
		if( this instanceof DefIcon ) DefIcon.asIcon( this );
		parentProperty().addListener( ( v, o, n ) -> { if( n != null ) doRender(); } );
	}

	protected void doRender() {
		// Set the defaults
		getGraphicsContext2D().setLineCap( StrokeLineCap.ROUND );
		getGraphicsContext2D().setLineJoin( StrokeLineJoin.ROUND );
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );
		getGraphicsContext2D().setLineWidth( getStrokeWidth() );
		getGraphicsContext2D().setStroke( getStrokePaint() );
		getGraphicsContext2D().setFill( getStrokePaint() );
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	public double getStrokeWidth() {
		return strokeWidthOverride != null ? strokeWidthOverride : strokeWidthProperty().get();
	}

	public void setStrokeWidth( double width ) {
		strokeWidthOverride = Double.isNaN( width ) ? null : width;
	}

	public DoubleProperty strokeWidthProperty() {
		if( strokeWidth == null ) {
			strokeWidth = new SimpleStyleableDoubleProperty( CSS_STROKE_WIDTH, VectorImage.this, "outlineWidth", DEFAULT_STROKE_WIDTH ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return strokeWidth;
	}

	public Paint getStrokePaint() {
		return strokePaintOverride != null ? strokePaintOverride : strokePaintProperty().get();
	}

	public void setStrokePaint( Paint paint ) {
		strokePaintOverride = paint;
	}

	public ObjectProperty<Paint> strokePaintProperty() {
		if( strokePaint == null ) {
			strokePaint = new SimpleStyleableObjectProperty<>( CSS_STROKE_PAINT, VectorImage.this, "strokePaint", DEFAULT_STROKE_PAINT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return strokePaint;
	}

	public Paint getPrimaryPaint() {
		return primaryPaintOverride != null ? primaryPaintOverride : primaryPaintProperty().get();
	}

	public void setPrimaryPaint( Paint paint ) {
		primaryPaintOverride = paint;
	}

	public ObjectProperty<Paint> primaryPaintProperty() {
		if( primaryPaint == null ) {
			primaryPaint = new SimpleStyleableObjectProperty<>( CSS_PRIMARY_PAINT, VectorImage.this, "primaryPaint", DEFAULT_PRIMARY_PAINT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return primaryPaint;
	}

	public Paint getSecondaryPaint() {
		return secondaryPaintOverride != null ? secondaryPaintOverride : secondaryPaintProperty().get();
	}

	public void setSecondaryPaint( Paint paint ) {
		secondaryPaintOverride = paint;
	}

	public ObjectProperty<Paint> secondaryPaintProperty() {
		if( secondaryPaint == null ) {
			secondaryPaint = new SimpleStyleableObjectProperty<>( CSS_SECONDARY_PAINT, VectorImage.this, "secondaryPaint", DEFAULT_SECONDARY_PAINT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return secondaryPaint;
	}

	public Font getFont() {
		return fontOverride != null ? fontOverride : fontProperty().get();
	}

	public void setFont( Font font ) {
		fontOverride = font;
	}

	public ObjectProperty<Font> fontProperty() {
		if( font == null ) {
			font = new SimpleStyleableObjectProperty<>( CSS_FONT, VectorImage.this, "font", DEFAULT_FONT ) {

				@Override
				protected void invalidated() {
					doRender();
				}

			};
		}
		return font;
	}

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
			copy.strokePaintOverride = this.strokePaintOverride;
			copy.strokeWidthOverride = this.strokeWidthOverride;
			copy.setHeight( getHeight() );
			copy.setWidth( getWidth() );
			copy.setStyle( getStyle() );
			copy.setTheme( getTheme() );
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
