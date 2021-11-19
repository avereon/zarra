package com.avereon.zarra.javafx;

import com.avereon.zerra.test.FxPlatformTestCase;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.testfx.assertions.api.Assertions.assertThat;

public class FxUtilTest extends FxPlatformTestCase {

	private final TreeItem<String> root = new TreeItem<>( "root" );

	private final TreeItem<String> a = new TreeItem<>( "a" );

	private final TreeItem<String> a1 = new TreeItem<>( "a1" );

	private final TreeItem<String> a2 = new TreeItem<>( "a2" );

	private final TreeItem<String> b = new TreeItem<>( "b" );

	private final TreeItem<String> b3 = new TreeItem<>( "b3" );

	private final TreeItem<String> b4 = new TreeItem<>( "b4" );

	private final TreeItem<String> b5 = new TreeItem<>( "b5" );

	private final TreeItem<String> b6 = new TreeItem<>( "b6" );

	private final TreeItem<String> c = new TreeItem<>( "c" );

	private final TreeItem<String> c7 = new TreeItem<>( "c7" );

	@BeforeEach
	@SuppressWarnings( "unchecked" )
	public void setup() {
		a.getChildren().addAll( a1, a2 );
		b.getChildren().addAll( b3, b4, b5, b6 );
		c.getChildren().addAll( c7 );
		root.getChildren().addAll( a, b, c );
	}

	@Test
	void testIsChildOfFalse() {
		Pane parent = new Pane();
		Node child = new Label();
		assertThat( FxUtil.isChildOf( child, parent ) ).isEqualTo( false );
	}

	@Test
	void testIsChildOfWithParent() {
		Pane parent = new Pane();
		Node child = new Label();

		assertThat( FxUtil.isChildOf( child, parent ) ).isEqualTo( false );

		parent.getChildren().add( child );

		assertThat( FxUtil.isChildOf( child, parent ) ).isEqualTo( true );
	}

	@Test
	void testIsChildOfWithGrandParent() {
		Pane grandParent = new Pane();
		Pane parent = new Pane();
		Node child = new Label();

		assertThat( FxUtil.isChildOf( child, grandParent ) ).isEqualTo( false );

		grandParent.getChildren().add( parent );
		parent.getChildren().add( child );

		assertThat( FxUtil.isChildOf( child, grandParent ) ).isEqualTo( true );
	}

	@Test
	void testFlatTree() {
		assertThat( FxUtil.flatTree( root ) ).contains( a, a1, a2, b, b3, b4, b5, b6, c, c7 );
	}

	@Test
	void testFlatTreeWithRoot() {
		assertThat( FxUtil.flatTree( root, true ) ).contains( root, a, a1, a2, b, b3, b4, b5, b6, c, c7 );
	}

}
