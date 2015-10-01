package gamePieces;

public class Battlefield {
	private Player player;
	private CardCollection cards;


	public Battlefield(String cardList) {
		player = new Player(cardList);
		cards = player.getBattlefieldCards();
	}

}
