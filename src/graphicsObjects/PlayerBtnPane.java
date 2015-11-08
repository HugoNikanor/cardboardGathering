package graphicsObjects;

import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class PlayerBtnPane extends VBox{
	public Button shuffleBtn;
	public Button resetBoardBtn;

	public PlayerBtnPane(int width, int height, EventHandler<ActionEvent> handler) {
		this.setPrefSize(width, height);
		this.setFillWidth(true);
		this.setSpacing(10);
		this.getStyleClass().add("btn-pane");

		this.setAlignment(Pos.CENTER);

		shuffleBtn = new ShuffleBtn(handler);
		this.getChildren().add(shuffleBtn);

		resetBoardBtn = new ResetBoardBtn(handler);
		this.getChildren().add(resetBoardBtn);

	}

	/**
	 * @return the shuffleBtn
	 */
	public Button getShuffleBtn() {
		return shuffleBtn;
	}

	/**
	 * @return the resetBoardBtn
	 */
	public Button getResetBoardBtn() {
		return resetBoardBtn;
	}
}
