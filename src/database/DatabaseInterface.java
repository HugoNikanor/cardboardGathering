package database;

import game.Card;

public class DatabaseInterface {
	
	public DatabaseInterface(String cardList) {

	}

	public Card[] getCards() {
		Card[] cards = {new Card(), new Card()};

		return cards;
	}

}
