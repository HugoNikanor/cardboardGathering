package gamePieces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import database.DatabaseInterface;

import exceptions.CardNotFoundException;

public class CardCollection extends ArrayList<Card> {
	private static final long serialVersionUID = 1L;

	/*
	 * Create a new card collection without any card in it.
	 * Used for everything but the deck
	 */
	public CardCollection() {
	}

	/*
	 * cardList should be a string pointing to the name of a preprepared text 
	 * file containing a list of all cards desired. This shold be sent to the 
	 * card fectcher, which gets them from the database
	 */
	public CardCollection(String cardList) {
		//System.out.println("Debug: start of cardCollection");
		this.addAll(Arrays.asList(this.fetchCards(cardList)));

		//Should this be called upon constuction? TODO
		//this.shuffleCards();
		//System.out.println("Debug: end of cardCollection");
	}


	private Card[] fetchCards(String cardList) {
		return new DatabaseInterface(cardList).getCards();

	}

	public void shuffleCards() {
		try {
			Random rand = new Random();
			Card tempCard;
			int cardIndex;
			for(int i = 0; i < this.size(); i++) {
				cardIndex = rand.nextInt(this.size() - 1);
				tempCard = this.get(cardIndex);
				this.add(cardIndex, this.get(i));
				this.add(i, tempCard);
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * These following methods fetches the card you want from the list
	 * and then delets it from the list, 
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

	/**
	 * Look at card from collection without removing it.
	 */
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

}
