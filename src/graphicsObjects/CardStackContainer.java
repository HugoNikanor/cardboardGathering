package graphicsObjects;

import gamePieces.Card;
import gamePieces.CardCollection;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
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

	
	public CardStackContainer( CardCollection.Collections type, EventHandler<Event> listener, double xPos, double yPos ) {
		cardStack = new CardStackPane( type, listener, Card.WIDTH, Card.HEIGHT );

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

	public CardCollection.Collections getType() {
		return cardStack.getType();
	}

	/**
	 * @return the getFromDeckBtn
	 */
	public Button getGetFromDeckBtn() {
		return getFromDeckBtn;
	}

	/**
	 * @return the cardStack
	 */
	public CardStackPane getCardStack() {
		return cardStack;
	}
}
