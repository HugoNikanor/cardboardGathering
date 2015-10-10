package gamePieces;

import java.util.Random;

import exceptions.CardNotFoundException;

import javafx.scene.layout.Pane;

public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	public Battlefield(String cardList) {
		//System.out.println("Debug: start of Battlefield");
		player = new Player(cardList);
		cards = player.getBattlefieldCards();

		this.getStyleClass().add("battlefield");
		this.setPrefSize(400, 200);

		try {
			this.getChildren().add(getCards().getCard(0));
			getCards().getCard(0).setTranslateX(new Random().nextInt(331));
			getCards().getCard(0).setTranslateY(new Random().nextInt(101));

			this.getChildren().add(getCards().getCard(1));
			getCards().getCard(1).setTranslateX(new Random().nextInt(331));
			getCards().getCard(1).setTranslateY(new Random().nextInt(101));
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

		//System.out.println("Debug: end of Battlefield");
	}

	public Player getPlayer() {
		return player;
	}

	public CardCollection getCards() {
		return cards;
	}

}
