package com.avereon.venza.image;

import com.avereon.venza.javafx.JavaFxStarter;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.util.List;

public abstract class RenderedImage extends Canvas {

	public static final double DEFAULT_SIZE = 256;

	protected static final String DARK_THEME = "proof-dark.css";

	protected static final String LIGHT_THEME = "proof-light.css";

	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	private static final CssMetaData<RenderedImage, Paint> CSS_RENDER_PAINT;

	private static final CssMetaData<RenderedImage, Number> CSS_STROKE_WIDTH;

	private static final Paint DEFAULT_RENDER_PAINT = Color.web( "#404040" );

	private static final double DEFAULT_STROKE_WIDTH = 4.0 / 32.0;

	private ObjectProperty<Paint> renderPaint;

	private Paint renderPaintOverride;

	private DoubleProperty strokeWidth;

	private Double strokeWidthOverride;

	private GraphicsContext graphicsContextOverride;

	private Transform baseTransform = new Affine();

	static {
		CSS_RENDER_PAINT = new CssMetaData<>( "-xe-render-paint", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.renderPaint == null || !styleable.renderPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Paint>)styleable.renderPaintProperty();
			}

		};

		CSS_STROKE_WIDTH = new CssMetaData<>( "-xe-stroke-width", StyleConverter.getSizeConverter() ) {

			@Override
			public boolean isSettable( RenderedImage styleable ) {
				return styleable.renderPaint == null || !styleable.renderPaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Number> getStyleableProperty( RenderedImage styleable ) {
				return (StyleableProperty<Number>)styleable.strokeWidthProperty();
			}

		};

		STYLEABLES = List.of( CSS_RENDER_PAINT, CSS_STROKE_WIDTH );
	}

	protected RenderedImage() {
		resize( DEFAULT_SIZE );
		getStyleClass().add( "xe-image" );
		parentProperty().addListener( ( v, o, n ) -> { if( n != null ) fireRender(); } );
	}

	protected abstract void render();

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	public Paint getRenderPaint() {
		return renderPaintOverride != null ? renderPaintOverride : renderPaintProperty().get();
	}

	public void setRenderPaint( Paint paint ) {
		renderPaintOverride = paint;
	}

	public ObjectProperty<Paint> renderPaintProperty() {
		if( renderPaint == null ) {
			renderPaint = new SimpleStyleableObjectProperty<>( CSS_RENDER_PAINT, RenderedImage.this, "outlinePaint", DEFAULT_RENDER_PAINT ) {

				@Override
				protected void invalidated() {
					fireRender();
				}

			};
		}
		return renderPaint;
	}

	public double getStrokeWidth() {
		return strokeWidthOverride != null ? strokeWidthOverride : strokeWidthProperty().get();
	}

	public void setStrokeWidth( double width ) {
		strokeWidthOverride = width == Double.NaN ? null : width;
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
			stage.setScene( getImageScene( image, width, height ) );
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

	protected void startPath() {
		getGraphicsContext2D().beginPath();
	}

	protected void moveTo( double x, double y ) {
		getGraphicsContext2D().moveTo( x, y );
	}

	protected void lineTo( double x, double y ) {
		getGraphicsContext2D().lineTo( x, y );
	}

	protected void closePath() {
		getGraphicsContext2D().closePath();
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

	protected void draw() {
		getGraphicsContext2D().stroke();
	}

	protected void fill() {
		getGraphicsContext2D().fill();
	}

	protected void fireRender() {
		double size = Math.min( getWidth(), getHeight() );

		// Set the defaults
		getGraphicsContext2D().setLineCap( StrokeLineCap.ROUND );
		getGraphicsContext2D().setLineJoin( StrokeLineJoin.ROUND );
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );

		getGraphicsContext2D().setLineWidth( getStrokeWidth() );
		getGraphicsContext2D().setStroke( getRenderPaint() );
		getGraphicsContext2D().setFill( getRenderPaint() );

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

	protected <T extends RenderedImage> T copy() {
		RenderedImage copy = null;

		try {
			copy = getClass().getDeclaredConstructor().newInstance();
			copy.renderPaintOverride = this.renderPaintOverride;
			copy.strokeWidthOverride = this.strokeWidthOverride;
			copy.setHeight( getHeight() );
			copy.setWidth( getWidth() );
			copy.setStyle( getStyle() );
		} catch( Exception exception ) {
			exception.printStackTrace();
		}

		return (T)copy;
	}

	protected double g( double value ) {
		return value / 32d;
	}

	private void setGraphicsContext2D( GraphicsContext context ) {
		this.graphicsContextOverride = context;
	}

	private static Scene getImageScene( RenderedImage image, double imageWidth, double imageHeight ) {
		Pane pane = new Pane( image );
		pane.setBackground( Background.EMPTY );
		pane.setPrefSize( imageWidth, imageHeight );
		Scene scene = new Scene( pane );
		scene.setFill( Color.TRANSPARENT );
		return scene;
	}

}