package graphicsObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class PlayerResetBoardBtn extends Button {
	public PlayerResetBoardBtn(EventHandler<ActionEvent> listener) {
		super("Reset Board");
		this.setOnAction(listener);

		this.getStyleClass().add("reset-board-btn");

		this.setMaxWidth(Double.MAX_VALUE);
	}
}
