package com.avereon.zerra.javafx;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BackgroundPosition;

import java.util.*;

public class FxUtil {

	public static Pos parseAlign( String align ) {
		switch( align ) {
			case "northwest":
				return Pos.TOP_LEFT;
			case "north":
				return Pos.TOP_CENTER;
			case "northeast":
				return Pos.TOP_RIGHT;
			case "west":
				return Pos.CENTER_LEFT;
			case "center":
				return Pos.CENTER;
			case "east":
				return Pos.CENTER_RIGHT;
			case "southwest":
				return Pos.BOTTOM_LEFT;
			case "south":
				return Pos.BOTTOM_CENTER;
			case "southeast":
				return Pos.BOTTOM_RIGHT;
		}
		return Pos.CENTER;
	}

	public static BackgroundPosition parseBackgroundPosition( String align ) {
		switch( align ) {
			case "northwest":
				return new BackgroundPosition( Side.LEFT, 0, true, Side.TOP, 0, true );
			case "north":
				return new BackgroundPosition( Side.LEFT, 0.5, true, Side.TOP, 0, true );
			case "northeast":
				return new BackgroundPosition( Side.LEFT, 1, true, Side.TOP, 0, true );
			case "west":
				return new BackgroundPosition( Side.LEFT, 0, true, Side.TOP, 0.5, true );
			case "center":
				return new BackgroundPosition( Side.LEFT, 0.5, true, Side.TOP, 0.5, true );
			case "east":
				return new BackgroundPosition( Side.LEFT, 1, true, Side.TOP, 0.5, true );
			case "southwest":
				return new BackgroundPosition( Side.LEFT, 0, true, Side.TOP, 1, true );
			case "south":
				return new BackgroundPosition( Side.LEFT, 0.5, true, Side.TOP, 1, true );
			case "southeast":
				return new BackgroundPosition( Side.LEFT, 1, true, Side.TOP, 1, true );
		}
		return BackgroundPosition.CENTER;
	}

	public static boolean isChildOf( Node node, Node container ) {
		while( (node = node.getParent()) != null ) {
			if( node == container ) return true;
		}
		return false;
	}

	public static boolean isParentOf( TreeItem<?> item, TreeItem<?> child ) {
		TreeItem<?> parent = child;

		while( parent != null ) {
			if( item.equals( parent ) ) return true;
			parent = parent.getParent();
		}

		return false;
	}

	public static Bounds localToParent( Node source, Node target ) {
		return localToParent( source, target, source.getLayoutBounds() );
	}

	public static Bounds localToParent( Node source, Node target, Bounds bounds ) {
		Bounds result = bounds;

		Node parent = source;
		while( parent != null ) {
			if( parent == target ) break;
			result = parent.localToParent( result );
			parent = parent.getParent();
		}

		return result;
	}

	public static Bounds merge( Bounds a, Bounds b ) {
		if( a == null ) return b;
		if( b == null ) return a;
		double minX = Math.min( a.getMinX(), b.getMinX() );
		double minY = Math.min( a.getMinY(), b.getMinY() );
		double minZ = Math.min( a.getMinZ(), b.getMinZ() );
		double maxX = Math.max( a.getMaxX(), b.getMaxX() );
		double maxY = Math.max( a.getMaxY(), b.getMaxY() );
		double maxZ = Math.max( a.getMaxZ(), b.getMaxZ() );
		return new BoundingBox( minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ );
	}

	public static Bounds add( Bounds a, Insets b ) {
		return new BoundingBox( a.getMinX(), a.getMinY(), a.getWidth() + b.getLeft() + b.getRight(), a.getHeight() + b.getTop() + b.getBottom() );
	}

	public static Insets add( Insets a, Insets b ) {
		return new Insets( a.getTop() + b.getTop(), a.getRight() + b.getRight(), a.getBottom() + b.getBottom(), a.getLeft() + b.getLeft() );
	}

	public static <T> List<TreeItem<T>> flatTree( TreeItem<T> item ) {
		return flatTree( item, false );
	}

	public static <T> List<TreeItem<T>> flatTree( TreeItem<T> item, boolean includeItem ) {
		List<TreeItem<T>> list = new ArrayList<>();

		if( includeItem ) list.add( item );
		item.getChildren().forEach( ( child ) -> list.addAll( flatTree( child, true ) ) );

		return list;
	}

	/**
	 * Pick the child node in a parent node that contains the scene point. Found
	 * at: http://fxexperience.com/2016/01/node-picking-in-javafx/.
	 *
	 * @param parent
	 * @param sceneX
	 * @param sceneY
	 * @return
	 */
	public static Node pick( Node parent, double sceneX, double sceneY ) {
		Point2D point = parent.sceneToLocal( sceneX, sceneY, true );

		// Check if the given node has the point inside it
		if( !parent.contains( point ) ) return null;

		if( parent instanceof Parent ) {
			Node closest = null;
			List<Node> children = ((Parent)parent).getChildrenUnmodifiable();
			for( int i = children.size() - 1; i >= 0; i-- ) {
				Node child = children.get( i );
				point = child.sceneToLocal( sceneX, sceneY, true );
				if( child.isVisible() && !child.isMouseTransparent() && child.contains( point ) ) {
					closest = child;
					break;
				}
			}

			if( closest != null ) return pick( closest, sceneX, sceneY );
		}

		return parent;
	}

	public static <T extends Node> T findById( Collection<? extends T> nodes, String id ) {
		return nodes.stream().filter( n -> Objects.equals(n.getId(), id)).findFirst().orElse( null );
	}

	public static <T extends MenuItem> T findMenuItemById( Collection<? extends T> nodes, String id ) {
		return nodes.stream().filter( n -> Objects.equals(n.getId(), id)).findFirst().orElse( null );
	}

	public static void setTransferMode( DragEvent e ) {
		setTransferMode( e, TransferMode.MOVE );
	}

	public static void setTransferMode( DragEvent e, TransferMode... defaultModes ) {
		e.acceptTransferModes( TransferMode.COPY_OR_MOVE );
	}

	private static void setTransferModeWorkaround( DragEvent e, TransferMode... defaultModes ) {
		// FIXME This does not work the same when the drag comes from an external application,
		// including other JVMs, as it does internally. Very annoying.
		Set<TransferMode> modes = e.getDragboard().getTransferModes();
		System.out.println( "dtms=" + modes + " tm=" + e.getTransferMode() + " atm=" + e.getAcceptedTransferMode() );
		if( modes.isEmpty() ) return;
		TransferMode[] mode = modes.size() == 1 ? TransferMode.ANY : defaultModes;
		e.acceptTransferModes( mode );
	}

}
