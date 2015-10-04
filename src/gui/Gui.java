package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This class will probably use JavaFX
 */
public class Gui extends Application {

	private static EventHandler<ActionEvent> actionEventHandler;
	private static EventHandler<KeyEvent>    keyEventHandler;

	private static Stage primaryStage;
	
	//private static GameLogic.ActionEventHandler actionEventHandler;
	//private static GameLogic.KeyEventHandler keyEventHandler;

	public static void addActionEventHandler(EventHandler<ActionEvent> actionEventHandler) {
		Gui.actionEventHandler = actionEventHandler;
	}
	public static void addKeyEventHandler(EventHandler<KeyEvent> keyEventHandler) {
		Gui.keyEventHandler = keyEventHandler;
	}

	@Override
	public void start(Stage inStage) throws Exception {
		primaryStage = inStage;

		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(actionEventHandler);

		StackPane root = new StackPane();
		root.getChildren().add(btn);

		Scene scene = new Scene(root, 300, 250);
		scene.setOnKeyPressed(keyEventHandler);

		primaryStage.setTitle("Hello World");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public static void close() {
		primaryStage.close();
	}
}
