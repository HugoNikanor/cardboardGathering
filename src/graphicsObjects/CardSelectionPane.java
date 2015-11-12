package graphicsObjects;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import exceptions.CardNotFoundException;

import gamePieces.Card;
import gamePieces.CardCollection;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class CardSelectionPane extends ScrollPane {

	private Card returnCard;

	public Card getStaticCard(CardCollection cards, Pane rootPane) throws CardNotFoundException {

		CyclicBarrier latch = new CyclicBarrier(2);

		ScrollPane outerPane = new ScrollPane();
		FlowPane innerPane = new FlowPane();


		// This should maybe be made a bit safer...
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
			//System.out.println( "added: " + innerTemp.getCardName() );
		}

		outerPane.setContent( innerPane );
		outerPane.setStyle("-fx-background-color: DAE6F3;");

		double rootWidth  = rootPane.getWidth();
		double rootHeight = rootPane.getHeight();

		System.out.println( "rootWidth = " + rootWidth );
		System.out.println( "rootHeight = " + rootHeight );
		outerPane.setMinWidth  (( 2 * rootWidth ) / 3 );
		outerPane.setPrefHeight(( 2 * rootHeight) / 3 );

		outerPane.relocate( rootWidth / 6, 0 );

		innerPane.setPrefWrapLength( 2*rootWidth / 3 );


		Platform.runLater(new Thread(() -> {
			rootPane.getChildren().add( outerPane );

			System.out.println( "plat.run " + outerPane.getMinWidth() );
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
			System.out.println( "End " + outerPane.getMinWidth() );
			rootPane.getChildren().remove( outerPane );
		}));

		return cards.getCard( returnCard.getCardId());
	}
}
