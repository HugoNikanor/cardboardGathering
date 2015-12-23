package gamePieces;

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

public class Token extends MovableGamePiece {
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

	public Token() {
		super( 100, 50, Collections.BATTLEFIELD );


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

		//DragListener dl = new DragListener();
		//middlePane.setOnMousePressed( dl );
		//middlePane.setOnMouseDragged( dl );
		//middlePane.setOnMouseReleased( dl );
		
		xPos = 200;
		yPos = 200;

		this.setTranslateX( xPos );
		this.setTranslateY( yPos );

		Pane containerPane = new Pane();

		containerPane.getChildren().add( minusBtn );
		containerPane.getChildren().add( middlePane );
		containerPane.getChildren().add( plusBtn );

		this.getChildren().add( containerPane );

		middlePane.toFront();

		MouseEnteredListener mel = new MouseEnteredListener();
		this.setOnMouseEntered( mel );
		this.setOnMouseExited( mel );
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

	/**
	 * Used for the 'plus' and 'minus' buttons
	 */
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
}
