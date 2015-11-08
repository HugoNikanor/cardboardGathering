package gamePieces;

import database.JSONCardReader;

import exceptions.CardNotFoundException;

import graphicsObjects.DeckPane;
import graphicsObjects.GraveyardPane;
import graphicsObjects.LifeCounter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import network.Connection;

/**
 * A battlefield in rule design Also the graphical representation of the same
 */
public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	private DeckPane deckPane;
	private GraveyardPane graveyardPane;
	private LifeCounter lifeCounter;

	public static final double WIDTH = 1920;// 1600;
	public static final double HEIGHT = 474;// 395;

	private boolean isReady;

	/**
	 * This is for seting up the oponenets battlefield over the network
	 *
	 * @param jCardReader
	 *            Get's the card data from there
	 */
	public Battlefield(JSONCardReader jCardReader, String[] cardList) {
		// cardStream will be gotten over the network
		// Both players MUST have all database files
		// eventHandlers are exchanged for proper alternatives
		// Connection is not used

		player = new Player(jCardReader, cardList);
		cards = player.getBattlefieldCards();

		this.initialSetup();
		this.setRotate(180d);
	}

	/**
	 * This is for creating the local battlefield
	 *
	 * @param cardListFile
	 *            file which tells the program which cards are desired
	 * @param mouseEventHandler
	 *            sholud handle the card being played upon pressing it in the
	 *            hand
	 * @param connection
	 *            the connection to the server, used to send data
	 * @param jCardReader
	 *            Get's the card data from there
	 */
	public Battlefield( EventHandler<MouseEvent> mouseEventHandler, Connection connection, JSONCardReader jCardReader, String[] cardList ) {
		player = new Player( jCardReader, mouseEventHandler, connection, cardList );
		cards = player.getBattlefieldCards();

		this.initialSetup();
	}

	/**
	 * The parts of the constructor that are the same between the local and
	 * remote instance of the class
	 */
	private void initialSetup() {
		// JavaFX
		this.getStyleClass().add("battlefield");
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setMinHeight(HEIGHT);
		this.setMaxHeight(HEIGHT);
		this.setPrefSize(this.getWidth(), this.getHeight());
		this.setMinSize(this.getWidth(), this.getHeight());

		// Deck
		String deckString = Integer.toString(getPlayer().getDeckCards().size());
		deckPane = new DeckPane(deckString, Card.WIDTH, Card.HEIGHT, this.getWidth(), this.getHeight());
		deckPane.setOnMouseClicked(new DeckPaneHandler());
		this.getChildren().add(deckPane);

		// Graveyard
		graveyardPane = new GraveyardPane(Card.WIDTH, Card.HEIGHT, this.getWidth(), 10);
		this.getChildren().add(graveyardPane);

		// Life counter
		lifeCounter = new LifeCounter(new LifeCounterHandler());
		this.getChildren().add(lifeCounter);

		this.isReady = true;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the cards
	 */
	public CardCollection getCards() {
		return cards;
	}

	/**
	 * @return the deckPane
	 */
	public DeckPane getDeckPane() {
		return deckPane;
	}

	/**
	 * @return the lifeCounter
	 */
	public LifeCounter getLifeCounter() {
		return lifeCounter;
	}

	/**
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}

	/**
	 * Takes care of the button inputs on the lifecounter Updates the players
	 * stats and the counters display
	 */
	private class LifeCounterHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == lifeCounter.getHpUpBtn()) {
				getPlayer().changeHealth(1);
				lifeCounter.setHealthValue(getPlayer().getHealth());
			}
			if (event.getSource() == lifeCounter.getHpDownBtn()) {
				getPlayer().changeHealth(-1);
				lifeCounter.setHealthValue(getPlayer().getHealth());
			}
			if (event.getSource() == lifeCounter.getPoisonUpBtn()) {
				getPlayer().changePoison(1);
				lifeCounter.setPoisonValue(getPlayer().getPoison());
			}
			if (event.getSource() == lifeCounter.getPoisonDownBtn()) {
				getPlayer().changePoison(-1);
				lifeCounter.setPoisonValue(getPlayer().getPoison());
			}
			if (event.getSource() == lifeCounter.getResetBtn()) {
				getPlayer().setHealth(20);
				getPlayer().setPoison(0);

				lifeCounter.setHealthValue(getPlayer().getHealth());
				lifeCounter.setPoisonValue(getPlayer().getPoison());
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
				getPlayer().drawCard();
			} catch (CardNotFoundException e) {
				System.out.println("Player " + getPlayer() + " trying to draw cards with an empty deck");
			}
			deckPane.updateText(Integer.toString(getPlayer().getDeckCards().size()));
		}
	}

	/**
	 * Update which scale the window is in Used by card to know how far to move
	 *
	 * @param newScaleFactor
	 *            the scalefactor that should be used from this point on
	 */
	public void updateScaleFactor(double newScaleFactor) {
		player.updateScaleFactor(newScaleFactor);
	}

}
