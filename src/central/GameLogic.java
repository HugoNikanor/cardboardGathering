package central;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameLogic extends Application {

	//private Battlefield ownBattlefield;
	//private Battlefield otherBattlefield;

	//private Network network;
	private NetworkThread networkThread;


	private ActionEventHandler actionEventHandler;
	private KeyEventHandler keyEventHandler;

	private Stage primaryStage;

	public GameLogic() {

		//System.out.println("Debug: start of GameLogic");

		//ownBattlefield = new Battlefield("cardList");

		// These lines are debug
		/*
		try {
			System.out.println(ownBattlefield.getCards().getCard(0).toString());
			System.out.println(ownBattlefield.getCards().getCard(1).toString());
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
		*/

		networkThread = new NetworkThread();
		new Thread(networkThread).start();

		actionEventHandler = new ActionEventHandler();
		keyEventHandler    = new KeyEventHandler();
		
		//System.out.println("Debug: end of GameLogic");

	}

	@Override
	public void start(Stage inStage) throws Exception {
		primaryStage = inStage;

		// btn
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(actionEventHandler);

		// Stack pane
		Pane root = new Pane();
		root.getChildren().add(btn);

		// Scene
		// Only one at a time, can change
		Scene scene = new Scene(root, 300, 250);
		scene.setOnKeyPressed(keyEventHandler);

		// Stage
		// This never changes
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("Hello World");
		}
	}
	public class KeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			// C-m to close the window
			if(event.getCode() == KeyCode.M &&
			   event.isControlDown()) {
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
