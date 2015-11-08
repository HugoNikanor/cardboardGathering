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

public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	private DeckPane deckPane;
	private GraveyardPane graveyardPane;
	private LifeCounter lifeCounter;

	public static final double WIDTH = 1920;//1600;
	public static final double HEIGHT = 474;//395;

	//private String cardListFile;
	//private Connection connection;
	//private Stream<String> cardStream;

	private boolean isReady;

	/**
	 * This is for seting up the oponenets battlefield over the network
	 */
	public Battlefield( JSONCardReader jCardReader ) throws ClassNotFoundException {
		// cardStream will be gotten over the network
		// Both players MUST have all database files
		// eventHandlers are exchanged for proper alternatives
		// Connection is not used

		player = new Player( jCardReader );
		cards = player.getBattlefieldCards();


		this.initialSetup();
		this.setRotate(180d);
	}

	/**
	 * This is for creating the local battlefield
	 */
	public Battlefield(String cardListFile, EventHandler<MouseEvent> mouseEventHandler , Connection connection, JSONCardReader jCardReader ) {
		//System.out.println("Debug: start of Battlefield");

		//this.connection = connection;
		
		/*
		this.cardListFile = cardListFile;
		Stream<String> cardStream;
		try {
			cardStream = this.setupCardStream();
			player = new Player( cardStream, mouseEventHandler, connection );
			cards = player.getBattlefieldCards();
		} catch (IOException e) {
			System.out.println( "No file with that name" );
			e.printStackTrace();
		}
		*/
		player = new Player( jCardReader, mouseEventHandler, connection );
		cards = player.getBattlefieldCards();

		this.initialSetup();

		//System.out.println("Debug: end of Battlefield");
	}

	/**
	 * The parts of the constructor that are the same between the local and
	 * remote instance of the class
	 */
	private void initialSetup() {
		// JavaFX
		this.getStyleClass().add( "battlefield" );
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setMinHeight(HEIGHT);
		this.setMaxHeight(HEIGHT);
		this.setPrefSize( this.getWidth(), this.getHeight() );
		this.setMinSize(  this.getWidth(), this.getHeight() );

		// Deck 
		String deckString = Integer.toString(getPlayer().getDeckCards().size());
		deckPane = new DeckPane( deckString, Card.WIDTH, Card.HEIGHT, this.getWidth(), this.getHeight() );
		deckPane.setOnMouseClicked( new MouseEventHandler() );
		this.getChildren().add( deckPane );

		// Graveyard
		graveyardPane = new GraveyardPane( Card.WIDTH, Card.HEIGHT, this.getWidth(), 10 ); 
		this.getChildren().add( graveyardPane );

		// Life counter
		lifeCounter = new LifeCounter( new LifeCounterHandler() );
		this.getChildren().add( lifeCounter );

		this.isReady = true;

		// Debug
		System.out.println(" === CARDS === ");
		for( Card tempCard : player.getDeckCards() ) {
			System.out.println( tempCard.getCardId() + ": " + tempCard.getCardName() );
		}
		System.out.println(" ============= ");
	}

	// This is moved further down the tree
	/*
	private Stream<String> setupCardStream() throws IOException {
		Path filepath = Paths.get( "decks/" + cardListFile );

		@SuppressWarnings("resource")
		Stream<String> cardStream = Files.lines(filepath, StandardCharsets.UTF_8);

		// DO NOT HAVE LEADING WHITESPACE!
		cardStream = cardStream
			.filter( u -> u.charAt(0) != '#' ) // '#' for comment
			.sorted();                         // Alphabetical

		//connection.sendPacket( new CardListObject(cardStream) );
		return cardStream;
	}
	*/

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

	private class LifeCounterHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if( event.getSource() == lifeCounter.getHpUpBtn() ) {
				getPlayer().changeHealth( 1 );
				lifeCounter.setHealthValue(getPlayer().getHealth());
			}
			if( event.getSource() == lifeCounter.getHpDownBtn() ) {
				getPlayer().changeHealth( -1 );
				lifeCounter.setHealthValue(getPlayer().getHealth());
			}
			if( event.getSource() == lifeCounter.getPoisonUpBtn() ) {
				getPlayer().changePoison( 1 );
				lifeCounter.setPoisonValue(getPlayer().getPoison());
			}
			if( event.getSource() == lifeCounter.getPoisonDownBtn() ) {
				getPlayer().changePoison( -1 );
				lifeCounter.setPoisonValue(getPlayer().getPoison());
			}
			if( event.getSource() == lifeCounter.getResetBtn() ) {
				getPlayer().setHealth( 20 );
				getPlayer().setPoison( 0 );

				lifeCounter.setHealthValue(getPlayer().getHealth());
				lifeCounter.setPoisonValue(getPlayer().getPoison());
			}
		}
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				getPlayer().drawCard();
			} catch( CardNotFoundException e ) {
				System.out.println( "Player " + getPlayer() + " trying to draw cards with an empty deck" );
			}
			deckPane.updateText(Integer.toString(getPlayer().getDeckCards().size()));
		}
	}

	public void updateScaleFactor( double newScaleFactor ) {
		player.updateScaleFactor( newScaleFactor );
	}

	public Player getPlayer() {
		return player;
	}

	public CardCollection getCards() {
		return cards;
	}

}
