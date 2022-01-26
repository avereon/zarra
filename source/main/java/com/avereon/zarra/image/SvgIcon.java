package com.avereon.zarra.image;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SvgIcon extends VectorIcon {

	/**
	 * A Paint object to denote the use of the primary color. The actual color is of no interest.
	 */
	protected static final Paint PRIMARY = Paint.valueOf( "#000000" );

	/**
	 * A Paint object to denote the use of the secondary color. The actual color is of no interest.
	 */
	protected static final Paint SECONDARY = Paint.valueOf( "#000000" );

	private final List<IconAction> actions;

	public SvgIcon() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	protected SvgIcon( double gridX, double gridY ) {
		this( gridX, gridY, null );
	}

	protected SvgIcon( double gridX, double gridY, String svgPath ) {
		super( gridX, gridY );
		actions = new CopyOnWriteArrayList<>();
		reset();
		fill( svgPath );
	}

	public SvgIcon clear() {
		actions.clear();
		return this;
	}

	public void restore() {
		actions.add( new Reset() );
	}

	public SvgIcon clip( String path ) {
		actions.add( new Clip( path ) );
		return this;
	}

	public SvgIcon transform( Transform transform ) {
		actions.add( new Xform( transform ) );
		return this;
	}

	/**
	 * Add a filled SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon fill( String path ) {
		return fill( path, null, null );
	}

	public SvgIcon fill( String path, Paint paint ) {
		return fill( path, paint, null );
	}

	public SvgIcon fill( String path, FillRule rule ) {
		return fill( path, null, rule );
	}

	public SvgIcon fill( String path, Paint paint, FillRule rule ) {
		if( path != null ) actions.add( new Fill( path, paint, rule ) );
		return this;
	}

	/**
	 * Add a stroked SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon draw( String path ) {
		return draw( path, null );
	}

	public SvgIcon draw( String path, Paint paint ) {
		return draw( path, paint, DEFAULT_STROKE_WIDTH );
	}

	public SvgIcon draw( String path, double width ) {
		return draw( path, null, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon draw( String path, Paint paint, double width ) {
		return draw( path, paint, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon draw( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join ) {
		return draw( path, paint, width, cap, join, 0 );
	}

	public SvgIcon draw( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
		if( path != null ) actions.add( new Draw( path, paint, width, cap, join, dashOffset, dashes ) );
		return this;
	}

	public SvgIcon draw( SvgIcon icon ) {
		if( icon != null ) {
			icon.doRender();
			actions.addAll( icon.actions );
			actions.add( new Reset() );
		}
		return this;
	}

	/**
	 * Create an SVG string for a circle. Because SVG path arcs are a bit
	 * cumbersome this method simplifies the creating of a full circle.
	 *
	 * @param cx The circle center X coordinate
	 * @param cy The circle center Y coordinate
	 * @param r The circle radius
	 * @return The SVG string for the circle
	 */
	public static String circle( double cx, double cy, double r ) {
		return "M" + (cx + r) + "," + cy + " A" + r + "," + r + " 0 0 0 " + (cx - r) + "," + cy + " A" + r + "," + r + " 0 0 0 " + (cx + r) + "," + cy;
	}

	/**
	 * Create an SVG string for an ellipse. Because SVG path arcs are a bit
	 * cumbersome this method simplifies the creating of a full ellipse.
	 *
	 * @param cx The ellipse center X coordinate
	 * @param cy The ellipse center Y coordinate
	 * @param rx The ellipse X radius
	 * @param ry The ellipse Y radius
	 * @return The SVG string for the ellipse
	 */
	public static String ellipse( double cx, double cy, double rx, double ry ) {
		return ellipse( cx, cy, rx, ry, 0 );
	}

	/**
	 * Create an SVG string for an ellipse. Because SVG path arcs are a bit
	 * cumbersome this method simplifies the creating of a full ellipse.
	 *
	 * @param cx The ellipse center X coordinate
	 * @param cy The ellipse center Y coordinate
	 * @param rx The ellipse X radius
	 * @param ry The ellipse Y radius
	 * @return The SVG string for the ellipse
	 */
	public static String ellipse( double cx, double cy, double rx, double ry, double rotate ) {
		return "M" + (cx + rx) + "," + (cy + ry) + " A" + rx + "," + ry + " " + rotate + " 0 0 " + (cx - rx) + "," + (cy - ry) + " A" + rx + "," + ry + " " + rotate + " 0 0 " + (cx + rx) + "," + (cy + ry);
	}

	/**
	 * Create an SVG string that represents the given point rotated about cx,cy by angle.
	 *
	 * @param x The X coordinate of the point
	 * @param y The Y coordinate of the point
	 * @param cx The X coordinate of the rotation center
	 * @param cy The Y coordinate of the rotation center
	 * @param angle The angle in degrees to rotate counter clockwise
	 * @return The SVG string of the rotated point
	 */
	protected String rotate( double x, double y, double cx, double cy, double angle ) {
		return rx( cx, cy, x, y, angle ) + "," + ry( cx, cy, x, y, angle );
	}

	private double rx( double cx, double cy, double x, double y, double angle ) {
		return cx + ((x - cx) * Math.cos( Math.toRadians( angle ) )) + ((y - cy) * Math.sin( Math.toRadians( angle ) ));
	}

	private double ry( double cx, double cy, double x, double y, double angle ) {
		return cy + ((y - cy) * Math.cos( Math.toRadians( angle ) )) - ((x - cx) * Math.sin( Math.toRadians( angle ) ));
	}

	private void pushClip( String path ) {
//		clips.push( path );
	}

	private void popClip() {
//		clips.pop();
	}

	private void setClip() {
		//if( clips.isEmpty() ) System.out.println( "No clips in queue" );

		//		String clip = clips.peek();
		//		//if( clip == null ) System.out.println( "Clip is null" );
		//		//if( Objects.equals( clip, EMPTY_CLIP ) ) System.out.println( "Clip is EMPTY_CLIP" );
		//
		//		if( clip == null || Objects.equals( clip, EMPTY_CLIP ) ) clip = null;
		//
		//		//if( clip == null ) System.out.println( "Clip is still null" );
		//
		//		if( Objects.equals( clip, null ) ) {
		//			System.err.println( "Using null clip" );
		//		} else if( Objects.equals( clip, defaultClip ) ) {
		//			System.err.println( "Using default clip" );
		//		} else {
		//			System.err.println( "Using special clip" );
		//		}
		//
		//		GraphicsContext context = getGraphicsContext2D();
		//		context.beginPath();
		//		if( clip == null ) {
		//			//context.rect( Double.MIN_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MAX_VALUE );
		//		} else {
		//			context.appendSVGPath( clip );
		//		}
		//		context.clip();
	}

	@Override
	public <T extends VectorImage> T copy() {
		T copy = super.copy();
		((SvgIcon)copy).actions.addAll( this.actions );
		return copy;
	}

	@Override
	protected void doRender() {
		super.doRender();
		getGraphicsContext2D().save();
		actions.forEach( a -> a.render( this ) );
	}

	private void doReset() {
		getGraphicsContext2D().restore();
		//super.reset();
	}

	public static void main( String[] commands ) {
		Proof.proof( new SvgIcon( 24, 24, "M20.5 6c-2.61.7-5.67 1-8.5 1s-5.89-.3-8.5-1L3 8c1.86.5 4 .83 6 1v13h2v-6h2v6h2V9c2-.17 4.14-.5 6-1l-.5-2zM12 6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z" ) );
	}

	private static interface IconAction {

		void render( SvgIcon icon );
	}

	private static abstract class RenderAction implements IconAction {

		private final String path;

		private final Paint paint;

		protected RenderAction( String path, Paint paint ) {
			this.path = path;
			this.paint = paint;
		}

		protected String getPath() {
			return path;
		}

		protected Paint getPaint() {
			return paint;
		}

		protected GraphicsContext setup( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();

			context.save();
			//icon.setClip();

			return context;
		}

		protected GraphicsContext teardown( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();

			context.restore();

			return context;
		}

		protected Paint calcPaint( SvgIcon icon ) {
			if( paint == null ) {
				return icon.getStrokePaint();
			} else if( paint == PRIMARY ) {
				return icon.getPrimaryPaint();
			} else if( paint == SECONDARY ) {
				return icon.getSecondaryPaint();
			} else {
				return paint;
			}
		}

	}

	private static class Fill extends RenderAction {

		private final FillRule rule;

		public Fill( String path, Paint paint, FillRule rule ) {
			super( path, paint );
			this.rule = rule;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			context.setFill( calcPaint( icon ) );
			context.setFillRule( rule );

			context.beginPath();
			context.appendSVGPath( getPath() );
			context.fill();

			teardown( icon );
		}

	}

	private static class Draw extends RenderAction {

		private final double width;

		private final StrokeLineCap cap;

		private final StrokeLineJoin join;

		private final double dashOffset;

		private final double[] dashes;

		public Draw( String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
			super( path, paint );
			this.width = width;
			this.cap = cap;
			this.join = join;
			this.dashOffset = dashOffset;
			this.dashes = dashes;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			context.setStroke( calcPaint( icon ) );
			context.setLineWidth( width );
			context.setLineCap( cap );
			context.setLineJoin( join );
			context.setLineDashOffset( dashOffset );
			context.setLineDashes( dashes );

			context.beginPath();
			context.appendSVGPath( getPath() );
			context.stroke();

			teardown( icon );
		}

	}

	private static class Clip extends RenderAction {

		public Clip( String path ) {
			super( path, null );
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = setup( icon );
			if( getPath() == null ) return;

			context.beginPath();
			context.appendSVGPath( getPath() );
			context.clip();
		}

	}

	private static class Xform implements IconAction {

		private final Transform transform;

		public Xform( Transform transform ) {
			this.transform = transform;
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.transform( new Affine( transform ) );
		}

	}

	private static class Reset implements IconAction {

		@Override
		public void render( SvgIcon icon ) {
			icon.doReset();
		}

	}

}
