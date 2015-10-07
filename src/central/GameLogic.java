package central;

import java.io.File;
import java.net.URL;

import gamePieces.Battlefield;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GameLogic extends Application {

	private Battlefield ownBattlefield;
	// This is the battlefield aquired over the network
	private Battlefield otherBattlefield;

	//private Network network;
	private NetworkThread networkThread;

	private ActionEventHandler actionEventHandler;
	private KeyEventHandler keyEventHandler;

	private Stage primaryStage;

	private Button ownBtn;
	private Button otherBtn;

	public GameLogic() {

		//System.out.println("Debug: start of GameLogic");

		ownBattlefield   = new Battlefield("cardList");
		otherBattlefield = new Battlefield("cardlist");

		ownBattlefield.setId("test-id");
		otherBattlefield.getStyleClass().add("test-class");

		networkThread = new NetworkThread();
		new Thread(networkThread).start();

		actionEventHandler = new ActionEventHandler();
		keyEventHandler    = new KeyEventHandler();
		
		//System.out.println("Debug: end of GameLogic");

	}

	@Override
	public void start(Stage inStage) throws Exception {
		/**
		 * Makes the Stage avalible to the whole class,
		 * Used to be able to close the window via an event.
		 */
		primaryStage = inStage;

		// =================================== //
		// Pane Declarations                   //
		// =================================== //

		GridPane battlefieldContainer = new GridPane();
		GridPane cardsOnHandPane = new GridPane();

		cardsOnHandPane.setPrefSize(400, 100);

		BorderPane rootGamePane = new BorderPane();

		rootGamePane.setCenter(battlefieldContainer);
		rootGamePane.setBottom(cardsOnHandPane);

		//ownBattlefield.setBorder(

		otherBattlefield.setRotate(180d);

		//battlefieldContainer.getChildren().add(ownBattlefield);
		battlefieldContainer.add(otherBattlefield, 0, 0);
		battlefieldContainer.add(ownBattlefield,   0, 1);


		// btn
		ownBtn = new Button();
		ownBtn.setText("'Own Battlefield'");
		ownBtn.setOnAction(actionEventHandler);

		otherBtn = new Button();
		otherBtn.setText("'Other Battlefield'");
		otherBtn.setOnAction(actionEventHandler);

		ownBattlefield.getChildren().add(ownBtn);
		otherBattlefield.getChildren().add(otherBtn);

		// Scene
		// Only one at a time, can change
		Scene gameScene = new Scene(rootGamePane);
		gameScene.setOnKeyPressed(keyEventHandler);


		// Loads the stylesheet, in a not to pretty way
		File f = new File("stylesheet.css");
		gameScene.getStylesheets().clear();
		gameScene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

		// Stage
		// This never changes
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(gameScene);
		primaryStage.show();

		System.out.println("launched();");

	}

	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if(event.getSource() == ownBtn) {
				System.out.println("Hello World");
			}
			if(event.getSource() == otherBtn) {
				System.out.println("Goodbye!");
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
