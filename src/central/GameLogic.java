package central;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import exceptions.CardNotFoundException;

import gamePieces.Battlefield;
import gamePieces.Card;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class GameLogic extends Application {

	private Battlefield ownBattlefield;
	// This is the battlefield aquired over the network
	private Battlefield otherBattlefield;

	//private HBox cardsInHandPane;
	//private Pane cardsInHandPane;

	private String styleFilePath;

	//private Network network;
	//private NetworkThread networkThread;
	private KeyHandleThread keyHandleThread;

	private KeyEventHandler keyEventHandler;

	private ArrayList<KeyCode> pressedKeys;

	private Stage primaryStage;
	private Scene gameScene;

	private BorderPane rootGamePane;

	//private double defaultSceneWidth;
	private double defaultSceneHeight;

	private Scale scale;

	//private double scaleFactorX;
	private double scaleFactorY;

	public GameLogic() {

		// Initiates the eventHandlers
		keyEventHandler = new KeyEventHandler();
		pressedKeys = new ArrayList<KeyCode>();

		CardPlayHandler cardPlayHandler = new CardPlayHandler();

		ownBattlefield   = new Battlefield("cardList", cardPlayHandler);
		otherBattlefield = new Battlefield("cardlist", cardPlayHandler);
		otherBattlefield.setRotate(180d);

		// contain the ownBattlefield & and otherBattlefield
		GridPane battlefieldContainer = new GridPane();
		battlefieldContainer.add(otherBattlefield, 0, 0);
		battlefieldContainer.add(ownBattlefield,   0, 1);


		// The pane everything ingame should be placed in
		// Change this for "out of game" menus & simmilar
		rootGamePane = new BorderPane();
		rootGamePane.setCenter(battlefieldContainer);
		rootGamePane.setBottom(ownBattlefield.getPlayer());
		
		// Scene
		// Only one at a time, can change
		gameScene = new Scene(rootGamePane);
		gameScene.setOnKeyPressed(keyEventHandler);
		gameScene.setOnKeyReleased(keyEventHandler);

		// ALL THE GRAPHICS OBJECTS SHOULD NOW BE INITIATED
		// Loads the stylesheet, in a not to pretty way
		File styleFile = new File("stylesheets/stylesheet.css");
		styleFilePath = "file:///" + styleFile.getAbsolutePath().replace("\\", "/");
		gameScene.getStylesheets().add(styleFilePath);



		//Give a proper card focus
		try {
			ownBattlefield.getCards().getCard(0).giveThisFocus();
		} catch (CardNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// Set scale, for windown resize
		scale = new Scale();
		scale.setPivotX(0);
		scale.setPivotY(0);
		rootGamePane.getTransforms().add(scale);

		//new Thread(new CardCheckThread()).start();
		try {
			keyHandleThread = new KeyHandleThread();
			new Thread(keyHandleThread).start();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		//networkThread = new NetworkThread();
		new Thread(new NetworkThread()).start();
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

		primaryStage.setTitle("cardboardGathering");

		WindowSizeListener windowSizeListener = new WindowSizeListener();

		primaryStage.widthProperty().addListener(windowSizeListener);
		primaryStage.heightProperty().addListener(windowSizeListener);


		// Sets all the cards to be painted
		for( Card ownTemp : ownBattlefield.getCards() ) {
			ownBattlefield.getChildren().add(ownTemp);
		}
		for( Card otherTemp : otherBattlefield.getCards() ) {
			otherBattlefield.getChildren().add(otherTemp);
		}
		for( Card handTemp : ownBattlefield.getPlayer().getHandCards() ) {
			ownBattlefield.getPlayer().getChildren().add(handTemp);
			handTemp.setTranslateX(150 * (1 + ownBattlefield.getPlayer().getHandCards().indexOf(handTemp)));
			handTemp.setTranslateY(20);
		}
		//Platform.runLater(new CardCheckThread());

		primaryStage.setScene(gameScene);
		primaryStage.show();

		//defaultSceneWidth = gameScene.getWidth();
		defaultSceneHeight = gameScene.getHeight();


		//System.out.println(gameScene.getWidth() + primaryStage.getWidth());
	}

	private class CardPlayHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				Card tempCard = ownBattlefield.getPlayer().getHandCards().getCard( (Card)(event.getSource()) );
				if( tempCard.getCurrentLocation() == Card.HAND ) {
					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						System.out.println("card should now graphicly move");
						ownBattlefield.getPlayer().getChildren().remove(tempCard);
						ownBattlefield.getChildren().add(tempCard);
						ownBattlefield.getPlayer().playCard(tempCard);
					}
				}
			} catch (CardNotFoundException e) {
				//TODO this will fail when using a card on the battlefield.
				//Be aware that it's the reason for not doing anything here
				//e.printStackTrace();
			}
		}
	}

	private class WindowSizeListener implements ChangeListener<Number> {
		final Timer timer = new Timer();
		TimerTask task = null;
		final long delayTime = 200;

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
			if( task != null ) {
				task.cancel();
			}
			task = new TimerTask() {

				@Override
				public void run() {
					//scaleFactorX = (gameScene.getWidth() / defaultSceneWidth);
					scaleFactorY = (gameScene.getHeight() / defaultSceneHeight);


					System.out.println( "sfY GL: " + scaleFactorY);

					scale.setX(scaleFactorY);
					scale.setY(scaleFactorY);

					// I know, X ≠ Y 
					ownBattlefield.getPlayer().updateScaleFactorX(scaleFactorY);
					ownBattlefield.getPlayer().updateScaleFactorY(scaleFactorY);
					
				}
			};
			timer.schedule(task, delayTime);
		}
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

		int moveSpeed = 20;

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

					tempCard = Card.getCurrentCard();

					TranslateTransition tt;
					if( pressedKeys.contains(KeyCode.DOWN) ||
					    pressedKeys.contains(KeyCode.J) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );

						if( tempCard.getTranslateY() + moveSpeed > ownBattlefield.getHeight() - tempCard.getHeight() ) {
							tt.setByY( ownBattlefield.getHeight() - tempCard.getHeight() - tempCard.getTranslateY() );
						} else {
							tt.setByY( moveSpeed );
						}
						tt.play();
					}
					if( pressedKeys.contains(KeyCode.UP) ||
					    pressedKeys.contains(KeyCode.K) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );

						if( tempCard.getTranslateY() - moveSpeed < 0 ) {
							tt.setByY( -1*tempCard.getTranslateY() );
						} else {
							tt.setByY( -1*moveSpeed );
						}
						tt.play();
					}
					if( pressedKeys.contains(KeyCode.RIGHT) ||
					    pressedKeys.contains(KeyCode.L) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );

						if( tempCard.getTranslateX() + moveSpeed > ownBattlefield.getWidth() - tempCard.getWidth() ) {
							tt.setByX( ownBattlefield.getWidth() - tempCard.getWidth() - tempCard.getTranslateX() );
						} else {
							tt.setByX( moveSpeed );
						}
						tt.play();
					}
					if( pressedKeys.contains(KeyCode.LEFT) ||
					    pressedKeys.contains(KeyCode.H) ) {
						tt = new TranslateTransition( Duration.millis(50), tempCard );

						if( tempCard.getTranslateX() - moveSpeed < 0 ) {
							tt.setByX( -1*tempCard.getTranslateX() );
						} else {
							tt.setByX( -1*moveSpeed );
						}
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
						if( !pressedKeys.contains(KeyCode.SHIFT) ) {
							int newCardIndex = ownBattlefield.getCards().indexOf(Card.getCurrentCard()) + 1;
							if( newCardIndex >= ownBattlefield.getCards().size() ) {
								newCardIndex = 0;
							}
							ownBattlefield.getCards().get(newCardIndex).giveThisFocus();
						} else {
							int newCardIndex = ownBattlefield.getCards().indexOf(Card.getCurrentCard()) - 1;
							if( newCardIndex < 0 ) {
								newCardIndex = ownBattlefield.getCards().size() - 1;
							}
							ownBattlefield.getCards().get(newCardIndex).giveThisFocus();
						}
					}

					try {
						this.wait(50);
					} catch (InterruptedException e) {
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
