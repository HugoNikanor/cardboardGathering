package gamePieces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import database.CardChooser;
import database.JSONCardReader;

import exceptions.CardNotFoundException;

/**
 * A collection of cards
 * Can hold about any number of cards
 */
public class CardCollection extends ArrayList<Card> {
	private static final long serialVersionUID = 1L;

	/**
	 * Create an empty collection
	 */
	public CardCollection() { }

	/**
	 * Create a collection with cards in it to start
	 * @param jCardReader where the cards should be read from
	 */
	public CardCollection( JSONCardReader jCardReader, int cardIdCounter, String[] cardList ) {
		try {
			CardChooser cardChooser = new CardChooser( cardList );

			while( cardChooser.hasNext() ) {
				System.out.println(this.hashCode() + "cardId = " + cardIdCounter);
				this.add( jCardReader.get( cardChooser.next(), cardIdCounter++ ) );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

		//TODO should this be set here?
		this.shuffleCards();

		//System.out.println("Debug: end of cardCollection");
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

	public Card takeCard( long id ) throws CardNotFoundException {
		for( Card temp : this ) {
			if( Objects.equals( temp.getCardId(), id ) ) {
				this.remove( temp );
				return temp;
			}
		}
		throw new CardNotFoundException("No card with that id " + id);
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

	public Card getCard( Card card ) throws CardNotFoundException {
		int indexOfCard = this.indexOf(card);
		// ArrayList.indexOf returns '-1' if there is no such element
		if( indexOfCard == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = this.get(indexOfCard);
		return returnCard;
	}

	public Card getCard( long id ) throws CardNotFoundException {
		for( Card temp : this ) {
			if( Objects.equals( temp.getCardId(), id ) ) {
				return temp;
			}
		}
		throw new CardNotFoundException("No card with that id " + id);
	}

}
