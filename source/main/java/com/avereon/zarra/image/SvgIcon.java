package com.avereon.zarra.image;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SvgIcon extends VectorIcon {

	/**
	 * A Paint object to denote the use of the primary color. The actual color is of no interest.
	 */
	protected static final Paint PRIMARY = Paint.valueOf( "#000000" );

	/**
	 * A Paint object to denote the use of the secondary color. The actual color is of no interest.
	 */
	protected static final Paint SECONDARY = Paint.valueOf( "#000000" );

	private List<RenderAction> actions;

	public SvgIcon() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	protected SvgIcon( double gridX, double gridY ) {
		this( gridX, gridY, null );
	}

	protected SvgIcon( double gridX, double gridY, String svgPath ) {
		super( gridX, gridY );
		actions = new CopyOnWriteArrayList<>();
		fill( svgPath );
	}

	public SvgIcon clear() {
		actions.clear();
		return this;
	}

	public SvgIcon clip( String path ) {
		actions.add( new Clip( path ) );
		return this;
	}

	public SvgIcon restore() {
		actions.add( new Restore() );
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
		return fill( new Affine(), path, paint, rule );
	}

	public SvgIcon fill( Transform transform, String path ) {
		return fill( transform, path, null, null );
	}

	public SvgIcon fill( Transform transform, String path, Paint paint ) {
		return fill( transform, path, paint, null );
	}

	public SvgIcon fill( Transform transform, String path, FillRule rule ) {
		return fill( transform, path, null, rule );
	}

	public SvgIcon fill( Transform transform, String path, Paint paint, FillRule rule ) {
		if( path != null ) actions.add( new FilledPath( transform, path, paint, rule ) );
		return this;
	}

	public SvgIcon draw( String path ) {
		return draw( path, null );
	}

	/**
	 * Add a stroked SVG path entry to the icon.
	 *
	 * @param path The SVG path to fill
	 * @param paint The paint to use to fill the path
	 * @return This {@link SvgIcon}
	 */
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
		return draw( new Affine(), path, paint, width, cap, join, dashOffset, dashes );
	}

	public SvgIcon draw( Transform transform, String path, Paint paint ) {
		return draw( transform, path, paint, DEFAULT_STROKE_WIDTH );
	}

	public SvgIcon draw( Transform transform, String path, double width ) {
		return draw( transform, path, null, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon draw( Transform transform, String path, Paint paint, double width ) {
		return draw( transform, path, paint, width, DEFAULT_STROKE_CAP, DEFAULT_STROKE_JOIN, 0 );
	}

	public SvgIcon draw( Transform transform, String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join ) {
		return draw( transform, path, paint, width, cap, join, 0 );
	}

	public SvgIcon draw( Transform transform, String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
		if( path != null ) actions.add( new StrokedPath( transform, path, paint, width, cap, join, dashOffset, dashes ) );
		return this;
	}

	public SvgIcon draw( SvgIcon icon ) {
		return draw( new Affine(), icon );
	}

	public SvgIcon draw( Transform transform, SvgIcon icon ) {
		icon.doRender();
		actions.addAll( icon.actions.stream().peek( a -> a.transform( transform ) ).collect( Collectors.toSet()) );
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

	@Override
	public <T extends VectorImage> T copy() {
		T copy = super.copy();
		((SvgIcon)copy).actions = new ArrayList<>( this.actions );
		return copy;
	}

	@Override
	protected void doRender() {
		super.doRender();
		actions.add( 0, new Save() );
		for( RenderAction action : actions ) action.render( this );
	}

	public static void main( String[] commands ) {
		Proof.proof( new SvgIcon( 24, 24, "M20.5 6c-2.61.7-5.67 1-8.5 1s-5.89-.3-8.5-1L3 8c1.86.5 4 .83 6 1v13h2v-6h2v6h2V9c2-.17 4.14-.5 6-1l-.5-2zM12 6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z" ) );
	}

	private static abstract class RenderAction {

		void render( SvgIcon icon ) {}

		void transform( Transform transform ) {}

	}

	private static class FilledPath extends RenderAction {

		private final Transform transform;

		private final String path;

		private final Paint paint;

		private final FillRule rule;

		public FilledPath( Transform transform, String path, Paint paint, FillRule rule ) {
			this.transform = transform;
			this.path = path;
			this.paint = paint;
			this.rule = rule;
		}

		public String getPath() {
			return path;
		}

		public Paint getPaint() {
			return paint;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.save();
			context.transform( new Affine( transform ) );
			if( paint == null ) {
				context.setFill( icon.getStrokePaint() );
			} else if( paint == PRIMARY ) {
				context.setFill( icon.getPrimaryPaint() );
			} else if( paint == SECONDARY ) {
				context.setFill( icon.getSecondaryPaint() );
			} else {
				context.setFill( paint );
			}
			context.setFillRule( rule );
			context.beginPath();
			context.appendSVGPath( path );
			context.fill();
			context.restore();
		}

	}

	private static class StrokedPath extends RenderAction {

		private final Transform transform;

		private final String path;

		private final Paint paint;

		private final double width;

		private final StrokeLineCap cap;

		private final StrokeLineJoin join;

		private final double dashOffset;

		private final double[] dashes;

		public StrokedPath( Transform transform, String path, Paint paint, double width, StrokeLineCap cap, StrokeLineJoin join, double dashOffset, double... dashes ) {
			this.transform = transform;
			this.path = path;
			this.paint = paint;
			this.width = width;
			this.cap = cap;
			this.join = join;
			this.dashOffset = dashOffset;
			this.dashes = dashes;
		}

		public Paint getPaint() {
			return paint;
		}

		public String getPath() {
			return path;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			context.save();
			context.transform( new Affine( transform ) );
			if( paint == null ) {
				context.setStroke( icon.getStrokePaint() );
			} else if( paint == PRIMARY ) {
				context.setStroke( icon.getPrimaryPaint() );
			} else if( paint == SECONDARY ) {
				context.setStroke( icon.getSecondaryPaint() );
			} else {
				context.setStroke( paint );
			}
			context.setLineWidth( width );
			context.setLineCap( cap );
			context.setLineJoin( join );
			context.setLineDashOffset( dashOffset );
			context.setLineDashes( dashes );
			context.beginPath();
			context.appendSVGPath( path );
			context.stroke();
			context.restore();
		}

	}

	private static class Save extends RenderAction {

		@Override
		public void render( SvgIcon icon ) {
			icon.getGraphicsContext2D().save();
		}

	}

	private static class Restore extends RenderAction {

		@Override
		public void render( SvgIcon icon ) {
			icon.getGraphicsContext2D().restore();
		}

	}

	private static class Clip extends RenderAction {

		private final Transform transform;

		private final String path;

		public Clip( String path ) {
			this.transform = new Affine();
			this.path = path;
		}

		@Override
		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			if( path == null ) {
				// This works in conjunction with context.save() in doRender()
				context.restore();
			} else {
				context.transform( new Affine( transform ) );
				context.beginPath();
				context.appendSVGPath( path );
				context.clip();
			}
		}

	}

}
