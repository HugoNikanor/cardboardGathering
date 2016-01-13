package gamePieces;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chat.ChatContainer;
import chat.ChatStream;
import chat.MessageInfo;

import database.JSONCardReader;

import exceptions.CardNotFoundException;

import graphicsObjects.PlayerBtnPane;
import graphicsObjects.TokenContainer;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import network.Connection;
import network.ConnectionPool;

import controllers.LifecounterButtonController;
import controllers.LifecounterNumberController;

import serverPackets.CardBetweenCollectionsPacket;
import serverPackets.CardCreatedPacket;
import serverPackets.CardFromDatabasePacket;
import serverPackets.CardMovePacket;
import serverPackets.HealthSetPacket;
import serverPackets.PoisonSetPacket;

public class Player extends Pane {
	private CardCollection deckCards;
	private CardCollection handCards;
	private CardCollection battlefieldCards;
	private CardCollection graveyardCards;

	private IntegerProperty observableHealth;
	private IntegerProperty observablePoison;;

	private static final int HEIGHT = 132;//110;

	private static final int defaultHealth = 20;
	private static final int defaultPoison = 0;

	private Connection connection;
	private boolean shouldSend;

	private PlayerBtnPane playerBtnPane;
	private TokenContainer tokenContainer;
	private GridPane lifecounter;
	private ChatContainer chatContainer;

	private double scaleFactor;

	/**
	 * Used by JSONCardReader to make every card have a uniqe id <br>
	 * Note that id's are only uniqe per player
	 */
	private CardIdCounter counter;

	/** the card reader, stored here to allow access to it from within this class */
	private JSONCardReader jCardReader;

	private EventHandler<MouseEvent> cardPlayHandler;

	// this is needed to stop the GC — I think...
	private LifecounterNumberController lifeNumController;
	// this is here for symmetry
	private LifecounterButtonController lifeBtnController;

	/**
	 * Use this for the local player
	 * 
	 * @param jCardReader
	 *            where the cards should be read from
	 * @param cardPlayHandler
	 *            event handler for when the cards are activated in the hand and
	 *            should be played
	 * @param cardList
	 *            A string array of the names of the cards desired to be created
	 */
	public Player( double width, JSONCardReader jCardReader, EventHandler<MouseEvent> cardPlayHandler, String[] cardList ) {
		this( jCardReader, cardList );
		this.cardPlayHandler = cardPlayHandler;

		this.connection = ConnectionPool.getConnection();
		shouldSend = true;
		new Thread( new SendCardDataThread() ).start();

		for( Card temp : deckCards ) {
			temp.setOnMouseClicked( cardPlayHandler );
			temp.setShouldSend( true );
		}

		deckCards.getObservableCardProperty().addListener( 
				(ov, oldValue, newValue ) -> {
			try {
				drawCard( newValue );
			} catch ( CardNotFoundException e ) {
				e.printStackTrace();
			} catch( NullPointerException e ) {
				ChatStream.print( "null card", MessageInfo.ERROR );
			}
		});
		graveyardCards.getObservableCardProperty().addListener(
				(ov, oldValue, newValue ) -> {
			try {
				graveToHand( newValue );
			} catch( CardNotFoundException e ) {
				e.printStackTrace();
			} catch( NullPointerException e ) {
				ChatStream.print( "null card", MessageInfo.ERROR );
			}
		});

		//===============================//
		//         JavaFX below          //
		//===============================//
		this.setPrefWidth( width );
		this.setPrefHeight(HEIGHT);
		this.getStyleClass().add("cards-in-hand-pane");

		playerBtnPane = new PlayerBtnPane(HEIGHT, HEIGHT, new BtnPaneHandler());
		this.getChildren().add(playerBtnPane);

		tokenContainer = new TokenContainer( new CardCreateHandler() );

		try {
			URL url = Paths.get( "fxml/LifecounterFull.fxml" ).toUri().toURL();
			FXMLLoader fullLoader = new FXMLLoader( url );
			lifecounter = fullLoader.load();
			lifeBtnController = (LifecounterButtonController) fullLoader.getController();
				lifeBtnController
					.bindNumbers( observableHealth, observablePoison )
					.setDefaultValues( defaultHealth, defaultPoison );
		} catch( Exception e ) {
			e.printStackTrace();
		}

		chatContainer = new ChatContainer( lifecounter );

		observableHealth.addListener( (ov, oVal, nVal ) -> {
			ConnectionPool.getConnection().sendPacket(
					new HealthSetPacket( nVal.intValue() ));
		});
		observablePoison.addListener( (ov, oVal, nVal ) -> {
			ConnectionPool.getConnection().sendPacket(
					new PoisonSetPacket( nVal.intValue() ));
		});
	}

	/**
	 * Use this directly for the remote player <br>
	 * Be aware that this doen't initiate any of the JavaFX functions
	 * except for the panes that sholud be displayed on the battlefield
	 * @param jCardReader
	 *            where the cards should be read from
	 * @param cardList
	 *            A string array of the names of the cards desired to be created
	 */
	public Player( JSONCardReader jCardReader, String[] cardList ) {
		this.jCardReader = jCardReader;

		observableHealth = new SimpleIntegerProperty();
		observablePoison = new SimpleIntegerProperty();

		observableHealth.set( defaultHealth );
		observablePoison.set( defaultPoison );

		counter = new CardIdCounter( 0 );
		deckCards        = new CardCollection( CardCollection.CollectionTypes.DECK, jCardReader, counter, cardList );
		handCards        = new CardCollection( CardCollection.CollectionTypes.HAND );
		battlefieldCards = new CardCollection( CardCollection.CollectionTypes.BATTLEFIELD );
		graveyardCards   = new CardCollection( CardCollection.CollectionTypes.GRAVEYARD );

		shouldSend = false;

		tokenContainer = new TokenContainer( new CardCreateHandler() );

		try {
			URL url = Paths.get("fxml/LifecounterNumbers.fxml").toUri().toURL();
			FXMLLoader numberLoader = new FXMLLoader( url );
			lifecounter = numberLoader.load();
			lifeNumController = (LifecounterNumberController) numberLoader.getController();
			lifeNumController.bindNumbers( observableHealth, observablePoison );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public Pane getDeckGraphic( boolean local ) {
		return deckCards.getGraphics( local );
	}

	public Pane getGraveGraphic( boolean local ) {
		return graveyardCards.getGraphics( local );
	}

	/**
	 * A thread checking what has happened the last game tick 
	 * (except that I don't have game ticks )
	 * and sends that data over the server<br>
	 * It also does some things localy
	 */
	private class SendCardDataThread implements Runnable {
		private List<Card> cardsToDeck;
		private List<Card> cardsToGrave;

		public SendCardDataThread() {
			cardsToDeck  = Collections.synchronizedList(new ArrayList<Card>());
			cardsToGrave = Collections.synchronizedList(new ArrayList<Card>());
		}
		@Override
		public void run() {
			synchronized( this ) {
				while( true ){
					long startTime = System.currentTimeMillis();
					// TODO I have had this throw ConcurrentModifiactionException when the other player is playing cards...
					// this crashes this thread, but not anything else, I think
					for( Card card : battlefieldCards ) {
						if( card.isShouldSend() ) {
							double newX = card.getTranslateX();
							double newY = card.getTranslateY();
							double newRotate = card.getRotate();

							try {
								// TODO try to have smaller hitboxes for the cardcollections
								// card to deck
								if( !card.isBeingUsed() &&
									//deckCont.localToScene(deckCont.getBoundsInLocal())
									// TODO this may throw an ArrayIndexOutOfBoundsException: -1
									// this is now catchedv but may crash in other ways
									deckCards.getGraphics(true).localToScene(deckCards.getGraphics(true).getBoundsInLocal())
										.intersects(card.localToScene( card.getBoundsInLocal())) ) {
									//ChatStream.print( "moving to deck", MessageInfo.SYSTEM, connection );
									cardsToDeck.add( card );
								}
							} catch( ArrayIndexOutOfBoundsException ex ) {
								System.out.println( "AIOOBE deck" );
							}
							try {
								// card to graveyard
								if( !card.isBeingUsed() &&
									//graveCont.localToScene(graveCont.getBoundsInLocal())
									graveyardCards.getGraphics(true).localToScene(graveyardCards.getGraphics(true).getBoundsInLocal())
										.intersects(card.localToScene( card.getBoundsInLocal())) ) {
									// TODO I have had this throw a NoClassDefFoundError. I believe that it has to do with my singleton not being thread safe
									//ChatStream.print( "moving to grave", MessageInfo.SYSTEM, connection );
									cardsToGrave.add( card );
								}
							} catch( ArrayIndexOutOfBoundsException ex ) {
								System.out.println( "AIOOBE grave" );
							}

							if( newX != card.getOldX() ||
								newY != card.getOldY() ||
								newRotate != card.getOldRotate() ) {
								connection.sendPacket( new CardMovePacket( card.getCardId(), newX, newY, newRotate ) );
							}

							card.setOldX( card.getTranslateX() );
							card.setOldY( card.getTranslateY() );
							card.setOldRotate( card.getRotate() );
						}
					}

					for( Card card : cardsToDeck ) {
						cardToDeck( card );
					}
					cardsToDeck.clear();

					for( Card card : cardsToGrave ) {
						cardToGrave( card );
					}
					cardsToGrave.clear();

					try {
						long waitTime = Connection.UPDATE_TIME - (System.currentTimeMillis() - startTime);
						if( waitTime >= 0 )
							this.wait( waitTime );
					} catch( InterruptedException e ) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class CardCreateHandler implements EventHandler<ActionEvent> {
		// TODO remove focus when drawer is closed
		@Override
		public void handle(ActionEvent event) {
			if( event.getSource() == tokenContainer.getCardDatabaseBtn() ||
			    event.getSource() == tokenContainer.getCardDatabaseField() ) {

				String cardToCreate = 
					tokenContainer.getCardDatabaseField().getText();

				tokenContainer.clearError();
				createCardFromDatabase( cardToCreate );

				tokenContainer.clearDatabaseTextField();
			}
			if( event.getSource() == tokenContainer.getCardCreateNameField() ||
			    event.getSource() == tokenContainer.getCardCreateAction() ) {

				String nameCreate = tokenContainer.getCardCreateNameField().getText();
				String infoCreate = tokenContainer.getCardCreateInfoText();

				tokenContainer.clearCardCreateTextAreas();

				createCard( new Card(
							nameCreate,
							"Token",
							"",
							infoCreate,
							"",
							-100,
							-100,
							-100,
							0,
							0,
							0,
							0,
							0,
							0,
							0) );
			}
		}
	}

	public void createCard( Card inCard, long id ) {
		Card card = new Card( inCard, id );
		card.setCurrentLocation( CardCollection.CollectionTypes.HAND );
		handCards.add( card );
		// maybe have this in the 'shouldSend' block
		card.setOnMouseClicked( cardPlayHandler );

		card.setTranslateY( card.getHandPopupValue() );

		if( shouldSend ) {
			//card.setConnection( connection );
			card.setShouldSend( true );
			connection.sendPacket( new CardCreatedPacket( card ) );
		}
		Platform.runLater(new Thread(() -> {
			this.getChildren().add( card );
			this.rearangeCards();
		}));
		updateScaleFactor( scaleFactor );
	}
	public void createCard( Card newCard ) {
		createCard( newCard, counter.getCounterAndIncrament() );
	}
	public void createCardFromDatabase( String cardName ) {
		try {
			Card card = jCardReader.get( cardName, counter.getCounter() );
			counter.incrament();
			card.setCurrentLocation( CardCollection.CollectionTypes.HAND );

			card.setTranslateY( card.getHandPopupValue() );

			handCards.add( card );
			card.setOnMouseClicked( cardPlayHandler );
			if( shouldSend ) {
				//card.setConnection( connection );
				card.setShouldSend( true );
				connection.sendPacket( new CardFromDatabasePacket( cardName ) );
			}
			Platform.runLater(new Thread(() -> {
				this.getChildren().add( card );
				this.rearangeCards();
			}));
			updateScaleFactor( scaleFactor );
		} catch( CardNotFoundException e ) {
			String errorMsg = "No card with that name in database";
			System.out.println( errorMsg );
			tokenContainer.error( errorMsg );
		}

	}

	public void cardToDeck( long cardId ) {
		try {
			cardToDeck( battlefieldCards.getCard(cardId) );
		} catch (CardNotFoundException e ) {
			e.printStackTrace();
		}
	}
	public void cardToDeck( Card card ) {
		try {
			moveCardBetweenCollections(battlefieldCards, deckCards, card );
			Platform.runLater(new Thread(() -> {
				((Pane) card.getParent()).getChildren().remove(card);
			}));
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void cardToGrave( long cardId ) {
		try {
			cardToGrave( battlefieldCards.getCard(cardId) );
		} catch( CardNotFoundException e ) {
			e.printStackTrace();
		}
	}
	public void cardToGrave( Card card ) {
		try {
			moveCardBetweenCollections(battlefieldCards, graveyardCards, card );
			Platform.runLater(new Thread(() -> {
				((Pane) card.getParent()).getChildren().remove(card);
			}));
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes care of the inputs from the buttons in the players pane
	 * @see graphicsObjects.PlayerBtnPane
	 * @see graphicsObjects.PlayerResetBoardBtn
	 * @see graphicsObjects.PlayerResetRotationBtn
	 * @see graphicsObjects.PlayerShuffleHandBtn
	 */
	private class BtnPaneHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			/*
			 * Shuffles the cards in the hand.
			 */
			if( event.getSource() == playerBtnPane.getShuffleBtn() ) {
				handCards.shuffleCards();
				rearangeCards();
			}

			/*
			 * Places the cards in accending order,
			 * starting from the top left and going down and right
			 * Starts a new 'stack' after 'cardsPerRow' cards.
			 * This also resets the cards scaling
			 */
			if( event.getSource() == playerBtnPane.getResetBoardBtn() ) { 
				int cardsPerRow = 12;
				int displacement = 0;
				int laps = 0;
				int offset = 30;
				for( int i = 0; i < battlefieldCards.size(); i++ ) {
					if(i % cardsPerRow  == 0 && i != 0) {
						displacement = 0;
						laps++;
					}
					Card tempCard = battlefieldCards.getCard(i);

					tempCard.smoothPlace(
							displacement*10 + 200*laps + offset, displacement*20, 500);
					tempCard.smoothSetScale(1.0, 500);
					tempCard.giveFocus();

					displacement++;
				}
			}

			/*
			 * Rotates all cards on the own battlefield back to upright
			 */
			if( event.getSource() == playerBtnPane.getResetRotationBtn() ){
				for( Card tempCard : battlefieldCards ) {
					tempCard.smoothSetRotate( 0 );
				}
			}
		}
	}

	/**
	 * Notify the lower classes about that everything has changed scale <br>
	 * Needed for card movement to scale it to the window size
	 * @param newScaleFactor what scale the new window is to the original
	 */
	public void updateScaleFactor( double newScaleFactor ) {
		this.scaleFactor = newScaleFactor;
		for( Card tch : handCards ) {
			tch.setScaleFactor( newScaleFactor );
		}
		for( Card tcd : deckCards ) {
			tcd.setScaleFactor( newScaleFactor );
		}
		for( Card tcg : graveyardCards ) {
			tcg.setScaleFactor( newScaleFactor );
		}
		for( Card tcb : battlefieldCards ) {
			tcb.setScaleFactor( newScaleFactor );
		}
	}

	/**
	 * Moves the cards to their new position, if it where to change,
	 * Used to recenter the cards when playing a card, and to move
	 * them when "shuffle" is pressed.
	 */
	private void rearangeCards() {
		TranslateTransition tt;
		for( Card temp : handCards ) {
			 tt = new TranslateTransition(Duration.millis(200), temp);
			 tt.setToX( Card.WIDTH + temp.getPreferdMargin() + ( handCards.indexOf(temp) ) * ( temp.getWidth() + temp.getPreferdMargin() * 2) );
			 tt.play();
		}
	}

	/**
	 * Moves the next card from the deck to the hand
	 * @throws CardNotFoundException probably non fatal
	 */
	public void drawCard() throws CardNotFoundException {
		drawCard( deckCards.getNextCard().getCardId() );
	}


	/**
	 * Moves a set card from the deck to the hand
	 * @param cardId what card that should be drawn
	 * @throws CardNotFoundException probably non fatal
	 */
	public void drawCard( long cardId ) throws CardNotFoundException {
		drawCard( deckCards.getCard( cardId ));
	}

	/**
	 * Moves the choosen card from the deck to the hand
	 * @param card  what card that should be drawn
	 * @throws CardNotFoundException probably non fatal
	 */
	private void drawCard( Card card ) throws CardNotFoundException {
		moveCardBetweenCollections( deckCards, handCards, card );

		card.setTranslateY(card.getHandPopupValue());

		Platform.runLater( new Thread(() -> {
			this.getChildren().add(card);
			this.rearangeCards();
		}));
	}

	public void graveToHand( long cardId ) throws CardNotFoundException {
		graveToHand( graveyardCards.getCard( cardId ) );
	}

	private void graveToHand( Card card ) throws CardNotFoundException {
		moveCardBetweenCollections( graveyardCards, handCards, card );

		card.setTranslateY( card.getHandPopupValue() );

		Platform.runLater( new Thread(() -> {
			this.getChildren().add(card);
			this.rearangeCards();
		}));
		
	}

	/**
	 * Moves card to the table
	 * @param card the card to be move
	 * @param targetBattlefield the battlefield that the card should end up on
	 * currently this only really works if you use "matching" player and battlefield
	 */
	public void playCard(Card card, Battlefield targetBattlefield) {
		try {
			//double finalPosY = 25;
			double startY = card.getTranslateY();

			/*
			 * Moves the card upwards from the hand to the battlefield
			 * Then jerk it back down only to have it moved by actually
			 * placing it on the target tbattlefield.
			 */
			moveCardBetweenCollections( handCards, battlefieldCards, card );
			this.getChildren().remove(card);
			targetBattlefield.playCard( card );

			//card.setTranslateY( Battlefield.HEIGHT + startY );
			card.setTranslateY( ((Pane)card.getParent()).getPrefHeight() + startY );
			card.updateContainerSize();

			//double moveDistance = Battlefield.HEIGHT - finalPosY + startY;
			TranslateTransition tt = new TranslateTransition( Duration.millis(500), card );
			//tt.setByY( -moveDistance );
			tt.setToY( 10 );


			// Changes the hower action from "jump up" in hand
			// to "set focus" on the battlefield
			// TODO set this in a prettier way
			// the if check is a horrible hack
			if( shouldSend ) {
				card.setOnMouseEntered( card.getLocalMouseHandler() );
			}

			tt.play();

			// Maybe play this on animation finish
			this.rearangeCards();
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Move a card from one collection to another
	 * @param whatCard the card that should be moved
	 * @param oldCollection where the card should be taken from
	 * @param newCollection where the card sholud end up
	 * @throws CardNotFoundException if the card isn't in the old collection
	 */
	public void moveCardBetweenCollections(
		CardCollection oldCollection, CardCollection newCollection, Card whatCard)
		throws CardNotFoundException {
		System.out.println( oldCollection.getCollection().toString() );
		System.out.println( newCollection.getCollection().toString() );
		whatCard.setCurrentLocation( newCollection.getCollection() );
		newCollection.add( oldCollection.takeCard(whatCard) );
		if( shouldSend ) {
			System.out.println( "Sending " + whatCard.getCardId() );

			connection.sendPacket( new CardBetweenCollectionsPacket(
						oldCollection.getCollection(), newCollection.getCollection(), whatCard.getCardId()) );

		}
	}

	/**
	 * @param health set the health value of the player
	 * @see health
	 * @see changeHealth
	 */
	public void setHealth(int health) {
		observableHealth.set( health );
	}

	/**
	 * @param change how much the players life total should change
	 * @see health
	 * @see setHealth
	 */
	public void changeHealth(int change) {
		observableHealth.set( observableHealth.get() + change );
	}

	/**
	 * @param poisonCounters set how many poison counters the player should have
	 * @see poisonCounters
	 * @see changePoison
	 */
	public void setPoison(int poisonCounters) {
		observablePoison.set( poisonCounters );
	}
	
	/**
	 * @param change how many poison counters total the player has
	 * @see poisonCounters
	 * @see setPoison
	 */
	public void changePoison(int change) {
		observablePoison.set( observablePoison.get() + change );
	}

	/**
	 * @return the lifeCounter
	 */
	public GridPane getLifecounter() {
		return lifecounter;
	}

	/**
	 * @return the tokenContainer
	 */
	public TokenContainer getTokenContainer() {
		return tokenContainer;
	}

	/**
	 * @return the chatContainer
	 */
	public ChatContainer getChatContainer() {
		return chatContainer;
	}

	/**
	 * @return the counter
	 */
	public CardIdCounter getCounter() {
		return counter;
	}

	/**
	 * @return the deckCards
	 */
	public CardCollection getDeckCards() {
		return deckCards;
	}

	/**
	 * @return the handCards
	 */
	public CardCollection getHandCards() {
		return handCards;
	}

	/**
	 * @return the battlefieldCards
	 */
	public CardCollection getBattlefieldCards() {
		return battlefieldCards;
	}

	/**
	 * @return the graveyardCards
	 */
	public CardCollection getGraveyardCards() {
		return graveyardCards;
	}

	/**
	 * This is probably slow,
	 * Use only when there is no other way.
	 */
	public CardCollection getAllCards() {
		CardCollection retCol = new CardCollection( CardCollection.CollectionTypes.OTHER );

		retCol.addAll( handCards.getAllCards() );
		retCol.addAll( deckCards.getAllCards() );
		retCol.addAll( graveyardCards.getAllCards() );
		retCol.addAll( battlefieldCards.getAllCards() );

		return retCol;
	}
}
