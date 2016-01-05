package network;

import java.util.ArrayList;

import database.JSONCardReader;

import exceptions.BadDataException;
import exceptions.CardNotFoundException;

import gamePieces.Battlefield;
import gamePieces.Card;

import javafx.application.Platform;

import serverPackets.CardBetweenCollectionsPacket;
import serverPackets.CardFocusPacket;
import serverPackets.CardFromDatabasePacket;
import serverPackets.CardListPacket;
import serverPackets.CardMovePacket;
import serverPackets.HealthSetPacket;
import serverPackets.NetworkPacket;
import serverPackets.PoisonSetPacket;
import serverPackets.CardCreatedPacket;

/**
 * Class which takes the info recieved over Connection and tells
 * the GameLogic, and all classes under it, what to do.
 */
public class InputObjectHandler {
	
	/**
	 * The packets recieved, allows for packages to be handeled 
	 * after one another
	 */
	private ArrayList<NetworkPacket> pendingPackets;

	private Battlefield battlefield;
	private JSONCardReader jCardReader;
	
	/**
	 * @param jCardReader where the cards should be read from
	 */
	public InputObjectHandler( JSONCardReader jCardReader ) { 
		System.out.println( "InputObjectHandler created" );
		this.jCardReader = jCardReader;

		pendingPackets = new ArrayList<NetworkPacket>();
		new Thread(new inputThread()).start();
	}

	/**
	 * Use this method to use the object, <br>
	 * Takes care of the data and updates battlefiled with the 
	 * new data.
	 * @param data the data to be sent
	 */
	public void handleObject( NetworkPacket data ) {
		pendingPackets.add( data );
	}

	private void cardMove( CardMovePacket obj ) {
		try {
			Card temp = battlefield.getCards().getCard( obj.getId() );
			Platform.runLater( new Runnable() {
				@Override
				public void run() {
					temp.smoothPlace( obj.getPosX(), obj.getPosY(), Connection.UPDATE_TIME );
					temp.smoothSetRotate( obj.getRotate(), Connection.UPDATE_TIME );
					//temp.giveFocus();
				}
			});
		} catch( CardNotFoundException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Called from cardCollectionChange
	 */
	private void playCard( CardBetweenCollectionsPacket obj ) {
		try {
			System.out.println( "obj.getId(): " + obj.getId() );
			Card tempCard = battlefield.getPlayer().getHandCards().getCard( obj.getId() );

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					battlefield.getPlayer().playCard( tempCard, battlefield );
				}
			});
		} catch( CardNotFoundException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Called from cardCollectionChange
	 */
	private void drawCard( CardBetweenCollectionsPacket obj ) {
		try {
			battlefield.getPlayer().drawCard( obj.getId() );
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Move a card between collections <br>
	 * Note that all moves are currently not supperted,
	 * but more options can easily be added in the source.
	 * @throws BadDataException if the data is bad or tries to make an illegal move
	 */
	private void cardCollectionChange( CardBetweenCollectionsPacket obj ) {
		switch( obj.getOldCollection() ) {
		case DECK:
			switch( obj.getNewCollection() ) {
			case HAND:
				this.drawCard( obj );
				break;
			default:
				throw new BadDataException( obj.toString() );
			}
			break;
		case HAND:
			switch( obj.getNewCollection() ) {
			case BATTLEFIELD:
				this.playCard( obj );
				break;
			default:
				throw new BadDataException( obj.toString() );
			}
			break;
		case BATTLEFIELD:
			switch( obj.getNewCollection() ) {
			case DECK:
				battlefield.getPlayer().cardToDeck( obj.getId() );
				break;
			case GRAVEYARD:
				battlefield.getPlayer().cardToGrave( obj.getId() );
				break;
			default:
				throw new BadDataException( obj.toString() );
			}
			break;
		case GRAVEYARD:
			switch( obj.getNewCollection() ) {
			case HAND:
				break;
			default:
				throw new BadDataException( obj.toString() );
			}
			break;
		default:
			throw new BadDataException( obj.toString() );
		}
	}
	private void focusCard( CardFocusPacket obj ) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					battlefield.getCards().getCard( obj.getId() ).giveFocus();
				} catch( CardNotFoundException e ){
					e.printStackTrace();
				}
			}
		});
	}
	private void setHealth( HealthSetPacket obj ) {
		battlefield.getPlayer().setHealth( obj.getHealth() );
	}
	private void setPoison( PoisonSetPacket obj ) {
		battlefield.getPlayer().setPoison( obj.getPoison() );
	}
	private void setCardList( CardListPacket obj ) {
		battlefield = new Battlefield( jCardReader, obj.getCardList() );
	}
	private void createCard( CardCreatedPacket obj ) {
		battlefield.getPlayer().createCard( new Card(
			obj.getCardName(),
			obj.getType(),
			obj.getType(),
			obj.getAbility(),
			obj.getFlavour(),
			obj.getPower(),
			obj.getToughness(),
			obj.getLoyalty(),
			obj.getManaCostBlack(),
			obj.getManaCostBlue(),
			obj.getManaCostGreen(),
			obj.getManaCostRed(),
			obj.getManaCostWhite(),
			obj.getManaCostBlank())
		);
	}
	private void createCardFromDatabase( CardFromDatabasePacket obj ) {
		battlefield.getPlayer().createCardFromDatabase( obj.getCardName() );
	}

	/**
	 * @return the battlefield, used for creating it in GameLogic
	 * @throws NullPointerException if battlefield is not yet readable
	 * @see battlefield
	 */
	public Battlefield getBattlefield() throws NullPointerException {
		try {
			return battlefield;
		} catch( NullPointerException e ) {
			throw new NullPointerException("battlefield not initiated yet");
		}
	}

	private class inputThread implements Runnable {
		@Override
		public void run() {
		synchronized( this ) {
		while( true ) {
			while( pendingPackets.size() > 0 ) {
				switch( pendingPackets.get(0).getDataType() ) {
				case INFO:
					break;
				case CARDMOVE:
					cardMove( (CardMovePacket) pendingPackets.get(0) );
					break;
				case CARDCOLLECTIONCHANGE:
					cardCollectionChange( (CardBetweenCollectionsPacket) pendingPackets.get(0) );
					break;
				case CARDFOCUS:
					focusCard( (CardFocusPacket) pendingPackets.get(0) );
					break;
				case HEALTHSET:
					setHealth( (HealthSetPacket) pendingPackets.get(0) );
					break;
				case POISONSET:
					setPoison( (PoisonSetPacket) pendingPackets.get(0) );
					break;
				case CARDLIST:
					setCardList( (CardListPacket) pendingPackets.get(0) );
					break;
				case CARDCREATE:
					createCard( (CardCreatedPacket) pendingPackets.get(0) );
					break;
				case CARDFROMDATABASE:
					createCardFromDatabase( (CardFromDatabasePacket) pendingPackets.get(0) );
					break;
				default:
					throw new BadDataException( pendingPackets.get(0).toString() );
				}
				pendingPackets.remove(0);
			}
			try {
				this.wait(Connection.UPDATE_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		}
		}
	}

}
