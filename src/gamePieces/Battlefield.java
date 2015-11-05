package gamePieces;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import graphicsObjects.DeckPane;
import graphicsObjects.LifeCounter;

import inputObjects.CardListObject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import network.Connection;
import network.InputObjectHandler;

public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	private DeckPane deckPane;
	private LifeCounter lifeCounter;

	public static final double WIDTH = 1920;//1600;
	public static final double HEIGHT = 474;//395;

	private String cardListFile;
	private Connection connection;
	//private Stream<String> cardStream;

	private boolean isReady;

	/**
	 * This is for seting up the oponenets battlefield over the network
	 */
	public Battlefield( InputObjectHandler inputObjectHandler ) throws ClassNotFoundException {
		// cardStream will be gotten over the network
		// Both players MUST have all database files
		// eventHandlers are exchanged for proper alternatives
		// Connection is not used

		int tryCounter = 0;
		Stream<String> cardStream;
		/*
		while( true ) {
			cardStream = inputObjectHandler.getLatestCardList().getStream();
			if( cardStream != null ) 
				break;

			if( tryCounter++ > 10 ) {
				// TODO maybe have another exception type
				throw new ClassNotFoundException( "Couldn't get cardStream from server" );
			}

			try {
				System.out.println( "Couldn't get the cardStream from the network," );
				System.out.println( "Trying again" );
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		try {
			this.cardListFile = "cardlist1";
			cardStream = this.setupCardStream();
			player = new Player( cardStream );
			cards = player.getBattlefieldCards();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		this.initialSetup();
		this.setRotate(180d);
	}

	/**
	 * This is for creating the local battlefield
	 */
	public Battlefield(String cardListFile, EventHandler<MouseEvent> mouseEventHandler, Connection connection) {
		//System.out.println("Debug: start of Battlefield");

		this.connection = connection;
		
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

		// Life counter
		lifeCounter = new LifeCounter( new LifeCounterHandler() );
		this.getChildren().add( lifeCounter );

		this.isReady = true;
	}

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
				lifeCounter.setLifeValue(getPlayer().getHealth());
			}
			if( event.getSource() == lifeCounter.getHpDownBtn() ) {
				getPlayer().changeHealth( -1 );
				lifeCounter.setLifeValue(getPlayer().getHealth());
			}
			if( event.getSource() == lifeCounter.getPoisonUpBtn() ) {
				getPlayer().changePoisonCounters( 1 );
				lifeCounter.setPoisonValue(getPlayer().getPoisonCounters());
			}
			if( event.getSource() == lifeCounter.getPoisonDownBtn() ) {
				getPlayer().changePoisonCounters( -1 );
				lifeCounter.setPoisonValue(getPlayer().getPoisonCounters());
			}
			if( event.getSource() == lifeCounter.getResetBtn() ) {
				getPlayer().setHealth( 20 );
				getPlayer().setPoisonCounters( 0 );

				lifeCounter.setLifeValue(getPlayer().getHealth());
				lifeCounter.setPoisonValue(getPlayer().getPoisonCounters());
			}
		}
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			getPlayer().drawCard();
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
