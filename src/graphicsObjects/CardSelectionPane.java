package graphicsObjects;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import exceptions.CardNotFoundException;

import gamePieces.Card;
import gamePieces.CardCollection;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/**
 * A class for displaying a popup in the middle of the given pane with all the
 * cards from the given collection, when one of the cards are pressed that card
 * is returned <br>
 * <b>THIS LOCKS UP THE THREAD IT'S RUN ON!</b>
 */
public class CardSelectionPane {

	private Card returnCard;

	/**
	 * @return the card selected in the popup
	 * @param cards the collection of cards to display
	 * @param rootPane the pane to draw the selection pane upon
	 */
	public Card getStaticCard(CardCollection cards, Pane rootPane) throws CardNotFoundException {
		if( cards.size() <= 0 ) {
			throw new CardNotFoundException( "No cards in collection." );
		}

		CyclicBarrier latch = new CyclicBarrier(2);

		ScrollPane outerPane = new ScrollPane();
		FlowPane innerPane = new FlowPane();

		// It would be better if this went backwards,
		// since the cards in the deck are accessed that way.
		for( Card temp : cards ) {
			Card innerTemp = new Card(temp, temp.getCardId());
			innerTemp.setOnMouseClicked( new EventHandler<MouseEvent>() {
				@Override
				public void handle( MouseEvent event ) {
					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						returnCard = (Card) event.getSource();
						try {
							System.out.println( "Awaiting latch (button)..." );
							latch.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							e.printStackTrace();
						}
					}
				}
			});
			innerPane.getChildren().add(innerTemp);
		}

		innerPane.getStyleClass().add("card-select-pane-inner");
		innerPane.setVgap( 15 );
		innerPane.setHgap( 15 );
		innerPane.setAlignment( Pos.TOP_CENTER );

		outerPane.setContent( innerPane );
		outerPane.getStyleClass().add("card-select-pane-outer");


		double rootWidth  = rootPane.getWidth();
		double rootHeight = rootPane.getHeight();

		System.out.println( "rootWidth = " + rootWidth );
		System.out.println( "rootHeight = " + rootHeight );
		outerPane.setMinWidth  (( 2 * rootWidth ) / 3 );

		//outerPane.setPrefHeight(( 2 * rootHeight) / 3 );
		//TODO do this better!
		outerPane.setPrefHeight(( 2 * (rootHeight/*+Battlefield.HEIGHT*/)) / 3 );

		//outerPane.relocate( rootWidth/6, rootHeight/6 );
		//TODO do this better!
		// When the pane goes into battlefield then the buttons there stop working...
		outerPane.relocate( rootWidth/6, (rootHeight/*+Battlefield.HEIGHT*/)/6 /*- Battlefield.HEIGHT */);

		innerPane.setPrefWrapLength( 2*rootWidth / 3 );


		Platform.runLater(new Thread(() -> {
			rootPane.getChildren().add( outerPane );
		}));

		try {
			System.out.println( "Awaiting latch (outer)..." );
			latch.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		latch.reset();

		System.out.println( "latch released" );

		Platform.runLater(new Thread(() -> {
			rootPane.getChildren().remove( outerPane );
		}));

		// This can throw a card not found exception
		// but shouldn't do it as long as the collection
		// isn't modified while popup is open
		return cards.getCard( returnCard.getCardId());
	}
}
