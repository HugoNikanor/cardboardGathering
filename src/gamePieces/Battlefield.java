package gamePieces;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import graphicsObjects.DeckPane;
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
	private LifeCounter lifeCounter;

	public static final double WIDTH = 1920;//1600;
	public static final double HEIGHT = 474;//395;

	private String cardListFile;
	private Stream<String> cardStream;

	public Battlefield() {
	}

	public Battlefield(String cardListFile, EventHandler<MouseEvent> mouseEventHandler, Connection connection) {
		//System.out.println("Debug: start of Battlefield");
		
		this.cardListFile = cardListFile;
		try {
			cardStream = this.setupCardStream();
		} catch (IOException e) {
			System.out.println( "No file with that name" );
			e.printStackTrace();
		}

		player = new Player( cardStream, mouseEventHandler, connection );
		cards = player.getBattlefieldCards();

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

		//System.out.println("Debug: end of Battlefield");
	}

	public Stream<String> getStream() {
		return cardStream;
	}
	private Stream<String> setupCardStream() throws IOException {
		Path filepath = Paths.get( "decks/" + cardListFile );

		@SuppressWarnings("resource")
		Stream<String> cardStream = Files.lines(filepath, StandardCharsets.UTF_8);

		// DO NOT HAVE LEADING WHITESPACE!
		cardStream = cardStream
			.filter( u -> u.charAt(0) != '#' ) // '#' for comment
			.sorted();                         // Alphabetical

		return cardStream;
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
