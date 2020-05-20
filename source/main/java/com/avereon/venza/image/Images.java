package com.avereon.venza.image;

import com.avereon.venza.icon.BrokenIcon;
import com.avereon.venza.icon.BrokenIconOld;
import com.avereon.venza.icon.RenderedIcon;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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

	public static Image[] getStageIcons( ProgramImage image ) {
		return getStageIcons( image, 16, 24, 32, 48, 64, 128, 256 );
	}

	public static Image[] getStageIcons( ProgramImage image, int... sizes ) {
		Image[] images = new Image[ sizes.length ];
		for( int index = 0; index < sizes.length; index++ ) {
			if( image == null ) image = new BrokenIconOld();
			images[ index ] = image.setSize( sizes[ index ] ).getImage();
		}
		return images;
	}


	public static Image[] getStageIcons( RenderedIcon image ) {
		return getStageIcons( image, 16, 24, 32, 48, 64, 128, 256 );
	}

	public static Image[] getStageIcons( RenderedIcon image, int... sizes ) {
		Image[] images = new Image[ sizes.length ];
		for( int index = 0; index < sizes.length; index++ ) {
			if( image == null ) image = new BrokenIcon();
			images[ index ] = image.resize( sizes[ index ] ).getImage();
		}
		return images;
	}

}
