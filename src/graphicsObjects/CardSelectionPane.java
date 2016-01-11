package graphicsObjects;

import java.io.File;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import exceptions.CardNotFoundException;

import gamePieces.Card;
import gamePieces.CardCollection;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * TODO rewrite this to be more of a singleton
 */
public class CardSelectionPane {

	private static Card returnCard;

	/**
	 * replaces the current scene in the stage with a card selection scene <br>
	 * It then returns the card you pressed in the new scene, and the scene is switched back <br>
	 * This is rather slow since it manually adds all the cards when the button is pressed
	 * @param cards from which collection the cards displayed should be taken
	 * @param stage which stage the scene should be set on
	 */
	public static Card getCard( CardCollection cards, Stage stage ) throws CardNotFoundException {
		if( cards.size() <= 0 ) {
			throw new CardNotFoundException( "No cards in collection." );
		}

		ScrollPane outerPane = new ScrollPane();
		FlowPane innerPane = new FlowPane();
		innerPane.getStyleClass().add("card-select-pane-inner");
		innerPane.setVgap( 15 );
		innerPane.setHgap( 15 );
		innerPane.setAlignment( Pos.CENTER );
		innerPane.setPrefWrapLength( stage.getWidth() );

		outerPane.setContent( innerPane );
		outerPane.setPrefViewportHeight( stage.getHeight() );
		outerPane.getStyleClass().add("card-select-pane-outer");
		outerPane.setHbarPolicy( ScrollBarPolicy.NEVER );

		StackPane rootPane = new StackPane();
		Scene defaultScene = stage.getScene();

		rootPane.getChildren().add( outerPane );

		//rootPane.getTransforms().add( new Scale( 1.5, 1.5, 0d, 0d ) );
		//innerPane.getTransforms().add( new Scale( 1.5, 1.5, 0d, 0d ) );

		Scene chooseScene = new Scene( rootPane, stage.getWidth(), stage.getHeight() );

		// Css is aparently set per scene...
		File styleFile = new File("stylesheets/stylesheet.css");
		String styleFilePath = "file:///" + styleFile.getAbsolutePath().replace("\\", "/");
		chooseScene.getStylesheets().add(styleFilePath);


		CyclicBarrier latch = new CyclicBarrier(2);


		// It would be better if this went backwards,
		// since the cards in the deck are accessed that way.
		/*
		for( Card temp : cards ) {
			Card innerTemp = new Card(temp, temp.getCardId());
			//innerTemp.getTransforms().add( new Scale( 1.5, 1.5, 0d, 0d ) );
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
		*/
		for( Card temp : cards ) {
			Card innerTemp = new Card(temp, temp.getCardId());
			innerTemp.setShouldSend( true );
			innerTemp.addEventHandler( MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle( MouseEvent event ) {
					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						((Card) event.getSource()).removeEventHandler( MouseEvent.MOUSE_CLICKED, this );
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


		// change scene
		Platform.runLater( new Thread(() -> {
			stage.setScene( chooseScene );
		}));

		try {
			System.out.println( "Awaiting latch (outer)..." );
			latch.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
		latch.reset();

		System.out.println( "latch released" );

		// change back scene
		Platform.runLater(new Thread(() -> {
			stage.setScene( defaultScene );
			defaultScene.getRoot().setScaleY( defaultScene.getRoot().getScaleY() );
		}));


		// This can throw a card not found exception
		// but shouldn't do it as long as the collection
		// isn't modified while popup is open
		return cards.getCard( returnCard.getCardId());
	}

	/**
	 * @return the card selected in the popup
	 * @param cards the collection of cards to display
	 * @param rootPane the pane to draw the selection pane upon
	 */
	public static Card getCard(CardCollection cards, Pane rootPane) throws CardNotFoundException {
		if( cards.size() <= 0 ) {
			throw new CardNotFoundException( "No cards in collection." );
		}

		CyclicBarrier latch = new CyclicBarrier(2);

		ScrollPane outerPane = new ScrollPane();
		FlowPane innerPane = new FlowPane();

		// It would be better if this went backwards,
		// since the cards in the deck are accessed that way.
		/*
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
		*/
		for( Card temp : cards ) {
			//Card innerTemp = new Card(temp, temp.getCardId());
			temp.addEventHandler( MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle( MouseEvent event ) {
					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						returnCard = (Card) event.getSource();
						try {
							System.out.println( "Awaiting latch (button)..." );
							latch.await();
							temp.removeEventHandler( MouseEvent.MOUSE_CLICKED, this );
						} catch (InterruptedException | BrokenBarrierException e) {
							e.printStackTrace();
						}
					}
				}
			});
			innerPane.getChildren().add(temp);
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
