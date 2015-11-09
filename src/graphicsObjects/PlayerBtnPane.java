package graphicsObjects;

import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

/**
 * The pane for the buttons in the players inventory
 * Should be used for convinence features for the physical player
 * @see graphicsObjects.PlayerResetBoardBtn
 * @see graphicsObjects.PlayerShuffleHandBtn
 * @see graphicsObjects.PlayerResetRotationBtn
 */
public class PlayerBtnPane extends VBox{
	public Button shuffleBtn;
	public Button resetBoardBtn;
	public Button resetRotationBtn;

	public PlayerBtnPane(int width, int height, EventHandler<ActionEvent> handler) {
		this.setPrefSize(width, height);
		this.setFillWidth(true);
		this.setSpacing(10);
		this.getStyleClass().add("btn-pane");

		this.setAlignment(Pos.CENTER);

		shuffleBtn = new PlayerShuffleHandBtn(handler);
		this.getChildren().add(shuffleBtn);

		resetBoardBtn = new PlayerResetBoardBtn(handler);
		this.getChildren().add(resetBoardBtn);

		resetRotationBtn = new PlayerResetRotationBtn(handler);
		this.getChildren().add(resetRotationBtn);

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

	/**
	 * @return the resetRotationBtn
	 */
	public Button getResetRotationBtn() {
		return resetRotationBtn;
	}
}
