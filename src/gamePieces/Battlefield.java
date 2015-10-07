package gamePieces;

import java.util.Random;

import exceptions.CardNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	// this is only here for testing purposes
	private Button testBtn;

	public Battlefield(String cardList, EventHandler<ActionEvent> aHandler) {
		//System.out.println("Debug: start of Battlefield");
		player = new Player(cardList);
		cards = player.getBattlefieldCards();

		// btn tests
		testBtn = new Button();
		testBtn.setText(this.toString());
		testBtn.setOnAction(aHandler);
		this.getChildren().add(testBtn);

		this.getStyleClass().add("battlefield");
		this.setPrefSize(400, 200);

		try {
			this.getChildren().add(player.getBattlefieldCards().getCard(0));
			player.getBattlefieldCards().getCard(0).setTablePosX(new Random().nextInt(331));
			player.getBattlefieldCards().getCard(0).setTablePosY(new Random().nextInt(101));
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Debug: end of Battlefield");
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the cards
	 */
	public CardCollection getCards() {
		return cards;
	}

	/**
	 * @return the testBtn
	 */
	public Button getTestBtn() {
		return testBtn;
	}

}
