package com.avereon.venza.image;

import com.avereon.venza.color.ColorTheme;
import com.avereon.venza.color.Colors;
import com.avereon.venza.font.FontUtil;
import com.avereon.venza.javafx.JavaFxStarter;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.css.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import javafx.embed.swing.SwingFXUtils;

//import javafx.embed.swing.SwingFXUtils;

/*
Conversion chart

A ----- 0
    J - 2
  F --- 4
    K - 6
D ----- 8
    L - 10
  G --- 12
    M - 14
C ----- 16
    N - 18
  H --- 20
    O - 22
E ----- 24
    P - 26
  I --- 28
    Q - 30
B ----- 32

ZA - 1
ZB - 3
ZC - 5
ZD - 7
ZE - 9
ZF - 11
ZG - 13
ZH - 15
ZI - 17
ZJ - 19
ZK - 21
ZL - 23
ZM - 25
ZN - 27
ZO - 29
ZP - 31
*/
public abstract class ProgramImage extends Canvas {

	protected enum GradientTone {
		LIGHT,
		MEDIUM,
		DARK
	}

	protected static final double RADIANS_PER_DEGREE = Math.PI / 180;

	protected static final double DEGREES_PER_RADIAN = 180 / Math.PI;

	protected static final String TEST_STYLE = "-xe-theme-color: magenta; -xe-outline-paint: purple;";

	private static final double DEFAULT_SIZE = 256;

	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	private static final CssMetaData<ProgramImage, Color> THEME_COLOR;

	private static final CssMetaData<ProgramImage, Paint> OUTLINE_PAINT;

	private static final CssMetaData<ProgramImage, Number> OUTLINE_WIDTH;

	private static final Color DEFAULT_THEME_COLOR = Color.web( "#c0c0c0");

	private static final Paint DEFAULT_OUTLINE_PAINT = Color.web( "#404040");

	private static final double DEFAULT_OUTLINE_WIDTH = 1.0 / 32.0;

	@Deprecated
	private static ColorTheme DEFAULT_COLOR_THEME = new ColorTheme( DEFAULT_THEME_COLOR );

	private ObjectProperty<Color> themeColor;

	private Color themeColorOverride;

	private ObjectProperty<Paint> outlinePaint;

	private Paint outlinePaintOverride;

	private DoubleProperty outlineWidth;

	private Double outlineWidthOverride;

	//private ColorTheme colorTheme;

	private Font font;

	private GraphicsContext graphicsContextOverride;

	private Transform baseTransform = new Affine();

	static {
		THEME_COLOR = new CssMetaData<>( "-xe-theme-color", StyleConverter.getColorConverter() ) {

			@Override
			public boolean isSettable( ProgramImage styleable ) {
				return styleable.themeColor == null || !styleable.themeColor.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Color> getStyleableProperty( ProgramImage styleable ) {
				return (StyleableProperty<Color>)styleable.themeColorProperty();
			}

		};

		OUTLINE_PAINT = new CssMetaData<>( "-xe-outline-paint", StyleConverter.getPaintConverter() ) {

			@Override
			public boolean isSettable( ProgramImage styleable ) {
				return styleable.outlinePaint == null || !styleable.outlinePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Paint> getStyleableProperty( ProgramImage styleable ) {
				return (StyleableProperty<Paint>)styleable.outlinePaintProperty();
			}

		};

		OUTLINE_WIDTH = new CssMetaData<>( "-xe-outline-width", StyleConverter.getSizeConverter() ) {

			@Override
			public boolean isSettable( ProgramImage styleable ) {
				return styleable.outlinePaint == null || !styleable.outlinePaint.isBound();
			}

			@Override
			@SuppressWarnings( "unchecked" )
			public StyleableProperty<Number> getStyleableProperty( ProgramImage styleable ) {
				return (StyleableProperty<Number>)styleable.outlineWidthProperty();
			}

		};

		STYLEABLES = List.of( THEME_COLOR, OUTLINE_PAINT, OUTLINE_WIDTH );
	}

	public ProgramImage() {
		setSize( DEFAULT_SIZE );
		getStyleClass().add( "xe-image" );
		parentProperty().addListener( ( v, o, n ) -> { if( n != null ) fireRender(); } );
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

	public Color getThemeColor() {
		return themeColorOverride != null ? themeColorOverride : themeColorProperty().get();
	}

	public void setThemeColor( Color color ) {
		themeColorOverride = color;
	}

	public ObjectProperty<Color> themeColorProperty() {
		if( themeColor == null ) {
			themeColor = new SimpleStyleableObjectProperty<>( THEME_COLOR, ProgramImage.this, "themeColor", DEFAULT_THEME_COLOR ) {

				@Override
				protected void invalidated() {
					fireRender();
				}

			};
		}
		return themeColor;
	}

	public Paint getOutlinePaint() {
		return outlinePaintOverride != null ? outlinePaintOverride : outlinePaintProperty().get();
	}

	public void setOutlinePaint( Paint paint ) {
		outlinePaintOverride = paint;
	}

	public ObjectProperty<Paint> outlinePaintProperty() {
		if( outlinePaint == null ) {
			outlinePaint = new SimpleStyleableObjectProperty<>( OUTLINE_PAINT, ProgramImage.this, "outlinePaint", DEFAULT_OUTLINE_PAINT ) {

				@Override
				protected void invalidated() {
					fireRender();
				}

			};
		}
		return outlinePaint;
	}

	public double getOutlineWidth() {
		return outlineWidthOverride != null ? outlineWidthOverride : outlineWidthProperty().get();
	}

	public void setOutlineWidth( double width ) {
		outlineWidthOverride = width == Double.NaN ? null : width;
	}

	public DoubleProperty outlineWidthProperty() {
		if( outlineWidth == null ) {
			outlineWidth = new SimpleStyleableDoubleProperty( OUTLINE_WIDTH, ProgramImage.this, "outlineWidth", DEFAULT_OUTLINE_WIDTH ) {

				@Override
				protected void invalidated() {
					fireRender();
				}

			};
		}
		return outlineWidth;
	}

	public ProgramImage setSize( double size ) {
		setHeight( size );
		setWidth( size );
		return this;
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
		WritableImage snapshot = getImageScene( width, height ).snapshot( new WritableImage( (int)width, (int)height ) );
		return new WritableImage( snapshot.getPixelReader(), (int)snapshot.getWidth(), (int)snapshot.getHeight() );
	}

	public static void wrapup() {
		Platform.runLater( () -> {if( Window.getWindows().size() == 0 ) Platform.exit();} );
	}

	public static void proof( ProgramImage image ) {
		proof( image, null );
	}

	public static void proof( ProgramImage image, Paint fill ) {
		proof( image, image.getWidth(), image.getHeight(), fill );
	}

	public static void proof( ProgramImage image, double width, double height ) {
		proof( image, width, height, null );
	}

	public static void proof( ProgramImage image, double width, double height, Paint fill ) {
		JavaFxStarter.startAndWait( 1000 );

		Platform.runLater( () -> {
			//Scene scene = new Scene( new VBox( image ) );
			Scene scene = image.getImageScene( width, height );
			if( fill != null ) scene.setFill( fill );

			Stage stage = new Stage();
			stage.setTitle( image.getClass().getSimpleName() );
			stage.setScene( scene );
			stage.setResizable( true );
			stage.centerOnScreen();
			stage.sizeToScene();
			stage.show();
		} );
	}

	public static void proof( ProgramIcon icon ) {
		proof( icon, null, null );
	}

	public static void proof( ProgramIcon icon, Paint fill ) {
		proof( icon, null, fill );
	}

	public static void proof( ProgramIcon icon, String style ) {
		proof( icon, style, null );
	}

	public static void proof( ProgramIcon icon, String style, Paint fill ) {
		if( style != null ) icon.setStyle( style );

		JavaFxStarter.startAndWait( 1000 );

		// Now show the icon window
		Platform.runLater( () -> {
			String title = icon.getClass().getSimpleName();

			ImageView imageView16 = new ImageView( Images.resample( icon.copy().setSize( 16 ).getImage(), 16 ) );
			ImageView imageView32 = new ImageView( Images.resample( icon.copy().setSize( 32 ).getImage(), 8 ) );

			ProgramImage icon128 = icon.copy().setSize( 128 );
			AnchorPane.setTopAnchor( icon128, 0.0 );
			AnchorPane.setLeftAnchor( icon128, 0.0 );

			ProgramImage icon64 = icon.copy().setSize( 64 );
			AnchorPane.setTopAnchor( icon64, 128.0 );
			AnchorPane.setLeftAnchor( icon64, 128.0 );

			ProgramImage icon32 = icon.copy().setSize( 32 );
			AnchorPane.setTopAnchor( icon32, 192.0 );
			AnchorPane.setLeftAnchor( icon32, 192.0 );

			ProgramImage icon16 = icon.copy().setSize( 16 );
			AnchorPane.setTopAnchor( icon16, 224.0 );
			AnchorPane.setLeftAnchor( icon16, 224.0 );

			ProgramImage icon8 = icon.copy().setSize( 8 );
			AnchorPane.setTopAnchor( icon8, 240.0 );
			AnchorPane.setLeftAnchor( icon8, 240.0 );

			AnchorPane iconPane = new AnchorPane();
			iconPane.getChildren().addAll( icon128, icon64, icon32, icon16, icon8 );

			GridPane pane = new GridPane();
			pane.add( icon.setSize( DEFAULT_SIZE ), 1, 1 );
			pane.add( imageView16, 2, 1 );
			pane.add( imageView32, 2, 2 );
			pane.add( iconPane, 1, 2 );

			List<Image> stageIcons = new ArrayList<>();
			stageIcons.add( icon.copy().setSize( 256 ).getImage() );
			stageIcons.add( icon.copy().setSize( 128 ).getImage() );
			stageIcons.add( icon.copy().setSize( 64 ).getImage() );
			stageIcons.add( icon.copy().setSize( 48 ).getImage() );
			stageIcons.add( icon.copy().setSize( 32 ).getImage() );
			stageIcons.add( icon.copy().setSize( 16 ).getImage() );

			Scene scene = new Scene( pane );
			if( fill != null ) scene.setFill( fill );

			Stage stage = new Stage();
			stage.setTitle( title );
			stage.getIcons().addAll( stageIcons );
			stage.setScene( scene );

			// The following line causes the stage not to show on Linux
			//stage.setResizable( false );

			stage.sizeToScene();
			stage.centerOnScreen();
			stage.show();
		} );
	}

	public static void save( ProgramImage icon, String path ) {
		try {
			save( icon, new File( path ).getCanonicalFile() );
		} catch( Exception exception ) {
			exception.printStackTrace();
		}
	}

	public static void save( ProgramImage icon, File path ) {
		JavaFxStarter.startAndWait( 1000 );

		// Render and save the icon
		Platform.runLater( () -> {
			try {
				path.getParentFile().mkdirs();
				ImageIO.write( SwingFXUtils.fromFXImage( icon.getImage(), null ), "png", path );
			} catch( Exception exception ) {
				exception.printStackTrace();
			}
		} );
	}

	protected abstract void render();

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
		getGraphicsContext2D().moveTo( xformX( x ), xformY( y ) );
	}

	protected void lineTo( double x, double y ) {
		getGraphicsContext2D().lineTo( xformX( x ), xformY( y ) );
	}

	protected void addArc( double cx, double cy, double rx, double ry, double start, double extent ) {
		getGraphicsContext2D().arc( xformX( cx ), xformY( cy ), xformX( rx ), xformY( ry ), start, extent );
	}

	protected void addRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().rect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void addOval( double cx, double cy, double rx, double ry ) {
		getGraphicsContext2D().arc( xformX( cx ), xformY( cy ), xformX( rx ), xformY( ry ), 0, 360 );
	}

	protected void curveTo( double xc, double yc, double x1, double y1 ) {
		getGraphicsContext2D().quadraticCurveTo( xformX( xc ), xformY( yc ), xformX( x1 ), xformY( y1 ) );
	}

	protected void curveTo( double xc1, double yc1, double xc2, double yc2, double x1, double y1 ) {
		getGraphicsContext2D().bezierCurveTo( xformX( xc1 ), xformY( yc1 ), xformX( xc2 ), xformY( yc2 ), xformX( x1 ), xformY( y1 ) );
	}

	protected void closePath() {
		getGraphicsContext2D().closePath();
	}

	@Override
	public GraphicsContext getGraphicsContext2D() {
		return graphicsContextOverride == null ? super.getGraphicsContext2D() : graphicsContextOverride;
	}

	public void setGraphicsContext2D( GraphicsContext context ) {
		this.graphicsContextOverride = context;
	}

	//	protected void setDrawWidth( double width ) {
	//		getGraphicsContext2D().setLineWidth( xformX( width ) );
	//	}
	//
	protected void setLineCap( StrokeLineCap cap ) {
		getGraphicsContext2D().setLineCap( cap );
	}

	protected void setLineJoin( StrokeLineJoin join ) {
		getGraphicsContext2D().setLineJoin( join );
	}

	protected void setDrawWidth( double width ) {
		getGraphicsContext2D().setLineWidth( width );
	}

	protected void setDrawPaint( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
	}

	protected void setFillTone( GradientTone shade ) {
		setFillPaint( getIconFillPaint( shade ) );
	}

	protected void setFillPaint( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
	}

	protected void setFillRule( FillRule rule ) {
		getGraphicsContext2D().setFillRule( rule );
	}

	protected void setTextAlign( TextAlignment alignment ) {
		getGraphicsContext2D().setTextAlign( alignment );
	}

	protected void setTextBaseLine( VPos baseline ) {
		getGraphicsContext2D().setTextBaseline( baseline );
	}

	protected Font getFont() {
		return font;
	}

	protected void setFont( Font font ) {
		this.font = font;
	}

	protected void fillAndDraw() {
		fill();
		draw();
	}

	protected void fillAndDraw( Paint fillPaint ) {
		fill( fillPaint );
		draw();
	}

	protected void fillAndDraw( GradientTone shade ) {
		fillAndDraw( getIconFillPaint( shade ) );
	}

	protected void fillAndDraw( Paint fillPaint, Paint drawPaint ) {
		fill( fillPaint );
		draw( drawPaint );
	}

	protected void fillAndDraw( GradientTone shade, Paint drawPaint ) {
		fill( getIconFillPaint( shade ) );
		draw( drawPaint );
	}

	protected void fill() {
		getGraphicsContext2D().fill();
	}

	protected void fill( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
		getGraphicsContext2D().fill();
		getGraphicsContext2D().setFill( getIconFillPaint() );
	}

	protected void fill( GradientTone shade ) {
		fill( getIconFillPaint( shade ) );
	}

	protected void draw() {
		getGraphicsContext2D().stroke();
	}

	protected void draw( Paint paint ) {
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().stroke();
		getGraphicsContext2D().setStroke( getOutlinePaint() );
	}

	protected void draw( ProgramImage image ) {
		image.setGraphicsContext2D( getGraphicsContext2D() );
		image.fireRender();
	}

	protected void drawDot( double x, double y ) {
		double offset = 0.5 * getIconDrawWidth();
		drawOval( x - offset, y - offset, offset * 2, offset * 2 );
	}

	protected void fillRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().fillRect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void fillOval( double x, double y, double w, double h ) {
		getGraphicsContext2D().fillOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void fillCenteredOval( double cx, double cy, double rx, double ry ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;
		getGraphicsContext2D().fillOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void fillText( String text, double x, double y, double textSize ) {
		fillText( text, x, y, textSize, 0 );
	}

	protected void fillText( String text, double x, double y, double textSize, double maxWidth ) {
		// Font sizes smaller than one don't scale as expected
		// so the workaround is to scale according the text size
		// and divide the coordinates by the size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale the transform
		Affine transform = getGraphicsContext2D().getTransform().clone();
		getGraphicsContext2D().scale( scale, scale );

		// Draw the text
		getGraphicsContext2D().setFont( deriveFont( font, fontSize ) );
		getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.GRAY );
		if( maxWidth == 0 ) {
			getGraphicsContext2D().fillText( text, x / scale, y / scale );
		} else {
			getGraphicsContext2D().fillText( text, x / scale, y / scale, maxWidth / scale );
		}

		// Reset transform
		getGraphicsContext2D().setTransform( transform );
	}

	protected void drawLine( double x1, double y1, double x2, double y2 ) {
		getGraphicsContext2D().strokeLine( xformX( x1 ), xformY( y1 ), xformX( x2 ), xformY( y2 ) );
	}

	protected void drawRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().strokeRect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void drawOval( double x, double y, double w, double h ) {
		getGraphicsContext2D().strokeOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected void drawCenteredOval( double cx, double cy, double rx, double ry ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;
		getGraphicsContext2D().strokeOval( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	/**
	 * @param cx
	 * @param cy
	 * @param rx
	 * @param ry
	 * @param start The start of the angle in degrees
	 * @param extent The extend of the angle in degrees
	 * @param type
	 */
	protected void drawCenteredArc( double cx, double cy, double rx, double ry, double start, double extent, ArcType type ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;
		getGraphicsContext2D().strokeArc( xformX( x ), xformY( y ), xformX( w ), xformY( h ), start, extent, type );
	}

	protected void drawText( String text, double x, double y, double textSize ) {
		drawText( text, x, y, textSize, 0 );
	}

	protected void drawText( String text, double x, double y, double textSize, double maxWidth ) {
		// Font sizes smaller than one don't scale as expected
		// so the workaround is to scale according the text size
		// and divide the coordinates by the size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale line width
		double lineWidth = getGraphicsContext2D().getLineWidth();
		getGraphicsContext2D().setLineWidth( lineWidth / scale );

		// Scale the transform
		Affine transform = getGraphicsContext2D().getTransform().clone();
		getGraphicsContext2D().scale( scale, scale );

		// Draw the text
		getGraphicsContext2D().setFont( deriveFont( font, fontSize ) );
		getGraphicsContext2D().setFontSmoothingType( FontSmoothingType.GRAY );
		if( maxWidth == 0 ) {
			getGraphicsContext2D().strokeText( text, x / scale, y / scale );
		} else {
			getGraphicsContext2D().strokeText( text, x / scale, y / scale, maxWidth / scale );
		}

		// Reset transform
		getGraphicsContext2D().setTransform( transform );

		// Reset line width
		getGraphicsContext2D().setLineWidth( lineWidth );
	}

	protected void drawImage( Image image ) {
		drawImage( image, 0, 0 );
	}

	protected void drawImage( Image image, double x, double y ) {
		getGraphicsContext2D().drawImage( image, x, y, 1, 1 );
	}

	protected void clearRect( double x, double y, double w, double h ) {
		getGraphicsContext2D().clearRect( xformX( x ), xformY( y ), xformX( w ), xformY( h ) );
	}

	protected double xformX( double value ) {
		//return size * (value + xOffset);
		//return size * value;
		return value;
	}

	protected double xformY( double value ) {
		//return size * (value + yOffset);
		//return size * value;
		return value;
	}

	protected double g( double value ) {
		return value / 32d;
	}

	protected double getIconDrawWidth() {
		return DEFAULT_OUTLINE_WIDTH;
	}

	protected Paint getIconDrawColor() {
		//return DEFAULT_OUTLINE_COLOR;
		//return getThemeDrawColor();
		return getOutlinePaint();
	}

	protected Color getIconFillColor() {
		return getThemeFillColor();
	}

	protected Paint getIconFillPaint() {
		return getIconFillPaint( GradientTone.MEDIUM );
	}

	protected Paint getIconFillPaint( GradientTone tone ) {
		return getIconFillPaint( getIconFillColor(), tone );
	}

	protected Paint getIconFillPaint( Color color, GradientTone tone ) {
		// The gradient range factor 0 to 2
		// A value of 0 is just the color with no gradient at all
		// A value of 2 is a gradient of white to black with no color at all
		double range = 0.8;

		// The gradient offset factor 0 to 1
		// This affects how "deep" the tone is
		double offset = 0.2;

		double a = 0.5 * range;
		double b = -0.5 * range;

		switch( tone ) {
			case LIGHT: {
				a += offset;
				b += offset;
				break;
			}
			case DARK: {
				a -= offset;
				b -= offset;
				break;
			}
		}

		Color colorA = Colors.getTone( color, a );
		Color colorB = Colors.getTone( color, b );

		return getGradientPaint( colorA, colorB );
	}

	private Paint getGradientPaint( Color a, Color b ) {
		return new LinearGradient( xformX( 0 ), xformX( 0 ), xformX( 1 ), xformX( 1 ), false, CycleMethod.NO_CYCLE, new Stop( 0.2, a ), new Stop( 0.8, b ) );
	}

	private Paint getGradientPaint( Color a, Color b, Color c ) {
		return new LinearGradient( xformX( 0 ),
			xformX( 0 ),
			xformX( 1 ),
			xformX( 1 ),
			false,
			CycleMethod.NO_CYCLE,
			new Stop( 0.2, a ),
			new Stop( 0.5, b ),
			new Stop( 0.8, c )
		);
	}

	protected Paint linearPaint( double x1, double y1, double x2, double y2, Stop... stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint linearPaint( double x1, double y1, double x2, double y2, List<Stop> stops ) {
		return new LinearGradient( x1, y1, x2, y2, false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint radialPaint( double x, double y, double r, Stop... stops ) {
		return new RadialGradient( 0, 0, xformX( x ), xformY( y ), xformX( r ), false, CycleMethod.NO_CYCLE, stops );
	}

	protected Paint radialPaint( double x, double y, double r, List<Stop> stops ) {
		return new RadialGradient( 0, 0, xformX( x ), xformY( y ), xformX( r ), false, CycleMethod.NO_CYCLE, stops );
	}

	protected Font deriveFont( Font font, double size ) {
		if( font == null ) font = getGraphicsContext2D().getFont();
		FontWeight fontWeight = FontUtil.getFontWeight( font.getStyle() );
		FontPosture fontPosture = FontUtil.getFontPosture( font.getStyle() );
		return Font.font( font.getFamily(), fontWeight, fontPosture, size );
	}

	protected Font deriveFont( Font font, String family ) {
		if( font == null ) font = getGraphicsContext2D().getFont();
		FontWeight fontWeight = FontUtil.getFontWeight( font.getStyle() );
		FontPosture fontPosture = FontUtil.getFontPosture( font.getStyle() );
		return Font.font( family, fontWeight, fontPosture, font.getSize() );
	}

	protected Font deriveFont( Font font, FontWeight weight, FontPosture posture, double size ) {
		return Font.font( font.getFamily(), weight, posture, size );
	}

	protected Font deriveFont( String family, FontWeight weight, FontPosture posture, double size ) {
		return Font.font( family, weight, posture, size );
	}

	protected static double distance( double x1, double y1, double x2, double y2 ) {
		return Math.sqrt( (Math.pow( y2 - y1, 2 ) + Math.pow( x2 - x1, 2 )) );
	}

	protected ProgramImage copy() {
		ProgramImage clone = null;

		try {
			clone = getClass().getDeclaredConstructor().newInstance();
			clone.themeColorOverride = this.themeColorOverride;
			clone.outlinePaintOverride = this.outlinePaintOverride;
			clone.outlineWidthOverride = this.outlineWidthOverride;
			clone.setStyle( getStyle() );
			clone.setHeight( getHeight() );
			clone.setWidth( getWidth() );
		} catch( Exception e ) {
			e.printStackTrace();
		}

		return clone;
	}

	private ColorTheme getColorTheme() {
		return new ColorTheme( getThemeColor() );
	}

//	private void setColorTheme( ColorTheme theme ) {
//		this.colorTheme = theme;
//	}

//	private Color getThemeDrawColor() {
//		Color primary = getColorTheme().getPrimary();
//
//		double y = Colors.getLuminance( primary );
//		if( y < 0.2 ) {
//			// Dark primary
//			return primary.deriveColor( 0, 1, 1.75, 1 );
//		} else {
//			// Light primary
//			return primary.deriveColor( 0, 1, 0.25, 1 );
//		}
//	}

	private Color getThemeFillColor() {
		return getColorTheme().getPrimary();
	}

	private Scene getImageScene() {
		return getImageScene( getWidth(), getHeight() );
	}

	private Scene getImageScene( double imageWidth, double imageHeight ) {
		Pane pane = new Pane( this );
		pane.setBackground( Background.EMPTY );
		pane.setPrefSize( imageWidth, imageHeight );
		Scene scene = new Scene( pane );
		scene.setFill( Color.TRANSPARENT );
		return scene;
	}

	protected ProgramImage fireRender() {
		double size = Math.min( getWidth(), getHeight() );

		// Set the defaults
		getGraphicsContext2D().setLineCap( StrokeLineCap.ROUND );
		getGraphicsContext2D().setLineJoin( StrokeLineJoin.ROUND );
		getGraphicsContext2D().setLineWidth( xformX( getOutlineWidth() ) );

		getGraphicsContext2D().setStroke( getOutlinePaint() );
		getGraphicsContext2D().setFill( getIconFillPaint() );
		getGraphicsContext2D().setFillRule( FillRule.EVEN_ODD );

		// Start rendering by clearing the icon area
		if( graphicsContextOverride == null ) {
			clearRect( 0, 0, 1, 1 );
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

		return this;
	}

	protected static class Point {

		public double x;

		public double y;

		public double z;

		public Point( double x, double y ) {
			this( x, y, 0 );
		}

		private Point( double x, double y, double z ) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final double getX() {
			return x;
		}

		public final double getY() {
			return y;
		}

		public final double getZ() {
			return z;
		}

		public final double getMagnitude() {
			return Math.sqrt( x * x + y * y + z * z );
		}

		public final double getAngle() {
			return Math.atan2( y, x );
		}

		public final double dot( Point vector ) {
			return x * vector.x + y * vector.y + z * vector.z;
		}

		public final Point cross( Point vector ) {
			return new Point( y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x );
		}

		public final Point plus( Point vector ) {
			return new Point( x + vector.x, y + vector.y, z + vector.z );
		}

		public final Point minus( Point vector ) {
			return new Point( x - vector.x, y - vector.y, z - vector.z );
		}

		public final Point times( double scale ) {
			return new Point( x * scale, y * scale, z * scale );
		}

	}

}
