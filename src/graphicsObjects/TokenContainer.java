package graphicsObjects;

import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * This might be used for the card creation menu,
 * If it isn't used for that then this should be removed.
 */
public class TokenContainer extends HBox {

	private Pane drawer;
	private Pane handle;

	private double xPosIn;
	private double xPosOut;
	private double yPos;

	private double height;
	private double drawerWidth;
	private double handleWidth;

	private boolean drawerOut;

	public TokenContainer() {
		height = 200;
		drawerWidth = 200;
		handleWidth = 50;

		drawer = new Pane();
		drawer.setPrefWidth ( drawerWidth );
		drawer.setPrefHeight( height );
		drawer.getStyleClass().add( "token-container-drawer" );

		handle = new Pane();
		handle.setPrefWidth( handleWidth );
		handle.setPrefHeight( height );
		handle.getStyleClass().add( "token-container-handle" );
		handle.setOnMouseClicked( new HandleListener() );



		xPosOut = 0;
		xPosIn = -(drawerWidth);
		yPos = 10;

		drawerOut = false;

		this.setTranslateX( xPosIn );
		this.setTranslateY( yPos );

		this.getStyleClass().add( "token-container" );

		this.getChildren().add( drawer );
		this.getChildren().add( handle );
	}
	
	public void openDrawer() {
		TranslateTransition tt = new TranslateTransition(Duration.millis(500), this);
		tt.setToX( xPosOut );
		drawerOut = true;
		this.toFront();
		tt.play();
	}

	public void closeDrawer() {
		TranslateTransition tt = new TranslateTransition(Duration.millis(500), this);
		tt.setToX( xPosIn );
		drawerOut = false;
		this.toBack();
		tt.play();
	}

	// TODO possibly add a mouse drag event to open and close the drawer
	private class HandleListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
				if( drawerOut ) {
					closeDrawer();
				} else {
					openDrawer();
				}
			}
		}
	}	
}
