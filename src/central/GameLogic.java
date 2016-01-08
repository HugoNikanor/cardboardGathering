package central;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameLogic extends Application {

	private int port;
	private String ipAddr;
	private Path deckFilepath;
	private boolean showTitle;

	private Settings settings;

	public GameLogic() {
		settings = new Settings( "settings/settings.properties" );

		// WARNING, this can maybe fail
		ipAddr = settings.getProperty("defIpAddress", "127.0.0.1" );
		port = Integer.parseInt(settings.getProperty( "defPort", "23732" ));

		//connection = new Connection( inputObjectHandler, ipAddr, port );

		deckFilepath = Paths.get(
		               settings.getProperty( "defCardDir", "decks/" ) +
		               settings.getProperty( "defCardList", "defCardList" ));

		String showTitleStr = settings.getProperty( "showTitle", "true" );
		if( showTitleStr.equalsIgnoreCase( "true" ) )
			showTitle = true;
		else
			showTitle = false;

   	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("cardboardGathering");
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent event ) {
				Platform.exit();
				System.exit(0);
			}
		});

		if( showTitle ) {
			System.out.println( "showTitle is true" );
			new TitleScene( primaryStage, settings );
		} else {
			System.out.println( "showTitle is false" );
			new GameScene( primaryStage, ipAddr, port, deckFilepath );
		}


		// TODO maybe bind exit fullsceen keys properly
		primaryStage.setFullScreenExitHint("There is no escape! except for escape and F11...");
	}




	/**
	 * Keeps a list of the pressed keys up to date
	 * @see pressedKeys
	 * @see KeyHandleThread
	 */
	/*
	public class KeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			if( event.getEventType() == KeyEvent.KEY_PRESSED) {
				if( !(pressedKeys.contains(event.getCode())) ) {
					pressedKeys.add(event.getCode());
				}
			}
			if( event.getEventType() == KeyEvent.KEY_RELEASED) {
				pressedKeys.remove(event.getCode());
			}
		}
	}
	*/

	/**
	 * Takes the pressed keys and do the actions linked to them
	 * @see pressedKeys
	 * @see KeyEventHandler
	 */
	/*
	private class KeyHandleThread implements Runnable {
		boolean spacePressedBefore;
		Card tempCard;

		double moveSpeed = 30;

		@Override
		public void run() {
			synchronized( this ) {
				System.out.println("key thread started");
				while( true ) {
					// <C-c> to close the window
					if(pressedKeys.contains(KeyCode.CONTROL) &&
					   pressedKeys.contains(KeyCode.C) ) {
						Platform.exit();
						System.exit(0);
					}

					tempCard = Card.getCurrentCard();

					/**
					 * Use the arrow keys or 'hjkl' to move the card,
					 * TODO This maybe has the risk of crashing the program
					 * /
					if( pressedKeys.contains(KeyCode.DOWN) ||
						pressedKeys.contains(KeyCode.J) ) {

						tempCard.smoothMove( 0, moveSpeed, 50 );
					}
					if( pressedKeys.contains(KeyCode.UP) ||
						pressedKeys.contains(KeyCode.K) ) {

						tempCard.smoothMove( 0, -moveSpeed, 50 );
					}
					if( pressedKeys.contains(KeyCode.RIGHT) ||
						pressedKeys.contains(KeyCode.L) ) {

						tempCard.smoothMove( moveSpeed, 0, 50 );
					}
					if( pressedKeys.contains(KeyCode.LEFT) ||
						pressedKeys.contains(KeyCode.H) ) {

						tempCard.smoothMove( -moveSpeed, 0, 50 );
					}

					// Rotate the card when space is released
					if( !pressedKeys.contains(KeyCode.SPACE) &&
						spacePressedBefore ) {
						System.out.println("space is down");
							if( tempCard.getRotate() == 0 ) {
								tempCard.smoothSetRotate( 90d, 500 );
							} else {
								tempCard.smoothSetRotate( 0d, 500 );
							}
					}
					spacePressedBefore = pressedKeys.contains(KeyCode.SPACE);

					/*
					 * Cycles thrugh the cards on the battlefield when <tab> or
					 * <S-tab> is pressed,
					 * TODO This currently doesn't play nicely with the buttons
					 * focus, the cards on the battlefield should be changed to
					 * use proper focus instead of my own version.
					 * Basicly, it crashes.
					 * / 
					if( pressedKeys.contains(KeyCode.TAB) ) {
						if( !pressedKeys.contains(KeyCode.SHIFT) ) {
							int newCardIndex = ownBattlefield.getCards().indexOf(Card.getCurrentCard()) + 1;
							if( newCardIndex >= ownBattlefield.getCards().size() ) {
								newCardIndex = 0;
							}
							ownBattlefield.getCards().get(newCardIndex).giveFocus();
						} else {
							int newCardIndex = ownBattlefield.getCards().indexOf(Card.getCurrentCard()) - 1;
							if( newCardIndex < 0 ) {
								newCardIndex = ownBattlefield.getCards().size() - 1;
							}
							ownBattlefield.getCards().get(newCardIndex).giveFocus();
						}
					}

					// TODO make this less agressive
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
	}*/
}
