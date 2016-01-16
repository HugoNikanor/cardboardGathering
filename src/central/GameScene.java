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

import gamePieces.Battlefield;
import gamePieces.Card;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import network.ConnectionPool;
import network.InputObjectHandler;

import serverPackets.CardListPacket;

public class GameScene {
	private Battlefield ownBattlefield;
	private Battlefield otherBattlefield;

	private BorderPane rootGamePane;

	//private double defaultSceneWidth;
	private double defaultSceneHeight;
	private Scale scale;
	private double scaleFactor;

	private InputObjectHandler inputObjectHandler;
	private JSONCardReader jCardReader;

	public GameScene( Stage stage, String ip, int port, Path deckFilepath ) {

		jCardReader = new JSONCardReader();
		inputObjectHandler = new InputObjectHandler( jCardReader );
		ConnectionPool.setDefaultIp( ip );
		ConnectionPool.setDefaultPort( port );
		ConnectionPool.setDefaultHandler( inputObjectHandler );

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

			ConnectionPool.getConnection().sendPacket( new CardListPacket( cardList ) );
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ownBattlefield = new Battlefield( jCardReader, cardList, true );

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
		// removing this crashes the program...?
		for( Card ownTemp : ownBattlefield.getCards() ) {
			ownBattlefield.getChildren().add(ownTemp);
		}
		for( Card otherTemp : otherBattlefield.getCards() ) {
			otherBattlefield.getChildren().add(otherTemp);
		}

		/*
		// Give a proper card focus
		// TODO this should probably just be removed
		try {
			ownBattlefield.getCards().getCard(0).giveFocus();
		} catch (CardNotFoundException e1) {
			System.out.println("Trying to give focus to card on the battlefild, but there are no cards there");
		}
		*/

		// contain the ownBattlefield & and otherBattlefield
		GridPane battlefieldContainer = new GridPane();
		battlefieldContainer.add(otherBattlefield, 0, 0);
		battlefieldContainer.add(ownBattlefield,   0, 1);
		otherBattlefield.toFront();

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

		Settings settings = new Settings( "settings/settings.properties" );
		String autoRescaleStr = settings.getProperty( "autoRescale", "true" );
		boolean autoRescale;
		if( autoRescaleStr.equalsIgnoreCase( "true" ) )
			autoRescale = true;
		else
			autoRescale = false;

		double manualScale = Double.parseDouble(settings.getProperty( "manualScale", "1.0" ));


		if( autoRescale ) {
			WindowSizeListener windowSizeListener = new WindowSizeListener( gameScene );
			stage.widthProperty().addListener(windowSizeListener);
			stage.heightProperty().addListener(windowSizeListener);
		} else {
			scale.setX( manualScale );
			scale.setY( manualScale );
		}
		
		ownBattlefield.updateScaleFactor(0.75);

		stage.setScene(gameScene);
		stage.show();

		defaultSceneHeight = gameScene.getHeight();

		gameScene.setOnKeyPressed(e -> {
			if( e.getCode() == KeyCode.F11 ) {
				Platform.runLater( new Thread(() -> {
					stage.setFullScreen( true );
				}) );
			}
		});
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

}
