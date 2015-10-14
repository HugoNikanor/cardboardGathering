package gamePieces;

import javafx.scene.layout.Pane;

import exceptions.CardNotFoundException;

import graphicsObjects.DeckPane;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class Battlefield extends Pane {

	private Player player;
	private CardCollection cards;

	private DeckPane deckPane;

	public static final double WIDTH = 1600;
	public static final double HEIGHT = 395;
  
	
	public Battlefield(String cardList, EventHandler<MouseEvent> cardPlayHandler) {
		//System.out.println("Debug: start of Battlefield");
		player = new Player(cardList, cardPlayHandler);
		cards = player.getBattlefieldCards();

		this.getStyleClass().add("battlefield");
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setMinHeight(HEIGHT);
		this.setMaxHeight(HEIGHT);
		this.setPrefSize(this.getWidth(), this.getHeight());
		this.setMinSize(this.getWidth(), this.getHeight());

		String deckString = Integer.toString(getPlayer().getDeckCards().size());
		deckPane = new DeckPane(deckString, 105, 150, this.getWidth(), this.getHeight());
		deckPane.setOnMouseClicked(new MouseEventHandler());
		this.getChildren().add(deckPane);

		//System.out.println("Debug: end of Battlefield");
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			try {
				getPlayer().drawCard();
				deckPane.updateText(Integer.toString(getPlayer().getDeckCards().size()));
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
