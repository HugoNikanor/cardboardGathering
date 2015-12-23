package gamePieces;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import database.CardChooser;
import database.JSONCardReader;

import exceptions.CardNotFoundException;

/**
 * A collection of cards
 * Can hold about any number of cards
 * extends ArrayList
 */
public class CardCollection extends ArrayList<Card> {
	private static final long serialVersionUID = 1L;

	/**
	 * The type of collection this is
	 */
	private Collections collection;

	/**
	 * Create an empty collection
	 * @param collection the type of collection this is
	 */
	public CardCollection( Collections collection ) {
		this.collection = collection;
   	}

	/**
	 * Create a collection with cards in it to start
	 * @param collection the type of collection this is
	 * @param jCardReader where the cards should be read from
	 * @param cardIdCounter what id the next created card should have, make sure to incarment every time it is used
	 * @param cardList String[] sent to the cardChooser to allow it to be read as a bad iterrator
	 */
	public CardCollection( Collections collection, JSONCardReader jCardReader, int cardIdCounter, String[] cardList ) {
		this.collection = collection;

		try {
			CardChooser cardChooser = new CardChooser( cardList );

			// Get all cards from the jCardChooser
			while( cardChooser.hasNext() ) {
				this.add( jCardReader.get( cardChooser.next(), cardIdCounter++ ) );
			}
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

		//TODO should this be done here?
		this.shuffleCards();
	}

	/**
	 * Places the cards in the collection in a random order
	 */
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
	 * Take (get and remove) the next card in the collection
	 * @return the next card in the collection
	 * @throws CardNotFoundException if there are no more cards in the collection
	 * @see getNextCard
	 */
	public Card takeNextCard() throws CardNotFoundException {
		if(this.size() == 0) {
			throw new CardNotFoundException("No cards in collection");
		}
		Card returnCard = this.get(this.size() - 1);
		this.remove(this.size() - 1);
		return returnCard;
	}

	/**
	 * Take (get and remove) the card at cardIndex
	 * @return the card it index cardIndex
	 * @param cardIndex position of card to take
	 * @throws CardNotFoundException if there is no card with that index
	 * @see getCard
	 */
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

	/**
	 * Take (get and remove) the chosen card
	 * @return The card given as indata, but it is also removed from the collection
	 * @param card what card to take
	 * @throws CardNotFoundException if the card is not in the collection
	 */
	public Card takeCard(Card card) throws CardNotFoundException {
		int cardIndex = this.indexOf(card);
		// ArrayList.indexOf returns '-1' if there is no such element
		if( cardIndex == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = this.get(cardIndex);
		this.remove(this.indexOf(card));
		return returnCard;
	}

	/**
	 * Take (get and remove) the card with the set cardId
	 * @return the card with the choosen cardId
	 * @param cardId the id of the card, note that the id is gotten by Card.getCardId() and not Card.getId()
	 * @throws CardNotFoundException if there is no card with that id
	 * @see getCard
	 */
	public Card takeCard( long cardId ) throws CardNotFoundException {
		for( Card temp : this ) {
			if( Objects.equals( temp.getCardId(), cardId ) ) {
				this.remove( temp );
				return temp;
			}
		}
		throw new CardNotFoundException("No card with that id " + cardId);
	}

	/**
	 * @return The next card in the collection
	 * @throws CardNotFoundException if there are no more cards in the collection
	 * @see takeNextCard
	 */
	public Card getNextCard() throws CardNotFoundException {
		if(this.size() == 0) {
			throw new CardNotFoundException("No cards in collection");
		}
		Card returnCard = this.get(this.size() - 1);
		return returnCard;
	}

	/**
	 * @return the card it index cardIndex
	 * @param cardIndex position of card to take
	 * @throws CardNotFoundException if there is no card with that index
	 * @see getCard
	 */
	public Card getCard(int cardIndex) throws CardNotFoundException {
		if( cardIndex < 0 || cardIndex >= this.size() ) {
			throw new CardNotFoundException("Index out of bounds");
		}
		Card returnCard = this.get(cardIndex);
		return returnCard;
	}

	/**
	 * Checks if the card is present in the collection
	 * @return the card entered
	 * @param card card to check if it's in the collection
	 * @throws CardNotFoundException if the card is not in the collection
	 * @see takeCard
	 */
	public Card getCard( Card card ) throws CardNotFoundException {
		int cardIndex = this.indexOf(card);
		if( cardIndex == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = this.get(cardIndex);
		return returnCard;
	}

	/**
	 * @return the card with the choosen cardId
	 * @param cardId the id of the card, note that the id is gotten by Card.getCardId() and not Card.getId()
	 * @throws CardNotFoundException if there is no card with that id
	 * @see takeCard
	 */
	public synchronized Card getCard( long cardId ) throws CardNotFoundException {
		for( Card temp : this ) {
			if( Objects.equals( temp.getCardId(), cardId ) ) {
				return temp;
			}
		}
		throw new CardNotFoundException("No card with that id " + cardId);
	}

	/**
	 * @return the collection
	 */
	public Collections getCollection() {
		return collection;
	}

}
