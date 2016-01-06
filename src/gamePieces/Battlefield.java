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
		player = new Player( jCardReader, cardList );
		cards = player.getBattlefieldCards();

		this.initialSetup( "remote" );
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

		this.initialSetup( "local" );
	}

	/**
	 * The parts of the constructor that are the same between the local and
	 * remote instance of the class
	 */
	private void initialSetup( String localOrRemote ) {
		// JavaFX
		this.getStyleClass().add("battlefield");
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setMinHeight(HEIGHT);
		this.setMaxHeight(HEIGHT);
		this.setPrefSize(this.getWidth(), this.getHeight());
		this.setMinSize(this.getWidth(), this.getHeight());

		// TODO maybe many of these should have one local and one remote version, 
		// allowing some buttons to be omitted or added

		// token container
		if( localOrRemote.equals( "local" ) )
			this.getChildren().add( player.getTokenContainer() );

		// Deck
		this.getChildren().add( player.getDeckCont() );

		// Graveyard
		this.getChildren().add( player.getGraveCont() );

		// Life counter
		this.getChildren().add( player.getLifeCounter() );
		//if( localOrRemote.equals( "remote" ) )
			//player.getLifeCounter().setRotate( 180d );


		// used when sending the battlefield to the other player
		if( localOrRemote.equals( "local" ) )
			this.isReady = true;

		if( localOrRemote.equals( "remote" ) )
			this.setRotate( 180d );
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
