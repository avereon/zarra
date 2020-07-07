package com.avereon.venza.image;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import com.avereon.venza.style.Stylesheet;
import com.avereon.venza.style.Theme;

public class Images {

	public static Image resample( Image input, int scale ) {
		int w = (int)input.getWidth();
		int h = (int)input.getHeight();

		WritableImage output = new WritableImage( w * scale, h * scale );

		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();

		for( int y = 0; y < h; y++ ) {
			for( int x = 0; x < w; x++ ) {
				final int argb = reader.getArgb( x, y );
				for( int dy = 0; dy < scale; dy++ ) {
					for( int dx = 0; dx < scale; dx++ ) {
						writer.setArgb( x * scale + dx, y * scale + dy, argb );
					}
				}
			}
		}

		return output;
	}

	public static Image[] getStageIcons( VectorImage image ) {
		return getStageIcons( image, 16, 24, 32, 48, 64, 128, 256 );
	}

	public static Image[] getStageIcons( VectorImage image, int... sizes ) {
		Image[] images = new Image[ sizes.length ];
		for( int index = 0; index < sizes.length; index++ ) {
			if( image == null ) image = new BrokenIcon();
			images[ index ] = image.resize( sizes[ index ] ).getImage();
		}
		return images;
	}

	static Scene getImageScene( Canvas canvas, double imageWidth, double imageHeight, Paint fill ) {
		if( canvas instanceof VectorImage ) {
			VectorImage image = (VectorImage)canvas;
			applyTheme( image, image.getTheme() );
		}
		Pane pane = new Pane( canvas );
		pane.setBackground( Background.EMPTY );
		pane.setPrefSize( imageWidth, imageHeight );
		applyContainerStylesheets( canvas, pane );
		Scene scene = new Scene( pane );
		scene.setFill( fill == null ? Color.TRANSPARENT : fill );
		return scene;
	}

	static void applyContainerStylesheets( Canvas image, Parent node ) {
		// Add the default container stylesheet
		node.getStylesheets().add( Stylesheet.VENZA );

		// Add extra container style
		String style = (String)image.getProperties().get( "container-style" );
		if( style != null ) node.setStyle( style );
	}

	private static void applyTheme( VectorImage node, Theme theme ) {
		String style = removeTheme( node );
		node.setStyle( style == null ? theme.getStyle() : style + theme.getStyle() );
	}

	private static String removeTheme( Canvas node ) {
		String style = node.getStyle();
		for( Theme t : Theme.values() ) {
			int index = style.indexOf( t.getStyle() );
			if( index > -1 ) style = style.replace( t.getStyle(), "" );
		}
		node.setStyle( style );
		return style;
	}

}
