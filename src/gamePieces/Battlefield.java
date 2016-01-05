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
	 * @param cardList the name of the cards that should be created
	 */
	public Battlefield(JSONCardReader jCardReader, String[] cardList) {
		// Both players MUST have all database files
		player = new Player(jCardReader, cardList);
		cards = player.getBattlefieldCards();

		this.initialSetup();
		this.setRotate(180d);
	}

	/**
	 * This is for creating the local battlefield
	 *
	 * @param cardPlayHandler
	 *            sholud handle the card being played upon pressing it in the
	 *            hand
	 * @param connection
	 *            the connection to the server, used to send data
	 * @param jCardReader
	 *            Get's the card data from there
	 * @param cardList
	 *            String[] with all card names to use in it
	 */
	public Battlefield( EventHandler<MouseEvent> cardPlayHandler, Connection connection, JSONCardReader jCardReader, String[] cardList ) {

		player = new Player( jCardReader, cardPlayHandler, connection, cardList );
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

		// token container
		this.getChildren().add( player.getTokenContainer() );

		// Deck
		this.getChildren().add( player.getDeckCont() );

		// Graveyard
		this.getChildren().add( player.getGraveCont() );

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
