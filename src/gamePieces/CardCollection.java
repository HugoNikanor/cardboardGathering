package gamePieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import database.DatabaseInterface;

import exceptions.CardNotFoundException;

public class CardCollection extends ArrayList<Card> {
	private static final long serialVersionUID = 1L;

	public CardCollection() { }

	/*
	 * cardList should be a string pointing to the name of a preprepared text 
	 * file containing a list of all cards desired. This shold be sent to the 
	 * card fectcher, which gets them from the database
	 */
	public CardCollection(String cardList) {
		//System.out.println("Debug: start of cardCollection");
		this.addAll(Arrays.asList(this.fetchCards(cardList)));

		this.shuffleCards();

		//System.out.println("Debug: end of cardCollection");
	}


	private Card[] fetchCards(String cardList) {
		return new DatabaseInterface(cardList).getCards();

	}

	public void shuffleCards() {
		Random rand = new Random();
		Card tempCard;
		int cardIndex;
		int cardCount = this.size();
		for(int i = 0; i < cardCount; i++) {
			cardIndex = rand.nextInt(this.size());
			tempCard = this.get(cardIndex);
			this.set(cardIndex, this.get(i));
			this.set(i, tempCard);
		}
	}

	/**
	 * the 'Take' methods removes the card from the collection,
	 * the 'Get' are comparable to ArrayList.get, except with a different error
	 */
	public Card takeNextCard() throws CardNotFoundException {
		if(this.size() == 0) {
			throw new CardNotFoundException("No cards in collection");
		}
		Card returnCard = this.get(this.size() - 1);
		this.remove(this.size() - 1);
		return returnCard;
	}

	public Card takeCard(int cardIndex) throws CardNotFoundException {
		Card returnCard;
		try {
			returnCard = this.get(cardIndex);
		} catch(IndexOutOfBoundsException e) {
			throw new CardNotFoundException("Index out of bounds");
		}
		this.remove(cardIndex);
		return returnCard;
	}

	public Card takeCard(Card card) throws CardNotFoundException {
		int indexOfCard = this.indexOf(card);
		// ArrayList.indexOf returns '-1' if there is no such element
		if( indexOfCard == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = this.get(indexOfCard);
		this.remove(this.indexOf(card));
		return returnCard;
	}

	public Card getNextCard() throws CardNotFoundException {
		if(this.size() == 0) {
			throw new CardNotFoundException("No cards in collection");
		}
		Card returnCard = this.get(this.size() - 1);
		return returnCard;
	}

	public Card getCard(int indexOfCard) throws CardNotFoundException {
		if( indexOfCard < 0 || indexOfCard >= this.size() ) {
			throw new CardNotFoundException("Index out of bounds");
		}
		Card returnCard = this.get(indexOfCard);
		return returnCard;
	}

	public Card getCard(Card card) throws CardNotFoundException {
		int indexOfCard = this.indexOf(card);
		// ArrayList.indexOf returns '-1' if there is no such element
		if( indexOfCard == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = this.get(indexOfCard);
		return returnCard;
	}
}
