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
import javafx.event.ActionEvent;
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

import network.Connection;
import network.InputObjectHandler;

public class GameLogic extends Application {

	// Battlefields
	private Battlefield ownBattlefield;
	// This is the battlefield aquired over the network
	private Battlefield otherBattlefield;

	// Keyboard
	private KeyEventHandler keyEventHandler;
	private ArrayList<KeyCode> pressedKeys;

	// JavaFX Objects
	private Stage primaryStage;
	private Scene gameScene;
	private BorderPane rootGamePane;

	// JavaFX variables
	//private double defaultSceneWidth;
	private double defaultSceneHeight;
	private Scale scale;
	private double scaleFactor;
	private boolean isFullscreen;

	// Network
	private Connection connection;
	private InputObjectHandler inputObjectHandler;

	public GameLogic() {

		// Initiates the eventHandlers
		keyEventHandler = new KeyEventHandler();
		pressedKeys = new ArrayList<KeyCode>();

		CardPlayHandler cardPlayHandler = new CardPlayHandler();

		ownBattlefield = new Battlefield(
				"cardlist1", cardPlayHandler, Battlefield.Populate.LOCAL );
		otherBattlefield = new Battlefield( Battlefield.Populate.NETWORK );
		otherBattlefield.setRotate(180d);

		inputObjectHandler = new InputObjectHandler( otherBattlefield );
		connection = new Connection( inputObjectHandler );


		// Adds the initial cards to the graphical display
		for( Card ownTemp : ownBattlefield.getCards() ) {
			ownBattlefield.getChildren().add(ownTemp);
		}
		for( Card otherTemp : otherBattlefield.getCards() ) {
			otherBattlefield.getChildren().add(otherTemp);
		}

		//Give a proper card focus
		try {
			ownBattlefield.getCards().getCard(0).giveThisFocus();
		} catch (CardNotFoundException e1) {
			System.out.println("Trying to give focus to card on the battlefild, but there are no cards there");
			//e1.printStackTrace();
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
		gameScene = new Scene(rootGamePane);
		gameScene.setOnKeyPressed(keyEventHandler);
		gameScene.setOnKeyReleased(keyEventHandler);


		// ALL THE GRAPHICS OBJECTS SHOULD NOW BE INITIATED
		// Loads the stylesheet, in a not to pretty way
		File styleFile = new File("stylesheets/stylesheet.css");
		String styleFilePath = "file:///" + styleFile.getAbsolutePath().replace("\\", "/");
		gameScene.getStylesheets().add(styleFilePath);

		new Thread(new KeyHandleThread()).start();
	}

	@Override
	public void start(Stage inStage) throws Exception {
		/**
		 * Makes the Stage avalible to the whole class,
		 * Used to be able to close the window via an event.
		 */
		primaryStage = inStage;

		primaryStage.setTitle("cardboardGathering");
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent event ) {
				Platform.exit();
				System.exit(0);
			}
		});

		WindowSizeListener windowSizeListener = new WindowSizeListener();
		primaryStage.widthProperty().addListener(windowSizeListener);
		primaryStage.heightProperty().addListener(windowSizeListener);

		primaryStage.setFullScreenExitHint("There is no escape! except for escape and F11...");
		primaryStage.setScene(gameScene);
		primaryStage.show();

		//defaultSceneWidth = gameScene.getWidth();
		defaultSceneHeight = gameScene.getHeight();
	}

	/**
	 * EventHandler for cards being played,
	 *
	 * This Handler is applied to the cards and is used when they are pressed
	 * and in a players hand.
	 */
	private class CardPlayHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				Card tempCard = ownBattlefield.getPlayer().getHandCards().getCard( (Card)(event.getSource()) );
				if( tempCard.getCurrentLocation() == Card.CardLocation.HAND ) {
					/**
					 * Moves the card upwards from the hand to the battlefield
					 * Then jerk it back down only to have it moved by actually
					 * placing it on the battlefield
					 *
					 * Only locks up its private 'thread'
					 *
					 *  TODO This should be changed so that it is first placed
					 *  on the battlefield, and then glides up from the hand,
					 *  This will allow the other player to see the transition. 
					 */
					if( event.getEventType() == MouseEvent.MOUSE_CLICKED ) {
						tempCard.setCurrentLocation(Card.CardLocation.BATTLEFIELD);

						double finalPosY = 25;
						double moveDistance = ownBattlefield.getHeight() - finalPosY + tempCard.getTranslateY();
						TranslateTransition tt = new TranslateTransition( Duration.millis(500), tempCard );
						tt.setByY( -1*moveDistance );

						ownBattlefield.getPlayer().playCard(tempCard);

						tt.setOnFinished(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								tempCard.setTranslateY( finalPosY );

								ownBattlefield.getPlayer().getChildren().remove(tempCard);
								ownBattlefield.getChildren().add(tempCard);

								tempCard.setTranslateY( finalPosY );
							}
						});

						tt.play();
					}
				}
			} catch (CardNotFoundException e) {
				//TODO this will fail when using a card on the battlefield.
				//Be aware that it's the reason for not doing anything here
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Listens for window resizing,
	 * If a resize is detected, the graphics will scale to match
	 *
	 * TODO Currently there is a problem with the background being slightly
	 * darker when making the window smaller
	 *
	 * TODO The cards on hand have a tendancy to dissapear when making the 
	 * window larger
	 *
	 * TODO The graphics only resize when the window is changed in the 'y'
	 * direction
	 */ 
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
					scaleFactor = (gameScene.getHeight() / defaultSceneHeight);
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
	 * Registers all the keys being pressed
	 */
	public class KeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			if( event.getEventType() == KeyEvent.KEY_PRESSED) {
				if( !(pressedKeys.contains(event.getCode())) ) {
					pressedKeys.add(event.getCode());
					//System.out.println("added: " + event.getCode());
				}
			}
			if( event.getEventType() == KeyEvent.KEY_RELEASED) {
				pressedKeys.remove(event.getCode());
				//System.out.println("removed: " + event.getCode());
			}
		}
	}

	/**
	 * Takes all the keys currently pressed and performs the actions linked 
	 * to them.
	 */
	private class KeyHandleThread implements Runnable {
		boolean spacePressedBefore;
		Card tempCard;

		int moveSpeed = 30;

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

					/**
					 * Use the arrow keys or 'hjkl' to move the card,
					 * TODO This has the risk of crashing the program.
					 */
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

					/**
					 * Cycles thrugh the cards on the battlefield when <tab> or
					 * <S-tab> is pressed,
					 * TODO This currently doesn't play nicely with the buttons
					 * focus, the cards on the battlefield should be changed to
					 * use proper focus instead of my own version.
					 */ 
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

					if( pressedKeys.contains(KeyCode.F11) ) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if( isFullscreen ) {
									isFullscreen = false;
									primaryStage.setFullScreen( isFullscreen );
								} else {
									isFullscreen = true;
									primaryStage.setFullScreen( isFullscreen );
								}
							}
						});
					}

					try {
						// Check for key inputs 20 times a secound
						this.wait(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
