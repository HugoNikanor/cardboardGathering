package gamePieces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import chat.ChatStream;
import chat.MessageInfo;
import database.CardChooser;
import database.JSONCardReader;

import exceptions.CardNotFoundException;

import graphicsObjects.CardSelectionPane;
import graphicsObjects.CardStackPane;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.transform.Rotate;

/**
 * A collection of cards
 * Can hold about any number of cards
 * extends ArrayList
 */
public class CardCollection implements Iterable<Card> {
	//private static final long serialVersionUID = 1L;

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
	private Button shuffleBtn;
	private CardStackPane cardStack;

	private Pane graphicPane;

	private ObjectProperty<Card> observableCard;
	private IntegerProperty observableCount;

	private ArrayList<Card> cards;



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
		cards = new ArrayList<Card>();
		observableCard = new SimpleObjectProperty<Card>();
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
		cards = new ArrayList<Card>();

		try {
			CardChooser cardChooser = new CardChooser( cardList );

			// Get all cards from the jCardChooser
			while( cardChooser.hasNext() ) {
				this.add( jCardReader.get( cardChooser.next(), counter.getCounterAndIncrament() ) );
			}
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

		//TODO should this be done here?
		//this.shuffleCards();

		observableCard = new SimpleObjectProperty<Card>();
	}

	public Pane getGraphics( boolean isLocal ) {
		if( graphicPane == null ) {
			graphicPane = new Pane();
			cardStack = new CardStackPane( collection, Card.WIDTH, Card.HEIGHT );
			if( isLocal ) {
				HBox buttonCont = new HBox();
				buttonCont.getTransforms().add( new Rotate(90, 0, 0, 0, Rotate.Z_AXIS) );
				buttonCont.setTranslateX( Card.WIDTH + 40 );
				buttonCont.setMinHeight( 30 );
				buttonCont.setMaxHeight( 30 );
				buttonCont.setMinWidth( Card.HEIGHT );
				buttonCont.setPrefHeight( 40 );
				buttonCont.setPrefWidth( 100 );


				getFromDeckBtn = new Button();
				getFromDeckBtn.setText( "Get Card" );
				getFromDeckBtn.setOnAction( new BtnHandler() );
				getFromDeckBtn.setMaxSize( Double.MAX_VALUE, Double.MAX_VALUE );

				shuffleBtn = new Button( "Shuffle" );
				shuffleBtn.setOnAction( e -> {
					ChatStream.print( "Shuffling cards", MessageInfo.SYSTEM, null );
					shuffleCards();
				} );
				shuffleBtn.setMaxSize( Double.MAX_VALUE, Double.MAX_VALUE );

				HBox.setHgrow( getFromDeckBtn, Priority.ALWAYS );
				HBox.setHgrow( shuffleBtn, Priority.ALWAYS );

				cardStack.setOnMouseClicked( new DeckAreaHandler() );

				buttonCont.getChildren().addAll( getFromDeckBtn, shuffleBtn );
				graphicPane.getChildren().add( buttonCont );
			} else {
				graphicPane.setRotate( 180d );
			}
			graphicPane.getChildren().add( cardStack );

			observableCount = new SimpleIntegerProperty();
			observableCount.set( this.size() );
			observableCount.addListener( (ov, oldVal, newVal) -> {
				System.out.println( "something changed" ); 
				cardStack.setText( newVal.toString() );
			} );

			updateGraphicText();
		}
		return graphicPane;
	}

	private class BtnHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle( ActionEvent e ) {
			new Thread(() -> {
				try {
					// The card should be removed from the collection when it's taken from here
					observableCard.set( CardSelectionPane.getCard( 
								CardCollection.this, (Pane)graphicPane.getParent() ));
				} catch( CardNotFoundException ex ) {
					ex.printStackTrace();
				}
			}).start();
		}
	}

	private class DeckAreaHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle( MouseEvent e ) {
			try {
				// The card should be removed from the collection when it's taken from here
				observableCard.set( getNextCard() );
			} catch( CardNotFoundException ex ) {
				ex.printStackTrace();
			}
		}
	}
	
	private void updateGraphicText() {
		if( cardStack != null ) {
			cardStack.setText( Integer.toString(cards.size()) );
		}
	}

	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	public int size() {
		return cards.size();
	}
	private Card get( int index ) {
		return cards.get( index );
	}
	private void set( int index, Card card ) {
		cards.set( index, card );
	}
	private Card remove( int index ) {
		Card removedCard = cards.remove( index );
		updateGraphicText();
		return removedCard;
	}
	private boolean remove( Card card ) {
		boolean success = cards.remove( card );
		updateGraphicText();
		return success;
	}
	public int indexOf( Card card ) {
		return cards.indexOf( card );
	}
	public boolean add( Card card ) {
		boolean success = cards.add( card );
		updateGraphicText();
		return success;
	}
	public void add( int index, Card card ) {
		cards.add( index, card );
		updateGraphicText();
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

	public ObjectProperty<Card> getObservableCardProperty() {
		return observableCard;
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
