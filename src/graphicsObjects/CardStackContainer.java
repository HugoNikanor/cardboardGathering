package graphicsObjects;

import gamePieces.Card;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

/**
 * An object to keep the DeckPane and GetDeckCardBtn in <br>
 */
public class CardStackContainer extends Pane {
	private Button getFromDeckBtn;
	private CardStackPane cardStack;

	public static final double WIDTH = Card.WIDTH + 40;
	public static final double HEIGHT = Card.HEIGHT;
	
	public CardStackContainer( CardStackPane cardStack, EventHandler<MouseEvent> listener, double xPos, double yPos ) {
		//deck = new CardStackPane(listener, "initialText", Card.WIDTH, Card.HEIGHT,  0, 0);
		this.cardStack = cardStack;

		getFromDeckBtn = new Button();
		getFromDeckBtn.getTransforms().add( new Rotate(90, 0, 0, 0, Rotate.Z_AXIS) );
		getFromDeckBtn.setText( "Get Card" );
		getFromDeckBtn.setTranslateX( WIDTH );
		getFromDeckBtn.setMinHeight( WIDTH - Card.WIDTH - 10 );
		getFromDeckBtn.setMaxHeight( WIDTH - Card.WIDTH - 10 );
		getFromDeckBtn.setMinWidth( HEIGHT );

		getFromDeckBtn.setPrefHeight( 40 );
		getFromDeckBtn.setPrefWidth( 100 );

		this.setPrefWidth( WIDTH );
		this.setPrefHeight( HEIGHT );
		this.setTranslateX( xPos );
		this.setTranslateY( yPos );

		this.getChildren().addAll(cardStack, getFromDeckBtn);
	}
	
	public void setText( String newText ) {
		cardStack.setText( newText );
	}
}
