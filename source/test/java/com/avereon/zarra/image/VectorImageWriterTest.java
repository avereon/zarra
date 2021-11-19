package com.avereon.zarra.image;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testfx.assertions.api.Assertions.assertThat;

public class VectorImageWriterTest {

	private Path targetFolder = Paths.get( "target", "images" );

	@Test
	void testSavePng() throws Exception {
		runTest( "testicon.png", ( path ) -> new VectorImageWriter().save( new BrokenIcon(), path ) );
	}

	@Test
	void testSaveIco() throws Exception {
		runTest( "testicon.ico", ( path ) -> new VectorImageWriter().save( new BrokenIcon(), path ) );
	}

	@Test
	void testSavePngWithOffset() throws Exception {
		runTest( "testiconoffset.png", ( path ) -> {
			RenderedIcon icon = new BrokenIcon();
			double size = icon.getSize();
			double pad = 0.2 * size;
			icon.relocate( pad, pad );
			new VectorImageWriter().save( icon, path, size + 2 * pad, size + 2 * pad );
		} );
	}

	private void runTest( String name, Generator generator ) throws Exception {
		Path path = targetFolder.resolve( name );
		Files.deleteIfExists( path );
		assertThat( Files.exists( path ) ).isFalse();
		generator.run( path );
		assertThat( Files.exists( path ) ).isTrue();
	}

	private interface Generator {

		void run( Path path ) throws Exception;

	}

}
