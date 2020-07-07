package com.avereon.venza.image;

public class SvgIcon extends VectorIcon {

	private double gridX;

	private double gridY;

	private String svgPath;

	public SvgIcon() {}

	protected SvgIcon( double gridX, double gridY, String svgPath ) {
		this.gridX = gridX;
		this.gridY = gridY;
		this.svgPath = svgPath;
	}

	@Override
	public <T extends VectorImage> T copy() {
		T copy = super.copy();

		((SvgIcon)copy).gridX = this.gridX;
		((SvgIcon)copy).gridY = this.gridY;
		((SvgIcon)copy).svgPath = this.svgPath;

		return copy;
	}

	@Override
	protected void doRender() {
		super.doRender();
		getGraphicsContext2D().scale( getWidth() / gridX, getHeight() / gridY );
		getGraphicsContext2D().appendSVGPath( this.svgPath );
		getGraphicsContext2D().fill();
	}

	public static void main( String[] commands ) {
		//Proof.proof( new SvgIcon( 1, 1, "M0.5 0.32375L0.76625 0.75H0.23375L0.5 0.32375 M0.5 0.16667 L0.08333 0.83333h0.83333 L0.5 0.16667z" ) );
		Proof.proof( new SvgIcon( 24, 24, "M20.5 6c-2.61.7-5.67 1-8.5 1s-5.89-.3-8.5-1L3 8c1.86.5 4 .83 6 1v13h2v-6h2v6h2V9c2-.17 4.14-.5 6-1l-.5-2zM12 6c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z" ) );
	}

}
