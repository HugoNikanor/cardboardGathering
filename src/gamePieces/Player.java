package gamePieces;

import java.util.ArrayList;

import database.JSONCardReader;

import exceptions.CardNotFoundException;

import graphicsObjects.CardSelectionPane;
import graphicsObjects.CardStackContainer;
import graphicsObjects.LifeCounter;
import graphicsObjects.PlayerBtnPane;
import graphicsObjects.TokenContainer;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import network.Connection;

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

	private int health;
	private int poisonCounters;

	private static final int HEIGHT = 132;//110;

	private Connection connection;
	private boolean shouldSend;

	private CardStackCollectionHandler cardStackHandler;
	private CardStackContainer deckCont;
	private CardStackContainer graveCont;

	private PlayerBtnPane playerBtnPane;
	private LifeCounter lifeCounter;
	private TokenContainer tokenContainer;

	private double scaleFactor;

	/**
	 * Used by JSONCardReader to make every card have a uniqe id <br>
	 * Note that id's are only uniqe per player
	 */
	private CardIdCounter counter;

	/**
	 * the card reader, stored here to allow access to it from within
	 * this class
	 */
	private JSONCardReader jCardReader;

	private EventHandler<MouseEvent> cardPlayHandler;

	/**
	 * Use this for the local player
	 * 
	 * @param jCardReader
	 *            where the cards should be read from
	 * @param cardPlayHandler
	 *            event handler for when the cards are activated in the hand and
	 *            should be played
	 * @param connection
	 *            Called with {@code connection.sendPacket()} to push data to the server
	 *            @see network.Connection
	 *            @see serverPackets.NetworkPacket
	 * @param cardList
	 *            A string array of the names of the cards desired to be created
	 */
	public Player( JSONCardReader jCardReader, EventHandler<MouseEvent> cardPlayHandler, Connection connection, String[] cardList ) {
		this( jCardReader, cardList );
		this.cardPlayHandler = cardPlayHandler;

		this.connection = connection;
		shouldSend = true;
		new Thread( new SendCardDataThread() ).start();

		for( Card temp : deckCards ) {
			temp.setOnMouseClicked( cardPlayHandler );
			temp.setConnection( connection );
		}

		//===============================//
		//         JavaFX below          //
		//===============================//
		this.setPrefWidth(Battlefield.WIDTH);
		this.setPrefHeight(HEIGHT);
		this.getStyleClass().add("cards-in-hand-pane");

		playerBtnPane = new PlayerBtnPane(HEIGHT, HEIGHT, new BtnPaneHandler());
		this.getChildren().add(playerBtnPane);

		// Objects displayed on the battlefield
		cardStackHandler = new CardStackCollectionHandler();
		deckCont = new CardStackContainer(
			CardCollection.Collections.DECK,
			cardStackHandler,
			Battlefield.WIDTH - CardStackContainer.WIDTH - 10,
			Battlefield.HEIGHT - Card.HEIGHT - 10
		);
		deckCont.setText(Integer.toString( getDeckCards().size() ));
		graveCont = new CardStackContainer(
			CardCollection.Collections.GRAVEYARD,
			cardStackHandler,
			Battlefield.WIDTH - CardStackContainer.WIDTH - 10,
			10
		);
		tokenContainer = new TokenContainer( new CardCreateHandler() );

		lifeCounter = new LifeCounter(new LifeCounterHandler());

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
		counter = new CardIdCounter( 0 );
		deckCards        = new CardCollection( CardCollection.Collections.DECK, jCardReader, counter, cardList );
		handCards        = new CardCollection( CardCollection.Collections.HAND );
		battlefieldCards = new CardCollection( CardCollection.Collections.BATTLEFIELD );
		graveyardCards   = new CardCollection( CardCollection.Collections.GRAVEYARD );

		health = 20;
		poisonCounters = 0;

		shouldSend = false;

		// Objects displayed on the battlefield
		cardStackHandler = new CardStackCollectionHandler();
		deckCont = new CardStackContainer(
			CardCollection.Collections.DECK,
			cardStackHandler,
			Battlefield.WIDTH - CardStackContainer.WIDTH - 10,
			Battlefield.HEIGHT - Card.HEIGHT - 10
		);
		deckCont.setText( Integer.toString( getDeckCards().size() ));
		graveCont = new CardStackContainer(
			CardCollection.Collections.GRAVEYARD,
			cardStackHandler,
			Battlefield.WIDTH - CardStackContainer.WIDTH - 10,
			10
		);
		tokenContainer = new TokenContainer( new CardCreateHandler() );

		lifeCounter = new LifeCounter(new LifeCounterHandler());
	}

	private class SendCardDataThread implements Runnable {

		private ArrayList<Card> cardsToDeck;
		private ArrayList<Card> cardsToGrave;

		public SendCardDataThread() {
			cardsToDeck = new ArrayList<Card>();
			cardsToGrave = new ArrayList<Card>();
		}
		@Override
		public void run() {
			synchronized( this ) {
				while( true ){
					long startTime = System.currentTimeMillis();
					for( Card card : battlefieldCards ) {
						if( card.isShouldSend() ) {
							double newX = card.getTranslateX();
							double newY = card.getTranslateY();
							double newRotate = card.getRotate();

							// card to deck
							if( !card.isBeingUsed() &&
							    deckCont.getTranslateX() < newX + card.getHeight() / 4 &&
							    deckCont.getTranslateY() < newY + card.getWidth()  / 4 &&
								deckCont.getTranslateX() + deckCont.getWidth()  > newX + card.getWidth()  - card.getWidth()  / 4 &&
								deckCont.getTranslateY() + deckCont.getHeight() > newY + card.getHeight() - card.getHeight() / 4 ) {
								System.out.println( "moving to deck" );
								cardsToDeck.add( card );
							}
							// card to graveyard
							if( !card.isBeingUsed() &&
							    graveCont.getTranslateX() < newX + card.getHeight() / 4 &&
							    graveCont.getTranslateY() < newY + card.getWidth()  / 4 &&
								graveCont.getTranslateX() + graveCont.getWidth()  > newX + card.getWidth()  - card.getWidth()  / 4 &&
								graveCont.getTranslateY() + graveCont.getHeight() > newY + card.getHeight() - card.getHeight() / 4 ) {
								System.out.println( "moving to grave" );
								cardsToGrave.add( card );
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
			String str = ((TextField)(event.getSource())).getText();
			System.out.println( str );
			tokenContainer.clearError();
			createCardFromDatabase( str );

			tokenContainer.clearTextArea();
		}
	}

	public void createCard( Card card, long id ) {
		card.setCurrentLocation( CardCollection.Collections.HAND );
		handCards.add( card );
		// maybe have this in the 'shouldSend' block
		card.setOnMouseClicked( cardPlayHandler );

		card.setTranslateY( card.getHandPopupValue() );

		if( shouldSend ) {
			card.setConnection( connection );
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
			Card card = jCardReader.get( cardName, counter.getCounterAndIncrament() );
			card.setCurrentLocation( CardCollection.Collections.HAND );

			card.setTranslateY( card.getHandPopupValue() );

			handCards.add( card );
			card.setOnMouseClicked( cardPlayHandler );
			if( shouldSend ) {
				card.setConnection( connection );
				// TODO, don't send card if it's not valid
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
	 * Changes the players health and poison values when the buttons
	 * on the lifecounter are clicked.
	 * @see grahicsObjects.LifeCounter
	 */
	private class LifeCounterHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if( event.getSource() == lifeCounter.getHpUpBtn() ) {
				changeHealth(1);
			}
			if( event.getSource() == lifeCounter.getHpDownBtn() ) {
				changeHealth(-1);
			}
			if( event.getSource() == lifeCounter.getPoisonUpBtn() ) {
				changePoison(1);
			}
			if( event.getSource() == lifeCounter.getPoisonDownBtn() ) {
				changePoison(-1);
			}
			if( event.getSource() == lifeCounter.getResetBtn() ) {
				setHealth(20);
				setPoison(0);
			}
		}
	}

	/**
	 * Event handler for the cardStackCollections <br>
	 */
	private class CardStackCollectionHandler implements EventHandler<Event> {
		private void handleDeckPress() {
			try {
				drawCard();
			} catch (CardNotFoundException e) {
				System.out.println(
					"Player " + this + 
					" trying to draw cards with an empty deck");
			}
		}

		private void handleDeckBtn() {
			// This locks up the controls in the battlefield since it
			// is somehow drawn on top of it...
			new Thread(() -> {
				try {
					Card card = new CardSelectionPane().getStaticCard( deckCards, Player.this );
					Player.this.drawCard( card );
				} catch (CardNotFoundException e) {
					e.printStackTrace();
				}
			}).start();
		}

		private void handleGravePress() {
			// Should this even do anything
		}

		private void handleGraveBtn() {
			// This locks up the controls in the battlefield since it
			// is somehow drawn on top of it...
			new Thread(() -> {
				try {
					Card card = new CardSelectionPane().getStaticCard( graveyardCards, Player.this );
					//moveCardBetweenCollections( graveyardCards, handCards, card );
					graveToHand( card );
				} catch (CardNotFoundException e) {
					e.printStackTrace();
				}
			}).start();
		}


		@Override
		public void handle(Event event) {
			if( event.getSource() == deckCont.getCardStack() ) {
				this.handleDeckPress();
			}
			if( event.getSource() == deckCont.getGetFromDeckBtn() ) {
				this.handleDeckBtn();
			}
			if( event.getSource() == graveCont.getCardStack() ) {
				this.handleGravePress();
			}
			if( event.getSource() == graveCont.getGetFromDeckBtn() ) {
				this.handleGraveBtn();
			}
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
				for( int i = 0; i < battlefieldCards.size(); i++ ) {
					if(i % cardsPerRow  == 0 && i != 0) {
						displacement = 0;
						laps++;
					}
					Card tempCard = battlefieldCards.get(i);

					tempCard.smoothPlace(
							displacement*10 + 200*laps, displacement*20, 500);
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
	public void drawCard( Card card ) throws CardNotFoundException {
		moveCardBetweenCollections( deckCards, handCards, card );

		card.setTranslateY(card.getHandPopupValue());

		Platform.runLater( new Thread(() -> {
			this.getChildren().add(card);
			this.rearangeCards();
		}));

		// Set the text on the graphical deck
		deckCont.setText(Integer.toString(getDeckCards().size()));
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
			double finalPosY = 25;
			double startY = card.getTranslateY();

			/*
			 * Moves the card upwards from the hand to the battlefield
			 * Then jerk it back down only to have it moved by actually
			 * placing it on the target tbattlefield.
			 */
			moveCardBetweenCollections( handCards, battlefieldCards, card );
			this.getChildren().remove(card);
			targetBattlefield.getChildren().add(card);

			card.setTranslateY( Battlefield.HEIGHT + startY );

			double moveDistance = Battlefield.HEIGHT - finalPosY + startY;
			TranslateTransition tt = new TranslateTransition( Duration.millis(500), card );
			tt.setByY( -moveDistance );


			// Changes the hower action from "jump up" in hand
			// to "set focus" on the battlefield
			card.setOnMouseEntered( card.getMouseEventHandler() );

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
		if( shouldSend ) {
			connection.sendPacket( new HealthSetPacket( health ) );
		}
		this.health = health;
		lifeCounter.setHealthValue(getHealth());
	}

	/**
	 * @param change how much the players life total should change
	 * @see health
	 * @see setHealth
	 */
	public void changeHealth(int change) {
		setHealth( getHealth() + change );
	}

	/**
	 * @param poisonCounters set how many poison counters the player should have
	 * @see poisonCounters
	 * @see changePoison
	 */
	public void setPoison(int poisonCounters) {
		if( shouldSend ) {
			connection.sendPacket( new PoisonSetPacket( poisonCounters ) );
		}
		this.poisonCounters = poisonCounters;
		lifeCounter.setPoisonValue(getPoison());
	}
	
	/**
	 * @param change how many poison counters total the player has
	 * @see poisonCounters
	 * @see setPoison
	 */
	public void changePoison(int change) {
		setPoison( getPoison() + change );
	}

	/**
	 * @return the deckCont
	 */
	public CardStackContainer getDeckCont() {
		return deckCont;
	}

	/**
	 * @return the graveCont
	 */
	public CardStackContainer getGraveCont() {
		return graveCont;
	}

	/**
	 * @return the lifeCounter
	 */
	public LifeCounter getLifeCounter() {
		return lifeCounter;
	}

	/**
	 * @return the tokenContainer
	 */
	public TokenContainer getTokenContainer() {
		return tokenContainer;
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
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @return the poisonCounters
	 */
	public int getPoison() {
		return poisonCounters;
	}

}
