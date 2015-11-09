package gamePieces;

import database.JSONCardReader;

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
		this.getChildren().add( player.getDeckPane() );

		// Graveyard
		this.getChildren().add( player.getGraveyardPane() );

		// Life counter
		this.getChildren().add( player.getLifeCounter() );

		// used when sending the battlefield to the other player
		this.isReady = true;
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
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}


}
