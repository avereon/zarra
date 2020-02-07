package com.avereon.venza.image;

import com.avereon.util.FileUtil;
import com.avereon.util.TextUtil;
import com.avereon.venza.javafx.FxUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import net.sf.image4j.codec.ico.ICOEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProgramImageWriter {

	private BufferedImage image;

	public void save( ProgramImage renderer, Path path ) throws Exception {
		save( List.of( renderer ), path );
	}

	/**
	 * Save a rendered image using the specified width and height. This is useful
	 * for creating an image that does not use the renderer's width and height.
	 * The renderer can also be repositioned using the relocate() method.
	 *
	 * @param renderer The image renderer
	 * @param path The path of the saved image
	 * @param width The image width
	 * @param height The image height
	 * @throws Exception
	 */
	public void save( ProgramImage renderer, Path path, double width, double height ) throws Exception {
		BufferedImage image = doCreateImage( renderer, width, height );
		saveImage( image == null ? List.of() : List.of( image ), path );
	}

	public void save( List<ProgramImage> renderers, Path path ) throws Exception {
		List<BufferedImage> images = new ArrayList<>();
		for( ProgramImage renderer : renderers ) {
			images.add( doCreateImage( renderer ) );
		}
		saveImage( images, path );
	}

	void saveImage( List<BufferedImage> images, Path path ) throws Exception {
		Path parent = path.getParent();
		if( !Files.exists( parent ) ) Files.createDirectories( parent );
		File absoluteFile = path.toFile().getAbsoluteFile();
		String type = FileUtil.getExtension( path );
		if( TextUtil.isEmpty( type ) ) type = "png";

		if( "ico".equals( type ) ) {
			ICOEncoder.write( images, absoluteFile );
		} else {
			ImageIO.write( images.get( 0 ), type, absoluteFile );
		}
	}

	private BufferedImage doCreateImage( ProgramImage image ) throws Exception {
		return doCreateImage( image, image.getWidth(), image.getHeight() );
	}

	private BufferedImage doCreateImage( ProgramImage image, double width, double height ) throws Exception {
		Runnable createImage = () -> doCreateImageFx( image, width, height );
		try {
			Platform.runLater( createImage );
		} catch( IllegalStateException exception ) {
			Platform.startup( createImage );
		}
		FxUtil.fxWait( 1000 );
		if( this.image == null )throw new NullPointerException( "Image not created" );
		return this.image;
	}

	private void doCreateImageFx( ProgramImage image, double width, double height ) {
		BufferedImage buffer = new BufferedImage( (int)width, (int)height, BufferedImage.TYPE_INT_ARGB );
		this.image = SwingFXUtils.fromFXImage( image.getImage( width, height ), buffer );
	}

}
