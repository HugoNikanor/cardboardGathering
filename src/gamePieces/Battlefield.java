package gamePieces;

import javafx.scene.layout.Pane;
import exceptions.CardNotFoundException;
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

		Pane deckPane;
		deckPane = new Pane();
		double deckHeight = 150;
		double deckWidth = 105;
		deckPane.setPrefSize(deckWidth, deckHeight);
		deckPane.setTranslateX(10);
		deckPane.setTranslateY(this.getHeight() - deckHeight - 10);
		deckPane.getStyleClass().add("deck");
		deckPane.setOnMouseClicked(new MouseEventHandler());
		this.getChildren().add(deckPane);

		//System.out.println("Debug: end of Battlefield");
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			System.out.println("card drawn");
			try {
				getPlayer().drawCard();
			} catch (CardNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Player getPlayer() {
		return player;
	}

	public CardCollection getCards() {
		return cards;
	}

}
