package graphicsObjects;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Token extends Pane {
	// maybe have something better than int for color
	private int color;

	private int value;

	private Button minusBtn;
	private Button plusBtn;
	private Pane middlePane;
	private Circle circle;

	private double xPos;
	private double yPos;
	
	private double minusBtnXOut;
	private double minusBtnXIn;
	private double plusBtnXOut;
	private double plusBtnXIn;

	private double scaleFactor;

	public Token() {

		minusBtnXOut = 0;
		minusBtnXIn  = 25;
		plusBtnXOut  = 75;
		plusBtnXIn   = 50;

		minusBtn = new Button( "-" );
		minusBtn.setTranslateY( 12.5 );
		minusBtn.setTranslateY( minusBtnXIn );
		minusBtn.setPrefWidth( 30 );
		minusBtn.setPrefHeight( 30 );

		plusBtn = new Button( "+" );
		plusBtn.setTranslateY( 12.5 );
		plusBtn.setTranslateX( plusBtnXIn );
		plusBtn.setPrefWidth( 30 );
		plusBtn.setPrefHeight( 30 );

		circle = new Circle( 25, 25, 25 );

		Text text = new Text( "0" );

		middlePane = new StackPane();
		middlePane.setTranslateX( 25 );
		middlePane.getChildren().add( circle );
		//middlePane.getChildren().add( text );

		DragListener dl = new DragListener();
		middlePane.setOnMousePressed( dl );
		middlePane.setOnMouseDragged( dl );
		middlePane.setOnMouseReleased( dl );
		
		xPos = 200;
		yPos = 200;
		scaleFactor = 1;

		this.setTranslateX( xPos );
		this.setTranslateY( yPos );

		this.getChildren().add( minusBtn );
		this.getChildren().add( middlePane );
		this.getChildren().add( plusBtn );

		middlePane.toFront();

		MouseEnteredListener mel = new MouseEnteredListener();
		this.setOnMouseEntered( mel );
		this.setOnMouseExited( mel );
	}

	public void setScaleFactor( double newScaleFactor ) {
		this.scaleFactor = newScaleFactor;
	}

	private void displayBtns() {
		TranslateTransition ttm =
			new TranslateTransition( Duration.millis( 200 ), minusBtn );
		TranslateTransition ttp =
			new TranslateTransition( Duration.millis( 200 ), plusBtn );
		ttm.setToX( minusBtnXOut );
		ttp.setToX( plusBtnXOut );
		ttm.play();
		ttp.play();
	}

	private void hideBtns() {
		TranslateTransition ttm =
			new TranslateTransition( Duration.millis( 200 ), minusBtn );
		TranslateTransition ttp =
			new TranslateTransition( Duration.millis( 200 ), plusBtn );
		ttm.setToX( minusBtnXIn );
		ttp.setToX( plusBtnXIn );
		ttm.play();
		ttp.play();
	}

	private class BtnListener implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// TODO Auto-generated method stub
		}
	}

	private class MouseEnteredListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if( event.getEventType() == MouseEvent.MOUSE_ENTERED ) {
				displayBtns();
			}
			if( event.getEventType() == MouseEvent.MOUSE_EXITED ) {
				hideBtns();
			}
		}
	}
	private class DragListener implements EventHandler<MouseEvent> {
		private double mouseInSceneX;
		private double mouseInSceneY;

		@Override
		public void handle(MouseEvent event) {
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				mouseInSceneX = event.getSceneX();
				mouseInSceneY = event.getSceneY();
			}
			if( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
				setCursor( Cursor.MOVE );
				double xChange = event.getSceneX() - this.mouseInSceneX;
				double yChange = event.getSceneY() - this.mouseInSceneY;

				setTranslateX(getTranslateX() + xChange * ( 1/scaleFactor ));
				setTranslateY(getTranslateY() + yChange * ( 1/scaleFactor ));

				if( getTranslateX() < 0 ) {
					setTranslateX(0);
				}
				if( getTranslateY() < 0 ) {
					setTranslateY(0);
				}
				/*
				if( getTranslateX() > containerSizeX ) {
					setTranslateX(containerSizeX);
				}
				if( getTranslateY() > containerSizeY ) {
					setTranslateY(containerSizeY);
				}
				*/

				this.mouseInSceneX = event.getSceneX();
				this.mouseInSceneY = event.getSceneY();
			}
			if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				setCursor( Cursor.DEFAULT );
			}
		}
	}
}
