package gamePieces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import database.CardChooser;
import database.JSONCardReader;

import exceptions.CardNotFoundException;

import graphicsObjects.CardStackPane;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

/**
 * A collection of cards
 * Can hold about any number of cards
 * extends ArrayList
 */
public class CardCollection extends Pane implements Iterable<Card> {
	//private static final long serialVersionUID = 1L;
	
	private ArrayList<Card> collectionCards;

	/**
	 * The different types of collections
	 */
	public static enum Collections {
		DECK,
		GRAVEYARD,
		HAND,
		BATTLEFIELD, BATTLEFILED
	}

	private Button getFromDeckBtn;
	private CardStackPane cardStack;
	private boolean graphicsInitiated;

	private Card bufferCard;
	private ObservableValue<Card> observableCard;

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
	public CardCollection( Collections collection, JSONCardReader jCardReader, CardIdCounter counter, String[] cardList ) {
		this.collection = collection;
		collectionCards = new ArrayList<Card>();

		try {
			CardChooser cardChooser = new CardChooser( cardList );

			// Get all cards from the jCardChooser
			while( cardChooser.hasNext() ) {
				collectionCards.add( jCardReader.get( cardChooser.next(), counter.getCounterAndIncrament() ) );
			}
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

		//TODO should this be done here?
		this.shuffleCards();

		graphicsInitiated = false;

		collectionCards.iterator();


	}

	public Pane getGraphics( boolean isLocal ) {
		if( graphicsInitiated ) {
			return this;
		}
		cardStack = new CardStackPane( collection, /*handler,*/ Card.WIDTH, Card.HEIGHT );
		if( isLocal ) {
			GetFromCollectionHandler handler = new GetFromCollectionHandler();

			getFromDeckBtn = new Button();
			getFromDeckBtn.getTransforms().add( new Rotate(90, 0, 0, 0, Rotate.Z_AXIS) );
			getFromDeckBtn.setText( "Get Card" );
			getFromDeckBtn.setTranslateX( Card.WIDTH + 40 );
			getFromDeckBtn.setMinHeight( 30 );
			getFromDeckBtn.setMaxHeight( 30 );
			getFromDeckBtn.setMinWidth( Card.HEIGHT );
			getFromDeckBtn.setPrefHeight( 40 );
			getFromDeckBtn.setPrefWidth( 100 );
			//getFromDeckBtn.addEventHandler( ActionEvent.ACTION, handler );
			getFromDeckBtn.addEventHandler( ActionEvent.ACTION, handler );
			cardStack.addEventHandler( ActionEvent.ACTION, handler );

			this.getChildren().add( getFromDeckBtn );
		} else {
			this.setRotate( 180d );
		}
		this.getChildren().add( cardStack );
		return this;
	}

	@Override
	public Iterator<Card> iterator() {
		return collectionCards.iterator();
	}

	private class GetFromCollectionHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle( ActionEvent e ) {
			if( e.getSource() == getFromDeckBtn ) {
			}
			if( e.getSource() == cardStack ) {
			}
		}
	}

	/**
	 * Places the cards in the collection in a random order
	 */
	public void shuffleCards() {
		Random rand = new Random();
		Card tempCard;
		int cardIndex;
		int cardCount = collectionCards.size();
		for(int i = 0; i < cardCount; i++) {
			cardIndex = rand.nextInt(collectionCards.size());
			tempCard = collectionCards.get(cardIndex);
			collectionCards.set(cardIndex, collectionCards.get(i));
			collectionCards.set(i, tempCard);
		}
	}

	/**
	 * Take (get and remove) the next card in the collection
	 * @return the next card in the collection
	 * @throws CardNotFoundException if there are no more cards in the collection
	 * @see getNextCard
	 */
	public Card takeNextCard() throws CardNotFoundException {
		if(collectionCards.size() == 0) {
			throw new CardNotFoundException("No cards in collection");
		}
		Card returnCard = collectionCards.get(collectionCards.size() - 1);
		collectionCards.remove(collectionCards.size() - 1);
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
			returnCard = collectionCards.get(cardIndex);
		} catch(IndexOutOfBoundsException e) {
			throw new CardNotFoundException("Index out of bounds");
		}
		collectionCards.remove(cardIndex);
		return returnCard;
	}

	/**
	 * Take (get and remove) the chosen card
	 * @return The card given as indata, but it is also removed from the collection
	 * @param card what card to take
	 * @throws CardNotFoundException if the card is not in the collection
	 */
	public Card takeCard(Card card) throws CardNotFoundException {
		int cardIndex = collectionCards.indexOf(card);
		// ArrayList.indexOf returns '-1' if there is no such element
		if( cardIndex == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = collectionCards.get(cardIndex);
		collectionCards.remove(collectionCards.indexOf(card));
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
		for( Card temp : collectionCards ) {
			if( Objects.equals( temp.getCardId(), cardId ) ) {
				collectionCards.remove( temp );
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
		if(collectionCards.size() == 0) {
			throw new CardNotFoundException("No cards in collection");
		}
		Card returnCard = collectionCards.get(collectionCards.size() - 1);
		return returnCard;
	}

	/**
	 * @return the card it index cardIndex
	 * @param cardIndex position of card to take
	 * @throws CardNotFoundException if there is no card with that index
	 * @see getCard
	 */
	public Card getCard(int cardIndex) throws CardNotFoundException {
		if( cardIndex < 0 || cardIndex >= collectionCards.size() ) {
			throw new CardNotFoundException("Index out of bounds");
		}
		Card returnCard = collectionCards.get(cardIndex);
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
		int cardIndex = collectionCards.indexOf(card);
		if( cardIndex == -1 ) {
			throw new CardNotFoundException("No such card in collection");
		}
		Card returnCard = collectionCards.get(cardIndex);
		return returnCard;
	}

	/**
	 * @return the card with the choosen cardId
	 * @param cardId the id of the card, note that the id is gotten by Card.getCardId() and not Card.getId()
	 * @throws CardNotFoundException if there is no card with that id
	 * @see takeCard
	 */
	public synchronized Card getCard( long cardId ) throws CardNotFoundException {
		for( Card temp : collectionCards ) {
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
