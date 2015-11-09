package graphicsObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class PlayerShuffleHandBtn extends Button {
	public PlayerShuffleHandBtn(EventHandler<ActionEvent> listener) {
		super("Shuffle Hand");
		this.setOnAction(listener);

		this.getStyleClass().add("shuffle-hand-btn");

		this.setMaxWidth(Double.MAX_VALUE);
	}
}
