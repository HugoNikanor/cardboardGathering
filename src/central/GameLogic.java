package central;

import java.io.File;
import java.util.ArrayList;

import exceptions.CardNotFoundException;

import gamePieces.Battlefield;
import gamePieces.Card;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class GameLogic extends Application {

	private Battlefield ownBattlefield;
	// This is the battlefield aquired over the network
	private Battlefield otherBattlefield;

	private HBox cardsInHandPane;

	private String styleFilePath;

	//private Network network;
	private NetworkThread networkThread;
	private KeyHandleThread keyHandleThread;

	private KeyEventHandler keyEventHandler;

	private ArrayList<KeyCode> pressedKeys;

	// used by the keylistener to know which card to move
	private int currentCard;

	private Stage primaryStage;


	public GameLogic() {
		// Loads the stylesheet, in a not to pretty way
		File styleFile = new File("stylesheets/stylesheet.css");
		styleFilePath = "file:///" + styleFile.getAbsolutePath().replace("\\", "/");

		// Initiates the eventHandlers
		keyEventHandler    = new KeyEventHandler();

		pressedKeys = new ArrayList<KeyCode>();


		ownBattlefield   = new Battlefield("cardList");
		otherBattlefield = new Battlefield("cardlist");
		otherBattlefield.setRotate(180d);

		// Pane for the cards in your hand
		// TODO This should possibly get its own class 
		cardsInHandPane = new HBox();
		cardsInHandPane.setPrefSize(400, 100);
		cardsInHandPane.getStyleClass().add("cards-in-hand-pane");

		// TODO testline
		ownBattlefield.setId("test-id");

		Platform.setImplicitExit(false);

		try {
			keyHandleThread = new KeyHandleThread();
			new Thread(keyHandleThread).start();
		} catch( Exception e ) {
			e.printStackTrace();
		}

		networkThread = new NetworkThread();
		new Thread(networkThread).start();
	}

	@Override
	public void start(Stage inStage) throws Exception {
		/**
		 * Makes the Stage avalible to the whole class,
		 * Used to be able to close the window via an event.
		 */
		primaryStage = inStage;

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent event ) {
				Platform.exit();
				System.exit(0);
			}
		});

		// contain the ownBattlefield & and otherBattlefield
		GridPane battlefieldContainer = new GridPane();
		battlefieldContainer.add(otherBattlefield, 0, 0);
		battlefieldContainer.add(ownBattlefield,   0, 1);


		// The pane everything ingame should be placed in
		// Change this for "out of game" menus & simmilar
		BorderPane rootGamePane = new BorderPane();
		rootGamePane.setCenter(battlefieldContainer);
		rootGamePane.setBottom(cardsInHandPane);

		// Scene
		// Only one at a time, can change
		Scene gameScene = new Scene(rootGamePane);
		gameScene.setOnKeyPressed(keyEventHandler);
		gameScene.setOnKeyReleased(keyEventHandler);

		// Applies the stylesheet
		gameScene.getStylesheets().add(styleFilePath);

		// Stage
		// This never changes
		primaryStage.setTitle("cardboardGathering");
		primaryStage.setScene(gameScene);
		primaryStage.show();
	}

	public class KeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			if( event.getEventType() == KeyEvent.KEY_PRESSED) {
				if( !(pressedKeys.contains(event.getCode())) ) {
					pressedKeys.add(event.getCode());
					System.out.println("added: " + event.getCode());
				}
			}
			if( event.getEventType() == KeyEvent.KEY_RELEASED) {
				pressedKeys.remove(event.getCode());
				System.out.println("removed: " + event.getCode());
			}
		}
	}

	private class KeyHandleThread implements Runnable {
		boolean spacePressedBefore;
		Card tempCard;

		@Override
		public void run() {
			synchronized( this ) {
				System.out.println("key thread started");
				while( true ) {
					// C-m to close the window
					if(pressedKeys.contains(KeyCode.CONTROL) &&
					   pressedKeys.contains(KeyCode.M) ) {
						Platform.exit();
						System.exit(0);
					}
					try {
						tempCard = ownBattlefield.getCards().getCard(currentCard);
					} catch (CardNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						tempCard = new Card();
					}

					TranslateTransition tt;
					if( pressedKeys.contains(KeyCode.DOWN) ||
					    pressedKeys.contains(KeyCode.J) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );
						tt.setByY( 12f );
						tt.play();
					}
					if( pressedKeys.contains(KeyCode.UP) ||
					    pressedKeys.contains(KeyCode.K) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );
						tt.setByY( -12f );
						tt.play();
					}
					if( pressedKeys.contains(KeyCode.RIGHT) ||
					    pressedKeys.contains(KeyCode.L) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );
						tt.setByX( 12f );
						tt.play();
					}
					if( pressedKeys.contains(KeyCode.LEFT) ||
					    pressedKeys.contains(KeyCode.H) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );
						tt.setByX( -12f );
						tt.play();
					}
					// Rotate the card when space is released
					if( !pressedKeys.contains(KeyCode.SPACE) &&
						spacePressedBefore ) {
						System.out.println("space is down");
						tempCard.smoothRotate(90d);
					}
					spacePressedBefore = pressedKeys.contains(KeyCode.SPACE);

					if( pressedKeys.contains(KeyCode.TAB) ) {
						if( pressedKeys.contains(KeyCode.SHIFT) ) {
							currentCard--;
							if( currentCard < 0 ) {
								currentCard = ownBattlefield.getCards().size();
							}
						}
						currentCard++;
						if( currentCard >= ownBattlefield.getCards().size() ) {
							currentCard = 0;
						}
					}

					try {
						this.wait(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class NetworkThread implements Runnable {
		@Override
		public void run() {
			/**
			 * TODO
			 * Ask the network class if anything has happenend,
			 * and if it has, tell 'GameLogic'
			 */
			System.out.println("Network thread, reporting for duty.");
		}
	}
}
