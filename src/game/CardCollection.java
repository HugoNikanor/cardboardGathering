package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import database.DatabaseInterface;

public class CardCollection {
	private ArrayList<Card> cards;

	/*
	 * Create a new card collection without any card in it.
	 * Used for everything but the deck
	 */
	public CardCollection() {
		cards = new ArrayList<Card>();

	}

	/*
	 * cardList should be a string pointing to the name of a preprepared text 
	 * file containing a list of all cards desired. This shold be sent to the 
	 * card fectcher, which gets them from the database
	 */
	public CardCollection(String cardList) {
		cards = new ArrayList<Card>(Arrays.asList(this.fetchCards(cardList)));

		//Should this be called upon constuction? TODO
		this.shuffleCards();

	}


	private Card[] fetchCards(String cardList) {
		return new DatabaseInterface(cardList).getCards();

	}

	public void shuffleCards() {
		Random rand = new Random();
		Card tempCard;
		int cardIndex;
		for(int i = 0; i < cards.size(); i++) {
			cardIndex = rand.nextInt(cards.size() - 1);
			tempCard = cards.get(cardIndex);
			cards.add(cardIndex, cards.get(i));
			cards.add(i, tempCard);
		}
	}

	public Card getNextCard() {
		return cards.get(cards.size());
	}

	public Card getCard(int cardId) {
		return cards.get(cardId);

	}
	public Card getCard(Card card) {
		return cards.get(cards.indexOf(card));

	}


}
