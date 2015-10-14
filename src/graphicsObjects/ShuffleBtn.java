package graphicsObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ShuffleBtn extends Button {
	public ShuffleBtn(EventHandler<ActionEvent> listener) {
		super("Shuffle Hand");
		this.setOnAction(listener);

		this.getStyleClass().add("shuffle-btn");

		this.setMaxWidth(Double.MAX_VALUE);
	}
}
