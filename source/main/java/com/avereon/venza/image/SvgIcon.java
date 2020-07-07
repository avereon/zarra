package com.avereon.venza.image;

public class SvgIcon extends VectorIcon {

	protected SvgIcon() {}

	protected SvgIcon( double gridWidth, double gridHeight, String svgPath ) {
		//getGraphicsContext2D().setTransform( 1, 1, 1, 1, 0, 0 );
		getGraphicsContext2D().appendSVGPath( svgPath );
	}

	public static void main( String[] commands ) {
		Proof.proof( new SvgIcon( 24, 24, "M0.5 0.32375L0.76625 0.75H0.23375L0.5 0.32375 M0.5 0.16667 L0.08333 0.83333h0.83333 L0.5 0.16667z" ) );
	}

	//	@Override
	//	protected void render() {
	//		//getGraphicsContext2D().appendSVGPath( "M0 0h24v24H0V0z" );
	//		//getGraphicsContext2D().appendSVGPath( "M12 7.77L18.39 18H5.61L12 7.77 M12 4L2 20h20L12 4z" );
	//		//getGraphicsContext2D().appendSVGPath( "M0 0h1v1H0V0z" );
	//
	//		// Works
	//		getGraphicsContext2D().appendSVGPath( "M0.5 0.32375L0.76625 0.75H0.23375L0.5 0.32375 M0.5 0.16667 L0.08333 0.83333h0.83333 L0.5 0.16667z" );
	//		//fill();
	//	}

}
