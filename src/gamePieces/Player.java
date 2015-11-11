package gamePieces;

import database.JSONCardReader;
import exceptions.CardNotFoundException;

import graphicsObjects.DeckContainer;
//import graphicsObjects.DeckPane;
import graphicsObjects.GraveyardPane;
import graphicsObjects.LifeCounter;
import graphicsObjects.PlayerBtnPane;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import network.Connection;

import serverPackets.CardDrawPacket;
import serverPackets.CardPlayedPacket;
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

	private PlayerBtnPane playerBtnPane;
	//private DeckPane deckPane;
	private DeckContainer deckPane; // TODO give this it's own identp
	private GraveyardPane graveyardPane;
	private LifeCounter lifeCounter;

	/**
	 * Used by JSONCardReader to make every card have a uniqe id <br>
	 * Note that id's are only uniqe per player
	 */
	private int cardIdCounter;

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

		this.connection = connection;
		shouldSend = true;

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
		String deckString = Integer.toString( getDeckCards().size() );
		double deckX = Battlefield.WIDTH - Card.WIDTH - 10;
		double deckY = Battlefield.HEIGHT - Card.HEIGHT - 10;
		//deckPane = new DeckPane( new DeckPaneHandler(), deckString, Card.WIDTH, Card.HEIGHT, deckX, deckY );
		deckPane = new DeckContainer( new DeckPaneHandler(), deckString, Card.WIDTH + 50, Card.HEIGHT, deckX - 50, deckY );
		double graveX = Battlefield.WIDTH - Card.WIDTH - 10;
		double graveY = 10;
		graveyardPane = new GraveyardPane( Card.WIDTH, Card.HEIGHT, graveX, graveY );
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
		cardIdCounter = 0;
		deckCards        = new CardCollection( jCardReader, cardIdCounter, cardList );
		handCards        = new CardCollection();
		battlefieldCards = new CardCollection();
		graveyardCards   = new CardCollection();

		health = 20;
		poisonCounters = 0;

		shouldSend = false;

		// Objects displayed on the battlefield
		String deckString = Integer.toString( getDeckCards().size() );
		double deckX = Battlefield.WIDTH - Card.WIDTH - 10;
		double deckY = Battlefield.HEIGHT - Card.HEIGHT - 10;
		//deckPane = new DeckPane( new DeckPaneHandler(), deckString, Card.WIDTH, Card.HEIGHT, deckX, deckY );
		deckPane = new DeckContainer( new DeckPaneHandler(), deckString, Card.WIDTH + 50, Card.HEIGHT, deckX - 50, deckY );
		double graveX = Battlefield.WIDTH - Card.WIDTH - 10;
		double graveY = 10;
		graveyardPane = new GraveyardPane( Card.WIDTH, Card.HEIGHT, graveX, graveY );
		lifeCounter = new LifeCounter(new LifeCounterHandler());
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
	 * Sends the drawCard to player when the deck is pressed
	 */
	private class DeckPaneHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				drawCard();
			} catch (CardNotFoundException e) {
				System.out.println("Player " + this + " trying to draw cards with an empty deck");
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
			if( event.getSource() == playerBtnPane.getShuffleBtn() ) {
				handCards.shuffleCards();
				rearangeCards();
			}

			/*
			 * Places the cards in accending order,
			 * starting from the top left and going down and right
			 * Starts a new 'stack' after 'cardsPerRow' cards.
			 */
			int cardsPerRow = 12;
			if( event.getSource() == playerBtnPane.getResetBoardBtn() ) { 
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
		Card tempCard = deckCards.takeCard( cardId );
		tempCard.setCurrentLocation(Card.CardLocation.HAND);
		handCards.add(tempCard);

		if( shouldSend ) {
			System.out.println( "Sending " + tempCard.getCardId() );
			connection.sendPacket( new CardDrawPacket( tempCard.getCardId() ) );
		}

		tempCard.setTranslateY(tempCard.getHandPopupValue());
		double cardPlacement = Battlefield.WIDTH * 0.08125; // TODO this should probably be put somewhere nicer
		tempCard.setTranslateX( cardPlacement + ( handCards.size() - 1 ) * ( tempCard.getWidth() + tempCard.getPreferdMargin() * 2) );

		this.getChildren().add(tempCard);

		// Set the text on the graphical deck
		deckPane.updateText(Integer.toString(getDeckCards().size()));
	}

	/**
	 * Notify the lower classes about that everything has changed scale <br>
	 * Needed for card movement to scale it to the window size
	 * @param newScaleFactor what scale the new window is to the original
	 */
	public void updateScaleFactor( double newScaleFactor ) {
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
	 * Moves card to the table
	 * @param card the card to be move
	 * @param targetBattlefield the battlefield that the card should end up on
	 * currently this only really works if you use "matching" player and battlefield
	 */
	public void playCard(Card card, Battlefield targetBattlefield) {
		try {
			card.setCurrentLocation( Card.CardLocation.BATTLEFIELD );

			double finalPosY = 25;
			double startY = card.getTranslateY();

			/*
			 * Moves the card upwards from the hand to the battlefield
			 * Then jerk it back down only to have it moved by actually
			 * placing it on the target tbattlefield.
			 */
			this.getChildren().remove(card);
			targetBattlefield.getChildren().add(card);
			battlefieldCards.add(handCards.takeCard(card));

			card.setTranslateY( Battlefield.HEIGHT + startY );

			double moveDistance = Battlefield.HEIGHT - finalPosY + startY;
			TranslateTransition tt = new TranslateTransition( Duration.millis(500), card );
			tt.setByY( -moveDistance );

			if( shouldSend ) {
				connection.sendPacket( new CardPlayedPacket( card.getCardId(), card.getTranslateX(), card.getTranslateY() ) );
			}

			// Changes the hower action from "jump up" in hand
			// to "set focus" on the battlefield
			card.setOnMouseEntered( card.getMouseEventHandler() );

			tt.play();

			// Maybe play this on animation finnis
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
	 */
	public void moveCardBetweenCollection(
			Card whatCard,
			CardCollection oldCollection,
			CardCollection newCollection ) {
		try {
			newCollection.add(oldCollection.takeCard(whatCard));
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * These functions moves cards on the battlefield,
	 * TODO, Warn if a card that isn't on the battlefield is trying to be
	 * moved
	 */
	public void modifyCardTranslateX(Card whatCard, int xChange) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).modifyTranslateX(xChange);
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void modifyCardTranslateY(Card whatCard, int yChange) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).modifyTranslateY(yChange);
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void setCartTranslateX(Card whatCard, int xPos) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).setTranslateX(xPos);
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void setCartTranslateY(Card whatCard, int yPos) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).setTranslateY(yPos);
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void modifyCardRotate(Card whatCard, int rotation) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).modifyRotate(rotation);
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void setCardRotate(Card whatCard, int rotation) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).setRotate(rotation);
		} catch (CardNotFoundException e) {
			e.printStackTrace();
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
	 * @return the deckPane
	 */
	/*
	public DeckPane getDeckPane() {
		return deckPane;
	}
	*/
	public DeckContainer getDeckPane() {
		return deckPane;
	}
		


	/**
	 * @return the graveyardPane
	 */
	public GraveyardPane getGraveyardPane() {
		return graveyardPane;
	}

	/**
	 * @return the lifeCounter
	 */
	public LifeCounter getLifeCounter() {
		return lifeCounter;
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
