package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This class will probably use JavaFX
 */
public class Gui extends Application {

	private EventHandler<ActionEvent> eventHandler;

	public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
		this.eventHandler = eventHandler;
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(eventHandler);

		System.out.println(btn.toString());

		StackPane root = new StackPane();
		root.getChildren().add(btn);

		Scene scene = new Scene(root, 300, 250);

		primaryStage.setTitle("Hello World");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
