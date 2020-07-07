package com.avereon.venza.image;

import com.avereon.venza.font.FontUtil;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.List;

public abstract class RenderedImage extends VectorImage {

	private GraphicsContext graphicsContextOverride;

	private Transform baseTransform = new Affine();

	protected RenderedImage() {}

	protected abstract void render();

	@Override
	public GraphicsContext getGraphicsContext2D() {
		return graphicsContextOverride == null ? super.getGraphicsContext2D() : graphicsContextOverride;
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
		drawText( text, x, y, textSize, -1 );
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

	protected void draw( Paint paint ) {
		getGraphicsContext2D().save();
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().stroke();
		getGraphicsContext2D().restore();
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

	protected void doRender() {
		super.doRender();

		// Start rendering by clearing the icon area
		if( graphicsContextOverride == null ) {
			getGraphicsContext2D().clearRect( 0, 0, 1, 1 );
			getGraphicsContext2D().setTransform( new Affine() );
			double size = Math.min( getWidth(), getHeight() );
			baseTransform = Transform.scale( size, size );
			reset();
		}

		render();
	}

	protected double g( double value ) {
		return value / 32d;
	}

	private void setGraphicsContext2D( GraphicsContext context ) {
		this.graphicsContextOverride = context;
	}

	private void renderText( String text, double x, double y, double textSize, double maxWidth, boolean draw ) {
		// Font sizes smaller than one don't scale as expected
		// so the workaround is to scale according the text size
		// and divide the coordinates by the size.

		double fontSize = 72;
		double scale = textSize / fontSize;

		// Scale the transform
		getGraphicsContext2D().save();
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

		getGraphicsContext2D().restore();
	}

	//	static void applyContainerStylesheets( RenderedImage image, Parent node ) {
	//		// Add the default container stylesheet
	//		node.getStylesheets().add( Stylesheet.VENZA );
	//
	//		// Add extra container style
	//		String style = (String)image.getProperties().get( "container-style" );
	//		if( style != null ) node.setStyle( style );
	//	}

	//	private static Scene getImageScene( RenderedImage image, double imageWidth, double imageHeight ) {
	//		return getImageScene( image, imageWidth, imageHeight, null );
	//	}

	//	private static Scene getImageScene( RenderedImage image, double imageWidth, double imageHeight, Paint fill ) {
	//		image.applyTheme();
	//		Pane pane = new Pane( image );
	//		pane.setBackground( Background.EMPTY );
	//		pane.setPrefSize( imageWidth, imageHeight );
	//		applyContainerStylesheets( image, pane );
	//		Scene scene = new Scene( pane );
	//		scene.setFill( fill == null ? Color.TRANSPARENT : fill );
	//		return scene;
	//	}
}
