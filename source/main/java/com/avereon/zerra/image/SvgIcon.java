package com.avereon.zerra.image;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
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

	private List<PaintedPath> paths;

	public SvgIcon() {
		this( DEFAULT_GRID, DEFAULT_GRID );
	}

	protected SvgIcon( double gridX, double gridY ) {
		this( gridX, gridY, null );
	}

	protected SvgIcon( double gridX, double gridY, String svgPath ) {
		super( gridX, gridY );
		paths = new CopyOnWriteArrayList<>();
		add( svgPath );
	}

	public SvgIcon clear() {
		paths.clear();
		return this;
	}

	public SvgIcon add( String path ) {
		return add( null, path );
	}

	/**
	 * Add a paint and SVG path entry to the icon.
	 *
	 * @param paint The paint to use to fill the path
	 * @param path The SVG path to fill
	 * @return This {@link SvgIcon}
	 */
	public SvgIcon add( Paint paint, String path ) {
		if( path != null ) paths.add( new PaintedPath( paint, path ) );
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
		((SvgIcon)copy).paths = new ArrayList<>( this.paths );
		return copy;
	}

	@Override
	protected void doRender() {
		super.doRender();
		for( PaintedPath path : paths ) path.render( this );
	}

	public static void main( String[] commands ) {
		Proof.proof( new SvgIcon(
			24,
			24,
			"M20.5 6c-2.61.7-5.67 1-8.5 1s-5.89-.3-8.5-1L3 8c1.86.5 4 .83 6 1v13h2v-6h2v6h2V9c2-.17 4.14-.5 6-1l-.5-2zM12 6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z"
		) );
	}

	private class PaintedPath {

		private final Paint paint;

		private final String path;

		public PaintedPath( Paint paint, String path ) {
			this.paint = paint;
			this.path = path;
		}

		public Paint getPaint() {
			return paint;
		}

		public String getPath() {
			return path;
		}

		public void render( SvgIcon icon ) {
			GraphicsContext context = icon.getGraphicsContext2D();
			if( paint == null ) {
				context.setFill( icon.getStrokePaint() );
			} else if( paint == PRIMARY ) {
				context.setFill( icon.getPrimaryPaint() );
			} else if( paint == SECONDARY ) {
				context.setFill( icon.getSecondaryPaint() );
			} else {
				context.setFill( paint );
			}
			context.beginPath();
			context.appendSVGPath( path );
			context.fill();
		}

	}

}
