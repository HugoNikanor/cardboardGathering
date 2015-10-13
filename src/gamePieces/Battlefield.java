package gamePieces;

import javafx.scene.layout.Pane;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	public static final double WIDTH = 1600;
	public static final double HEIGHT = 395;

	
	public Battlefield(String cardList, EventHandler<MouseEvent> cardPlayHandler) {
		//System.out.println("Debug: start of Battlefield");
		player = new Player(cardList, cardPlayHandler);
		cards = player.getBattlefieldCards();

		this.getStyleClass().add("battlefield");
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setPrefSize(this.getWidth(), this.getHeight());
		this.setMinSize(this.getWidth(), this.getHeight());

		//System.out.println(player.getParent());


		//System.out.println("Debug: end of Battlefield");
	}

	public Player getPlayer() {
		return player;
	}

	public CardCollection getCards() {
		return cards;
	}

}
