package com.avereon.venza.image;

import com.avereon.venza.font.FontUtil;
import com.avereon.venza.javafx.JavaFxStarter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.util.List;

public abstract class RenderedImage extends Canvas {

	public static final double DEFAULT_SIZE = 256;

	protected static final String DARK_THEME = "-fx-text-background-color: #E0E0E0FF;";

	protected static final String LIGHT_THEME = "-fx-text-background-color: #202020FF;";

	private static final String STYLESHEET = "venza.css";

	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	private static final CssMetaData<RenderedImage, Number> CSS_STROKE_WIDTH;

	private static final CssMetaData<RenderedImage, Paint> CSS_STROKE_PAINT;

	private static final CssMetaData<RenderedImage, Paint> CSS_PRIMARY_PAINT;

	private static final CssMetaData<RenderedImage, Paint> CSS_SECONDARY_PAINT;

	private static final CssMetaData<RenderedImage, Font> CSS_FONT;

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

	private GraphicsContext graphicsContextOverride;

	private Transform baseTransform = new Affine();

	static {
		// Don't forget to update the test style sheets
		CSS_STROKE_WIDTH = new CssMetaData<>( "-fx-stroke-width", StyleConverter.getSizeConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.strokePaint == null || !styleable.strokePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Number> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Number>)styleable.strokeWidthProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_STROKE_PAINT = new CssMetaData<>( "-fx-stroke", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.strokePaint == null || !styleable.strokePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Paint>)styleable.strokePaintProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_PRIMARY_PAINT = new CssMetaData<>( "-fx-primary", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.primaryPaint == null || !styleable.primaryPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Paint>)styleable.primaryPaintProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_SECONDARY_PAINT = new CssMetaData<>( "-fx-secondary", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.secondaryPaint == null || !styleable.secondaryPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Paint>)styleable.secondaryPaintProperty();
			}

		};

		// Don't forget to update the test style sheets
		CSS_FONT = new CssMetaData<>( "-fx-font", StyleConverter.getFontConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.font == null || !styleable.font.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Font> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Font>)styleable.fontProperty();
			}

		};

		STYLEABLES = List.of( CSS_STROKE_WIDTH, CSS_STROKE_PAINT, CSS_PRIMARY_PAINT, CSS_SECONDARY_PAINT, CSS_FONT );
	}

	protected RenderedImage() {
		resize( DEFAULT_SIZE );
		getStyleClass().add( "xe-image" );
		parentProperty().addListener( ( v, o, n ) -> { if( n != null ) fireRender(); } );
	}

	protected abstract void render();

	public void setTheme( String theme ) {
		setStyle( theme );
	}

	public String getTheme() {
		return getStyle();
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
			strokeWidth = new SimpleStyleableDoubleProperty( CSS_STROKE_WIDTH, RenderedImage.this, "outlineWidth", DEFAULT_STROKE_WIDTH ) {

				@Override
				protected void invalidated() {
					fireRender();
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
			strokePaint = new SimpleStyleableObjectProperty<>( CSS_STROKE_PAINT, RenderedImage.this, "strokePaint", DEFAULT_STROKE_PAINT ) {

				@Override
				protected void invalidated() {
					fireRender();
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
			primaryPaint = new SimpleStyleableObjectProperty<>( CSS_PRIMARY_PAINT, RenderedImage.this, "primaryPaint", DEFAULT_PRIMARY_PAINT ) {

				@Override
				protected void invalidated() {
					fireRender();
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
			secondaryPaint = new SimpleStyleableObjectProperty<>( CSS_SECONDARY_PAINT, RenderedImage.this, "secondaryPaint", DEFAULT_SECONDARY_PAINT ) {

				@Override
				protected void invalidated() {
					fireRender();
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
			font = new SimpleStyleableObjectProperty<>( CSS_FONT, RenderedImage.this, "font", DEFAULT_FONT ) {

				@Override
				protected void invalidated() {
					fireRender();
				}

			};
		}
		return font;
	}

	@SuppressWarnings( "unchecked" )
	public <T extends RenderedImage> T resize( double size ) {
		resize( size, size );
		return (T)this;
	}

	@Override
	public void resize( double width, double height ) {
		setWidth( width );
		setHeight( height );
	}

	@Override
	public GraphicsContext getGraphicsContext2D() {
		return graphicsContextOverride == null ? super.getGraphicsContext2D() : graphicsContextOverride;
	}

	public static void proof( RenderedImage image ) {
		proof( image, null );
	}

	public static void proof( RenderedImage image, Paint fill ) {
		proof( image, image.getWidth(), image.getHeight(), fill );
	}

	public static void proof( RenderedImage image, double width, double height ) {
		proof( image, width, height, null );
	}

	public static void proof( RenderedImage image, double width, double height, Paint fill ) {
		JavaFxStarter.startAndWait( 1000 );

		Platform.runLater( () -> {
			Stage stage = new Stage();
			stage.setTitle( image.getClass().getSimpleName() );
			stage.setScene( getImageScene( image, width, height, fill ) );
			// The following line causes the stage not to show on Linux
			//stage.setResizable( false );
			stage.centerOnScreen();
			stage.sizeToScene();
			stage.show();
		} );
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
		WritableImage snapshot = getImageScene( this, width, height ).snapshot( new WritableImage( (int)width, (int)height ) );
		return new WritableImage( snapshot.getPixelReader(), (int)snapshot.getWidth(), (int)snapshot.getHeight() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends RenderedImage> T copy() {
		RenderedImage copy = null;

		try {
			copy = getClass().getDeclaredConstructor().newInstance();
			copy.strokePaintOverride = this.strokePaintOverride;
			copy.strokeWidthOverride = this.strokeWidthOverride;
			copy.getProperties().putAll( this.getProperties() );
			copy.setHeight( getHeight() );
			copy.setWidth( getWidth() );
			copy.setStyle( getStyle() );
		} catch( Exception exception ) {
			exception.printStackTrace();
		}

		return (T)copy;
	}

	/**
	 * Reset the transform back to the initial rendering transform. This
	 * effectively clears any changes made by the {@link #move} and {@link #spin}
	 * and {@link #zoom} methods.
	 */
	protected void reset() {
		getGraphicsContext2D().setTransform( new Affine( baseTransform ) );
	}

	protected void move( double x, double y ) {
		getGraphicsContext2D().translate( x, y );
	}

	protected void zoom( double x, double y ) {
		getGraphicsContext2D().scale( x, y );
	}

	protected void spin( double x, double y, double a ) {
		move( x, y );
		getGraphicsContext2D().rotate( a );
		move( -x, -y );
	}

	protected void clip() {
		getGraphicsContext2D().clip();
	}

	protected void save() {
		getGraphicsContext2D().save();
	}

	protected void restore() {
		getGraphicsContext2D().restore();
	}

	protected void startPath() {
		getGraphicsContext2D().beginPath();
	}

	protected void startPath( double x, double y ) {
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( x, y );
	}

	protected void moveTo( double x, double y ) {
		getGraphicsContext2D().moveTo( x, y );
	}

	protected void lineTo( double x, double y ) {
		getGraphicsContext2D().lineTo( x, y );
	}

	protected void addArc( double cx, double cy, double rx, double ry, double start, double extent ) {
		getGraphicsContext2D().arc( cx, cy, rx, ry, start, extent );
	}

	protected void addOval( double cx, double cy, double rx, double ry ) {
		getGraphicsContext2D().moveTo( cx + rx, cy );
		getGraphicsContext2D().arc( cx, cy, rx, ry, 0, 360 );
	}

	protected void addDot( double cx, double cy ) {
		addOval( cx, cy, g( 1 ), g( 1 ) );
	}

	protected void addLine( double x1, double y1, double x2, double y2 ) {
		moveTo( x1, y1 );
		lineTo( x2, y2 );
	}

	protected void addRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().rect( x, y, w, h );
	}

	protected void curveTo( double xc, double yc, double x1, double y1 ) {
		getGraphicsContext2D().quadraticCurveTo( xc, yc, x1, y1 );
	}

	protected void curveTo( double xc1, double yc1, double xc2, double yc2, double x1, double y1 ) {
		getGraphicsContext2D().bezierCurveTo( xc1, yc1, xc2, yc2, x1, y1 );
	}

	protected void closePath() {
		getGraphicsContext2D().closePath();
	}

	protected void fillText( String text, double x, double y, double textSize ) {
		fillText( text, x, y, textSize, -1 );
	}

	protected void fillText( String text, double x, double y, double textSize, double maxWidth ) {
		renderText( text, x, y, textSize, maxWidth, false );
	}

	protected void drawText( String text, double x, double y, double textSize ) {
		fillText( text, x, y, textSize, -1 );
	}

	protected void drawText( String text, double x, double y, double textSize, double maxWidth ) {
		renderText( text, x, y, textSize, maxWidth, true );
	}

	protected Paint linearPaint( double x1, double y1, double x2, double y2, Stop... stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint linearPaint( double x1, double y1, double x2, double y2, List<Stop> stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint radialPaint( double x, double y, double r, Stop... stops ) {
		return new RadialGradient( 0, 0, x, y, r, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint radialPaint( double x, double y, double r, List<Stop> stops ) {
		return new RadialGradient( 0, 0, x, y, r, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Font deriveFont( Font font, double size ) {
		if( font == null ) font = getGraphicsContext2D().getFont();
		FontWeight fontWeight = FontUtil.getFontWeight( font.getStyle() );
		FontPosture fontPosture = FontUtil.getFontPosture( font.getStyle() );
		return Font.font( font.getFamily(), fontWeight, fontPosture, size );
	}

	protected void setDrawCap( StrokeLineCap cap ) {
		getGraphicsContext2D().setLineCap( cap );
	}

	protected void setDrawJoin( StrokeLineJoin join ) {
		getGraphicsContext2D().setLineJoin( join );
	}

	protected void setDrawWidth( double width ) {
		getGraphicsContext2D().setLineWidth( width );
	}

	protected void setDrawPaint( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
	}

	protected void setFillPaint( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
	}

	protected void setTextAlign( TextAlignment alignment ) {
		getGraphicsContext2D().setTextAlign( alignment );
	}

	protected void setTextBaseLine( VPos baseline ) {
		getGraphicsContext2D().setTextBaseline( baseline );
	}

	protected void drawImage( Image image ) {
		drawImage( image, 0, 0 );
	}

	protected void drawImage( Image image, double x, double y ) {
		getGraphicsContext2D().drawImage( image, x, y, 1, 1 );
	}

	protected void clearRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().clearRect( x, y, w, h );
	}

	protected void draw() {
		getGraphicsContext2D().stroke();
	}

	protected void fill() {
		getGraphicsContext2D().fill();
	}

	protected void fill( Paint paint ) {
		getGraphicsContext2D().save();
		getGraphicsContext2D().setFill( paint );
		getGraphicsContext2D().fill();
		getGraphicsContext2D().restore();
	}

	protected void render( RenderedImage image ) {
		image.setGraphicsContext2D( getGraphicsContext2D() );
		image.setStrokePaint( getStrokePaint() );
		image.setPrimaryPaint( getPrimaryPaint() );
		image.setSecondaryPaint( getSecondaryPaint() );
		image.render();
	}

	protected void fireRender() {
		double size = Math.min( getWidth(), getHeight() );

		// Set the defaults
		getGraphicsContext2D().setLineCap( StrokeLineCap.ROUND );
		getGraphicsContext2D().setLineJoin( StrokeLineJoin.ROUND );
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );

		getGraphicsContext2D().setLineWidth( getStrokeWidth() );
		getGraphicsContext2D().setStroke( getStrokePaint() );
		getGraphicsContext2D().setFill( getStrokePaint() );

		// Start rendering by clearing the icon area
		if( graphicsContextOverride == null ) {
			getGraphicsContext2D().clearRect( 0, 0, 1, 1 );
			getGraphicsContext2D().setTransform( new Affine() );
			baseTransform = Transform.scale( size, size );
			reset();
		}

		//		// Just for research, set different color backgrounds per size
		//		if( size == 16 ) protected void setFillColor( Color.PURPLE );
		//		if( size == 24 ) protected void setFillColor( Color.BLUE );
		//		if( size == 32 ) protected void setFillColor( Color.GREEN );
		//		if( size == 64 ) protected void setFillColor( Color.YELLOW );
		//		if( size == 128 ) protected void setFillColor( Color.ORANGE );
		//		if( size == 256 ) protected void setFillColor( Color.RED );
		//		protected void fillRect( 0, 0, getWidth(), getHeight() );

		render();
	}

	protected double g( double value ) {
		return value / 32d;
	}

	private void setGraphicsContext2D( GraphicsContext context ) {
		this.graphicsContextOverride = context;
	}

	private static Scene getImageScene( RenderedImage image, double imageWidth, double imageHeight ) {
		return getImageScene( image, imageWidth, imageHeight, null );
	}

	private static Scene getImageScene( RenderedImage image, double imageWidth, double imageHeight, Paint fill ) {
		Pane pane = new Pane( image );
		pane.setBackground( Background.EMPTY );
		pane.setPrefSize( imageWidth, imageHeight );
		applyContainerStylesheets( image, pane );
		Scene scene = new Scene( pane );
		scene.setFill( fill == null ? Color.TRANSPARENT : fill );
		return scene;
	}

	protected static void applyContainerStylesheets( RenderedImage image, Parent node ) {
		// Add the default container stylesheet
		node.getStylesheets().add( STYLESHEET );

		// Add extra container style
		String style = (String)image.getProperties().get( "container-style" );
		if( style != null ) node.setStyle( style );
	}

	private void renderText( String text, double x, double y, double textSize, double maxWidth, boolean draw ) {
		// Font sizes smaller than one don't scale as expected
		// so the workaround is to scale according the text size
		// and divide the coordinates by the size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale the transform
		Affine transform = getGraphicsContext2D().getTransform().clone();
		getGraphicsContext2D().scale( scale, scale );

		getGraphicsContext2D().setFont( deriveFont( getFont(), fontSize ) );
		getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.GRAY );

		// Stroke or fill the text
		if( draw ) {
			if( maxWidth < 0 ) {
				getGraphicsContext2D().strokeText( text, x / scale, y / scale );
			} else {
				getGraphicsContext2D().strokeText( text, x / scale, y / scale, maxWidth / scale );
			}
		} else {
			if( maxWidth < 0 ) {
				getGraphicsContext2D().fillText( text, x / scale, y / scale );
			} else {
				getGraphicsContext2D().fillText( text, x / scale, y / scale, maxWidth / scale );
			}
		}

		// Reset transform
		getGraphicsContext2D().setTransform( transform );
	}

}
