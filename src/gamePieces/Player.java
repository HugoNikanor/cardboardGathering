package gamePieces;

import exceptions.CardNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Player extends Pane {
	private CardCollection deckCards;
	private CardCollection handCards;
	private CardCollection battlefieldCards;
	private CardCollection graveyardCards;

	private int health;
	private int poisonCounters;

	private MouseEventHandler mouseEventHandler;

	public Player(String cardList, EventHandler<MouseEvent> cardPlayHandler) {
		//System.out.println("Debug: start of Player");
		deckCards        = new CardCollection(cardList);
		handCards        = new CardCollection();
		battlefieldCards = new CardCollection();
		graveyardCards   = new CardCollection();

		mouseEventHandler = new MouseEventHandler();

		for( Card temp : deckCards ) {
			temp.setOnMouseEntered( mouseEventHandler );
			temp.setOnMouseExited ( mouseEventHandler );

			temp.setOnMouseClicked( cardPlayHandler );
		}
		try {
			System.out.println(deckCards.getCard(0).getOnMouseClicked());
		} catch (CardNotFoundException e1) {
			e1.printStackTrace();
		}

		health = 20;
		poisonCounters = 0;

		try {
			//Draws the starting hand
			for( int i = 0; i < 7; i++ ) {
				this.drawCard();
			}
		} catch (CardNotFoundException e) {
			// This should trigger a player lost condition
			System.out.println(
				"==\n" +
				"Player (" +
				this.hashCode() + 
				"): trying to draw card with no cards left in deck\n" +
				"=="
			);
			//e.printStackTrace();
		}
		try {
			this.playCard(handCards.getNextCard());
			this.playCard(handCards.getNextCard());
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//===============================//
		//         JavaFX below          //
		//===============================//

		this.setPrefSize(Battlefield.WIDTH, 110);
		this.getStyleClass().add("cards-in-hand-pane");

		//System.out.println("Debug: end of Player");
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			//event.getSource() gives the card that is selected
			Card tempCard;
			try {
				tempCard = handCards.getCard(handCards.indexOf(event.getSource()));
				if( tempCard.getCurrentLocation() == Card.HAND ) {
					if( event.getEventType() == MouseEvent.MOUSE_ENTERED ) {
						tempCard.setTranslateY(-20);
					}
					if( event.getEventType() == MouseEvent.MOUSE_EXITED ) {
						tempCard.setTranslateY(20);
					}
				}
			} catch (CardNotFoundException e1) {
				// TODO This is triggered due to the card keeping this listener when it enters the battlefield
				// TODO A problem might very well be here, due to no error reporting
				// System.out.println("hover over card in battlefield");
			}
		}
	}

	/**
	 * Moves a card from the deck to the hand
	 * @throws CardNotFoundException
	 */
	public void drawCard() throws CardNotFoundException {
		Card tempCard = deckCards.takeNextCard();
		tempCard.setCurrentLocation(Card.HAND);
		handCards.add(tempCard);
	}

	/**
	 * Moves card to the table
	 */
	public void playCard(Card whatCard) {
		try {
			System.out.println("Card played rule");
			whatCard.setCurrentLocation(Card.BATTLEFIELD);
			battlefieldCards.add(handCards.takeCard(whatCard));
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
	}

	// This doesn't the cards status of where it is
	// This should be looked into
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

	/**
	 * These functions moves cards on the battlefield,
	 * TODO, Warn if a card that isn't on the battlefield is trying to be
	 * moved
	 */
	public void moveCardX(Card whatCard, int xChange) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).modifyTranslateX(xChange);
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void moveCardY(Card whatCard, int yChange) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).modifyTranslateY(yChange);
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void placeCardX(Card whatCard, int xPos) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).setTranslateX(xPos);
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void placeCardY(Card whatCard, int yPos) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).setTranslateY(yPos);
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rotateCard(Card whatCard, int rotation) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).modifyRotate(rotation);
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setCardRotation(Card whatCard, int rotation) {
		try {
			battlefieldCards.getCard(battlefieldCards.indexOf(whatCard)).setRotate(rotation);
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateScaleFactorX(double newScaleFactorX) {
		deckCards.updateScaleFactorX(newScaleFactorX);
		handCards.updateScaleFactorX(newScaleFactorX);
		battlefieldCards.updateScaleFactorX(newScaleFactorX);
		graveyardCards.updateScaleFactorX(newScaleFactorX);
	}

	public void updateScaleFactorY(double newScaleFactorY) {
		deckCards.updateScaleFactorY(newScaleFactorY);
		handCards.updateScaleFactorY(newScaleFactorY);
		battlefieldCards.updateScaleFactorY(newScaleFactorY);
		graveyardCards.updateScaleFactorY(newScaleFactorY);
	}

	/**
	 * Change 'Health' and 'poison count'
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	public void changeHealth(int change) {
		this.health += change;
	}

	public void setPoisonCounters(int poisonCounters) {
		this.poisonCounters = poisonCounters;
	}
	
	public void changePoisonCounters(int change) {
		this.poisonCounters += change;
	}

	/**
	 ********************************************
	 * There be nothing but Getters bellow,
	 * I think...
	 ********************************************
	 */

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
	public int getPoisonCounters() {
		return poisonCounters;
	}

}
