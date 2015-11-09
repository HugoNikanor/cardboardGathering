package graphicsObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class PlayerResetRotationBtn extends Button {
	public PlayerResetRotationBtn( EventHandler<ActionEvent> listener ) {
		super("Reset Rotation");
		this.setOnAction(listener);

		this.getStyleClass().add("reset-rotation-btn");

		this.setMaxWidth(Double.MAX_VALUE);
	}
}
