package com.avereon.venza.image;

import com.avereon.venza.color.Colors;
import com.avereon.venza.javafx.JavaFxStarter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderedIcon extends RenderedImage {

	private static final Color FILL = Color.GRAY;

	public RenderedIcon() {
		super();
		getStyleClass().add( "xe-icon" );
	}

	public double getSize() {
		return getWidth();
	}

	public static void proof( RenderedIcon icon ) {
		proof( icon, null, null );
	}

	public static void proof( RenderedIcon icon, Color darkFill, Color lightFill ) {
		JavaFxStarter.startAndWait( 1000 );

		// Now show the icon window
		Platform.runLater( () -> {
			Application.setUserAgentStylesheet( Application.STYLESHEET_MODENA );

			Pane darkPane = proofPane( icon.copy(), DARK_THEME, darkFill == null ? Color.web( "#404040" ) : darkFill );
			Pane lightPane = proofPane( icon.copy(), LIGHT_THEME, lightFill == null ? Color.web( "#C0C0C0" ) : lightFill );
			HBox box = new HBox( 5, darkPane, lightPane );
			box.setStyle( "-fx-background-color: " + Colors.web( FILL ) + ";" );
			Scene scene = new Scene( box );

			List<Image> stageIcons = new ArrayList<>();
			stageIcons.add( icon.copy().resize( 256 ).getImage() );
			stageIcons.add( icon.copy().resize( 128 ).getImage() );
			stageIcons.add( icon.copy().resize( 64 ).getImage() );
			stageIcons.add( icon.copy().resize( 48 ).getImage() );
			stageIcons.add( icon.copy().resize( 32 ).getImage() );
			stageIcons.add( icon.copy().resize( 16 ).getImage() );

			Stage stage = new Stage();
			stage.setTitle( icon.getClass().getSimpleName() );
			stage.getIcons().addAll( stageIcons );
			stage.setScene( scene );
			// The following line causes the stage not to show on Linux
			//stage.setResizable( false );
			stage.centerOnScreen();
			stage.sizeToScene();
			stage.show();
		} );
	}

	private static Pane proofPane( RenderedIcon icon, String theme, Color fill ) {
		icon.setTheme( theme );

		String style = "";
		if( fill != null ) style += "-fx-background-color: " + Colors.web( fill ) + ";";
		icon.getProperties().put( "container-style", style );

		ImageView imageView16 = new ImageView( Images.resample( icon.copy().resize( 16 ).getImage(), 16 ) );
		ImageView imageView32 = new ImageView( Images.resample( icon.copy().resize( 32 ).getImage(), 8 ) );

		RenderedIcon icon256 = icon.copy().resize( DEFAULT_SIZE );
		AnchorPane.setTopAnchor( icon256, 0.0 );
		AnchorPane.setLeftAnchor( icon256, 0.0 );

		RenderedIcon icon128 = icon.copy().resize( 128 );
		AnchorPane.setTopAnchor( icon128, 0.0 );
		AnchorPane.setLeftAnchor( icon128, 0.0 );

		RenderedIcon icon64 = icon.copy().resize( 64 );
		AnchorPane.setTopAnchor( icon64, 128.0 );
		AnchorPane.setLeftAnchor( icon64, 128.0 );

		RenderedIcon icon32 = icon.copy().resize( 32 );
		AnchorPane.setTopAnchor( icon32, 192.0 );
		AnchorPane.setLeftAnchor( icon32, 192.0 );

		RenderedIcon icon16 = icon.copy().resize( 16 );
		AnchorPane.setTopAnchor( icon16, 224.0 );
		AnchorPane.setLeftAnchor( icon16, 224.0 );

		RenderedIcon icon8 = icon.copy().resize( 8 );
		AnchorPane.setTopAnchor( icon8, 240.0 );
		AnchorPane.setLeftAnchor( icon8, 240.0 );

		AnchorPane scaledIconPane = new AnchorPane();
		scaledIconPane.getChildren().addAll( icon128, icon64, icon32, icon16, icon8 );

		StackPane iconPane256 = new StackPane( icon256 );
		StackPane iconPane16 = new StackPane( imageView16 );
		StackPane iconPane32 = new StackPane( imageView32 );

		applyContainerStylesheets( icon, iconPane256 );
		applyContainerStylesheets( icon, iconPane16 );
		applyContainerStylesheets( icon, iconPane32 );
		applyContainerStylesheets( icon, scaledIconPane );

		TilePane pane = new TilePane( Orientation.HORIZONTAL, 5, 5 );
		pane.setPrefColumns( 2 );
		pane.setPrefRows( 2 );
		pane.setHgap( 5 );
		pane.setVgap( 5 );
		pane.getChildren().add( iconPane256 );
		pane.getChildren().add( iconPane16 );
		pane.getChildren().add( iconPane32 );
		pane.getChildren().add( scaledIconPane );

		StackPane root = new StackPane( pane );
		root.getStyleClass().add( "root" );
		root.setStyle( "-fx-background-color: " + Colors.web( FILL ) + ";" );
		return root;
	}

}
