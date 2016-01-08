package central;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import database.JSONCardReader;

import exceptions.CardNotFoundException;

import gamePieces.Battlefield;
import gamePieces.Card;
import gamePieces.CardCollection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import network.Connection;
import network.InputObjectHandler;

import serverPackets.CardListPacket;

public class GameScene {
	// Battlefields
	private Battlefield ownBattlefield;
	// This is the battlefield aquired over the network
	private Battlefield otherBattlefield;

	// Keyboard
	//private KeyEventHandler keyEventHandler;
	//private ArrayList<KeyCode> pressedKeys;

	// JavaFX Objects
	//private Stage primaryStage;
	//private Scene gameScene;
	private BorderPane rootGamePane;

	// JavaFX variables
	//private double defaultSceneWidth;
	private double defaultSceneHeight;
	private Scale scale;
	private double scaleFactor;
	//private boolean isFullscreen;

	// Network
	private Connection connection;
	private InputObjectHandler inputObjectHandler;

	private JSONCardReader jCardReader;

	//private int port;
	//private String ipAddr;
	//private Path deckFilepath;
	//private boolean showTitle;

	//private Settings settings;
	public GameScene( Stage stage, String ip, int port, Path deckFilepath ) {

		// Initiates the eventHandlers
		//keyEventHandler = new KeyEventHandler();
		//pressedKeys = new ArrayList<KeyCode>();

		CardPlayHandler cardPlayHandler = new CardPlayHandler();

		jCardReader = new JSONCardReader();
		inputObjectHandler = new InputObjectHandler( jCardReader );

		/*
		Properties prop = new Properties();
		try {
			FileInputStream propInStream = new FileInputStream("settings/settings.properties");
			prop.load(propInStream);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
		String cardListFile = prop.getProperty("cardlist", "defCardList");
		*/

		/*
		settings = new Settings( "settings/settings.properties" );

		// WARNING, this can maybe fail
		ipAddr = settings.getProperty("defIpAddress", "127.0.0.1" );
		port = Integer.parseInt(settings.getProperty( "defPort", "23732" ));

		connection = new Connection( inputObjectHandler, ipAddr, port );

		deckFilepath = Paths.get(
		               settings.getProperty( "defCardDir", "decks/" ) +
		               settings.getProperty( "defCardList", "defCardList" ));

	    */
		connection = new Connection( inputObjectHandler, ip, port );
		//Path deckFilepath = Paths.get( deckFilepath );
		String[] cardList = {};
		try {
			System.out.println( deckFilepath );
			// DO NOT HAVE LEADING WHITESPACE!
			Stream<String> cardStream = Files.lines(deckFilepath, StandardCharsets.UTF_8);
			cardList = cardStream
				.filter( u -> u.charAt(0) != '#' ) // '#' for comment
				.sorted()                          // Alphabetical
				.toArray(String[]::new);           // Make it an array
			cardStream.close();

			connection.sendPacket( new CardListPacket( cardList ) );
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ownBattlefield = new Battlefield( cardPlayHandler, connection, jCardReader, cardList );


		// Waits for the other battlefield to get ready
		while( otherBattlefield == null ) {
			try {
				otherBattlefield = inputObjectHandler.getBattlefield();
			} catch( NullPointerException npe ) {
				System.out.println( "Other battlefield not ready yet..." );
				System.out.println( "Wainting for the other client..." );
				try {
					Thread.sleep( 2000 );
				} catch( InterruptedException ie ) {
					ie.printStackTrace();
				}
			} 
		}

		// Adds the initial cards to the graphical display
		for( Card ownTemp : ownBattlefield.getCards() ) {
			ownBattlefield.getChildren().add(ownTemp);
		}
		for( Card otherTemp : otherBattlefield.getCards() ) {
			otherBattlefield.getChildren().add(otherTemp);
		}

		// Give a proper card focus
		// TODO this should probably just be removed
		try {
			ownBattlefield.getCards().getCard(0).giveFocus();
		} catch (CardNotFoundException e1) {
			System.out.println("Trying to give focus to card on the battlefild, but there are no cards there");
		}

		// contain the ownBattlefield & and otherBattlefield
		GridPane battlefieldContainer = new GridPane();
		battlefieldContainer.add(otherBattlefield, 0, 0);
		battlefieldContainer.add(ownBattlefield,   0, 1);

		// The pane everything ingame should be placed in
		// Change this for "out of game" menus & simmilar
		rootGamePane = new BorderPane();
		rootGamePane.setCenter(battlefieldContainer);
		rootGamePane.setBottom(ownBattlefield.getPlayer());

		// Set scale, for windown resize
		scale = new Scale();
		scale.setPivotX(0);
		scale.setPivotY(0);
		rootGamePane.getTransforms().add(scale);

		// Scene
		// Only one at a time, can change
		Scene gameScene = new Scene(rootGamePane);
		//gameScene.setOnKeyPressed(keyEventHandler);
		//gameScene.setOnKeyReleased(keyEventHandler);


		// ALL THE GRAPHICS OBJECTS SHOULD NOW BE INITIATED
		// Loads the stylesheet, in a not to pretty way
		File styleFile = new File("stylesheets/stylesheet.css");
		String styleFilePath = "file:///" + styleFile.getAbsolutePath().replace("\\", "/");
		gameScene.getStylesheets().add(styleFilePath);

		//new Thread(new KeyHandleThread()).start();

		WindowSizeListener windowSizeListener = new WindowSizeListener( gameScene );
		stage.widthProperty().addListener(windowSizeListener);
		stage.heightProperty().addListener(windowSizeListener);

		stage.setScene(gameScene);
		stage.show();

		defaultSceneHeight = gameScene.getHeight();
	}

	/**
	 * Listens for window resizing,
	 * If a resize is detected, the graphics will scale to match
	 *
	 * TODO The cards on hand have a tendancy to dissapear when making the 
	 * window larger
	 *
	 * TODO The graphics only resize when the window is changed in the 'y'
	 * direction
	 *
	 * TODO this doesn't work correctly on my Windows 8 laptop
	 */ 
	private class WindowSizeListener implements ChangeListener<Number> {
		final Timer timer = new Timer();
		TimerTask task = null;
		final long delayTime = 200;

		Scene scene;

		public WindowSizeListener( Scene sceneToTrack ) {
			this.scene = sceneToTrack;
		}

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
			if( task != null ) {
				task.cancel();
			}

			task = new TimerTask() {
				@Override
				public void run() {
					scaleFactor = (scene.getHeight() / defaultSceneHeight);
					//System.out.println( "sf GL: " + scaleFactor);
					
					scale.setX(scaleFactor);
					scale.setY(scaleFactor);
					
					ownBattlefield.updateScaleFactor(scaleFactor);
				}
			};
			timer.schedule(task, delayTime);
		}
	}

	/**
	 * EventHandler for cards being played, <br>
	 *
	 * This Handler is applied to the cards and is used when they are pressed
	 * and in a players hand. <br>
	 *
	 * TODO check if this can be placed further down the project
	 */
	private class CardPlayHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				Card tempCard = ownBattlefield
					.getPlayer()
					.getHandCards()
					.getCard(
						(Card)event.getSource());

				if( tempCard.getCurrentLocation() == CardCollection.Collections.HAND ) {

					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						ownBattlefield.getPlayer().playCard(tempCard, ownBattlefield);
					}

				}
			} catch (CardNotFoundException e) {
				// This will fail when using a card on the battlefield.
				// Be aware that it's the reason for not doing anything here
			}
		}
	}
}
