package central;

import java.io.File;

import gamePieces.Battlefield;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GameLogic extends Application {

	private Battlefield ownBattlefield;
	// This is the battlefield aquired over the network
	private Battlefield otherBattlefield;

	private HBox cardsInHandPane;

	private String styleFilePath;

	//private Network network;
	private NetworkThread networkThread;

	private ActionEventHandler actionEventHandler;
	private KeyEventHandler keyEventHandler;

	private Stage primaryStage;

	public GameLogic() {
		// Loads the stylesheet, in a not to pretty way
		File styleFile = new File("stylesheets/stylesheet.css");
		styleFilePath = "file:///" + styleFile.getAbsolutePath().replace("\\", "/");

		// Initiates the eventHandlers
		keyEventHandler    = new KeyEventHandler();
		actionEventHandler = new ActionEventHandler();

		ownBattlefield   = new Battlefield("cardList", actionEventHandler);
		otherBattlefield = new Battlefield("cardlist", actionEventHandler);
		otherBattlefield.setRotate(180d);

		// Pane for the cards in your hand
		// TODO This should possibly get its own class 
		cardsInHandPane = new HBox();
		cardsInHandPane.setPrefSize(400, 100);
		cardsInHandPane.getStyleClass().add("cards-in-hand-pane");

		// TODO testline
		ownBattlefield.setId("test-id");

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

		// Applies the stylesheet
		gameScene.getStylesheets().add(styleFilePath);

		// Stage
		// This never changes
		primaryStage.setTitle("cardboardGathering");
		primaryStage.setScene(gameScene);
		primaryStage.show();
	}

	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if(event.getSource() == ownBattlefield.getTestBtn()) {
				System.out.println("ownBattlefield");
			}
			if(event.getSource() == otherBattlefield.getTestBtn()) {
				System.out.println("otherBattlefield");
			}
		}
	}

	public class KeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			// C-m to close the window
			if(event.isControlDown() &&
			   event.getCode() == KeyCode.M) {
				primaryStage.close();
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
