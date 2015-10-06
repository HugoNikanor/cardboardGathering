package gamePieces;

import javafx.scene.layout.Pane;

public class Battlefield extends Pane {
	private Player player;
	private CardCollection cards;

	public Battlefield(String cardList) {
		//System.out.println("Debug: start of Battlefield");
		player = new Player(cardList);
		cards = player.getBattlefieldCards();
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

}
