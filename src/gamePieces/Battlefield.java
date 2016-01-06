package gamePieces;

import database.JSONCardReader;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import network.Connection;

/**
 * A battlefield in rule design Also the graphical representation of the same
 */
public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	/**
	 * Here so that all cards can have a set height in relation to everything else.
	 */
	private Pane cardLayer;

	private BorderPane upperPane;
	private BorderPane lowerPane;

	private static final double WIDTH = 1920;// 1600;
	private static final double HEIGHT = 474;// 395;

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

		this.initialSetup( false );
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

		player = new Player( WIDTH, jCardReader, cardPlayHandler, connection, cardList );
		cards = player.getBattlefieldCards();

		this.initialSetup( true );
	}

	/**
	 * The parts of the constructor that are the same between the local and
	 * remote instance of the class
	 */
	private void initialSetup( boolean isLocal ) {
		// JavaFX
		this.getStyleClass().add("battlefield");
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setMinHeight(HEIGHT);
		this.setMaxHeight(HEIGHT);
		this.setPrefSize(this.getWidth(), this.getHeight());
		this.setMinSize(this.getWidth(), this.getHeight());

		cardLayer = new Pane();
		cardLayer.setPickOnBounds( false );
		cardLayer.setPrefSize(this.getWidth(), this.getHeight());
		//cardLayer.setMinSize(this.getWidth(), this.getHeight());

		upperPane = new BorderPane();
		upperPane.setPickOnBounds( false );
		upperPane.setPrefSize( this.getWidth(), this.getHeight() );

		lowerPane = new BorderPane();
		lowerPane.setPickOnBounds( false );
		lowerPane.setPrefSize( this.getWidth(), this.getHeight() );


		VBox cardStackContainerContainer = new VBox( 30 );
		cardStackContainerContainer.setFillWidth( false );
		cardStackContainerContainer.setAlignment( Pos.CENTER );
		cardStackContainerContainer.setPadding( new Insets(20) );
		lowerPane.setRight( cardStackContainerContainer );

		

		this.getChildren().addAll( cardLayer, upperPane, lowerPane );

		upperPane.toFront();
		lowerPane.toBack();

		// Add a toBack if that's desired, else it goes in front of the cards
		// It's preferable if noone puts two objects on top of each other

		// token container & chat
		if( isLocal ) {
			this.getChildren().addAll( 
					player.getTokenContainer(), player.getChatContainer() );
		}

		// Deck & Graveyard
		cardStackContainerContainer.getChildren().addAll( 
				player.getGraveCont(), player.getDeckCont() );

		// Life counter
		this.getChildren().add( player.getLifeCounter() );

		// used when sending the battlefield to the other player
		if( isLocal )
			this.isReady = true;

		if( !isLocal )
			this.setRotate( 180d );
	}

	/**
	 * Place a card on the graphical battlefield
	 */
	public void playCard( Card card ) {
		this.cardLayer.getChildren().add( card );
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
