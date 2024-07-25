package com.avereon.zarra.image;

import com.avereon.zarra.style.Motif;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public abstract class VectorImage extends Canvas {

	public static final double DEFAULT_SIZE = 256;

	public static final double DEFAULT_GRID = 32;

	public static final double DEFAULT_STROKE_WIDTH = 4.0;

	public static final StrokeLineCap DEFAULT_STROKE_CAP = StrokeLineCap.ROUND;

	public static final StrokeLineJoin DEFAULT_STROKE_JOIN = StrokeLineJoin.ROUND;

	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	private static final CssMetaData<VectorImage, Number> CSS_STROKE_WIDTH;

	private static final CssMetaData<VectorImage, Paint> CSS_STROKE_PAINT;

	private static final CssMetaData<VectorImage, Paint> CSS_PRIMARY_PAINT;

	private static final CssMetaData<VectorImage, Paint> CSS_SECONDARY_PAINT;

	private static final CssMetaData<VectorImage, Font> CSS_FONT;

	// This is set to a bright color to reveal when style is not working right
	private static final Paint DEFAULT_STROKE_PAINT = Color.MAGENTA;

	// This is set to a bright color to reveal when style is not working right
	private static final Paint DEFAULT_PRIMARY_PAINT = Color.RED;

	// This is set to a bright color to reveal when style is not working right
	private static final Paint DEFAULT_SECONDARY_PAINT = Color.YELLOW;

	private static final Font DEFAULT_FONT = Font.getDefault();

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

	private GraphicsContext graphicsContextOverride;

	private Transform baseTransform = new Affine();

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

	private double gridX;

	private double gridY;

	private Motif motif;

	protected VectorImage() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	protected VectorImage( double gridX, double gridY ) {
		this.gridX = gridX;
		this.gridY = gridY;
		resize( DEFAULT_SIZE );
		setTheme( Motif.DARK );
		getStyleClass().add( "xe-image" );
		if( this instanceof IconTag ) IconTag.asIcon( this );
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	public static void proof( VectorImage image ) {
		Proof.proof( image );
	}

	protected void doRender() {
		// Set the defaults
		getGraphicsContext2D().setLineCap( StrokeLineCap.ROUND );
		getGraphicsContext2D().setLineJoin( StrokeLineJoin.ROUND );
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );
		getGraphicsContext2D().setLineWidth( getStrokeWidth() );
		getGraphicsContext2D().setStroke( getStrokePaint() );
		getGraphicsContext2D().setFill( getStrokePaint() );

		// Start rendering by clearing the icon area
		if( graphicsContextOverride == null ) {
			double scaleX = getWidth() / getGridX();
			double scaleY = getHeight() / getGridY();
			baseTransform = Transform.scale( scaleX, scaleY );
			getGraphicsContext2D().clearRect( 0, 0, getGridX(), getGridY() );
			reset();
		}
	}

	/**
	 * Reset the transform back to the initial rendering transform.
	 */
	protected void reset() {
		getGraphicsContext2D().setTransform( new Affine( baseTransform ) );
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

	@Override
	public GraphicsContext getGraphicsContext2D() {
		return graphicsContextOverride == null ? super.getGraphicsContext2D() : graphicsContextOverride;
	}

	void setGraphicsContext2D( GraphicsContext context ) {
		this.graphicsContextOverride = context;
	}

	public Motif getTheme() {
		return motif;
	}

	public void setTheme( Motif motif ) {
		this.motif = motif;
	}

	public void regrid( double width, double height ) {
		setGridX( width );
		setGridY( height );
	}

	public double getGridX() {
		return gridX;
	}

	public void setGridX( double gridX ) {
		this.gridX = gridX;
	}

	public double getGridY() {
		return gridY;
	}

	public void setGridY( double gridY ) {
		this.gridY = gridY;
	}

	public boolean isIcon() {
		return this instanceof IconTag;
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
		Scene scene = Images.getImageScene( this, width, height, null );
		WritableImage snapshot = scene.snapshot( new WritableImage( (int)width, (int)height ) );
		return new WritableImage( snapshot.getPixelReader(), (int)snapshot.getWidth(), (int)snapshot.getHeight() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends VectorImage> T copy() {
		VectorImage copy = null;

		try {
			copy = getClass().getDeclaredConstructor().newInstance();
			copy.getStyleClass().setAll( this.getStyleClass() );
			copy.setHeight( getHeight() );
			copy.setWidth( getWidth() );
			copy.setStyle( getStyle() );
			copy.getProperties().putAll( this.getProperties() );
			copy.strokeWidthOverride = this.strokeWidthOverride;
			copy.strokePaintOverride = this.strokePaintOverride;
			copy.primaryPaintOverride = this.primaryPaintOverride;
			copy.secondaryPaintOverride = this.secondaryPaintOverride;
			copy.gridX = this.gridX;
			copy.gridY = this.gridY;
			copy.motif = this.motif;
		} catch( Exception exception ) {
			log.atWarn( exception ).log( "Unable to copy icon: %s", getClass().getName() );
		}

		return (T)copy;
	}

	void setAndApplyTheme( Motif motif ) {
		setTheme( motif );
		applyTheme();
	}

	private void applyTheme() {
		String style = removeTheme();
		Motif motif = this.motif == null ? Motif.DARK : this.motif;
		setStyle( style == null ? motif.getStyle() : style + motif.getStyle() );
	}

	private String removeTheme() {
		String style = getStyle();
		for( Motif t : Motif.values() ) {
			int index = style.indexOf( t.getStyle() );
			if( index > -1 ) style = style.replace( t.getStyle(), "" );
		}
		setStyle( style );
		return style;
	}

}
