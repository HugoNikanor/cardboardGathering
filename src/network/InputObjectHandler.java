package network;

import java.util.ArrayList;

import database.JSONCardReader;
import exceptions.CardNotFoundException;
import gamePieces.Battlefield;
import gamePieces.Card;

import javafx.application.Platform;

import serverPackets.CardDrawPacket;
import serverPackets.CardFocusPacket;
import serverPackets.CardListPacket;
import serverPackets.CardMovePacket;
import serverPackets.CardPlayedPacket;
import serverPackets.HealthSetPacket;
import serverPackets.NetworkPacket;
import serverPackets.PoisonSetPacket;

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
	private void playCard( CardPlayedPacket obj ) {
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
	private void drawCard( CardDrawPacket obj ) {
		try {
			battlefield.getPlayer().drawCard( obj.getId() );
		} catch (CardNotFoundException e) {
			e.printStackTrace();
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
					//System.out.println( "INFO" + System.currentTimeMillis() );
					break;
				case CARDMOVE:
					//System.out.println( "CARDMOVE" + System.currentTimeMillis());
					cardMove( (CardMovePacket) pendingPackets.get(0).getData() );
					break;
				case CARDPLAYED:
					//System.out.println( "CARDPLAYED" + System.currentTimeMillis());
					playCard( (CardPlayedPacket) pendingPackets.get(0).getData() );
					break;
				case CARDDRAW:
					//System.out.println( "CARDDRAW" + System.currentTimeMillis() );
					drawCard( (CardDrawPacket) pendingPackets.get(0).getData() );
					break;
				case CARDFOCUS:
					//System.out.println( "CARDFOCUS" + System.currentTimeMillis() );
					focusCard( (CardFocusPacket) pendingPackets.get(0).getData() );
					break;
				case HEALTHSET:
					//System.out.println( "HEALTHSET" + System.currentTimeMillis() );
					setHealth( (HealthSetPacket) pendingPackets.get(0).getData() );
					break;
				case POISONSET:
					//System.out.println( "POISONSET" + System.currentTimeMillis() );
					setPoison( (PoisonSetPacket) pendingPackets.get(0).getData() );
					break;
				case CARDLIST:
					//System.out.println( "CARDLIST" + System.currentTimeMillis() );
					setCardList( (CardListPacket) pendingPackets.get(0).getData() );
					break;
				default:
					System.err.println( "Somethin is wrong with the data:" );
					System.err.println( pendingPackets.get(0).toString() );
					break;
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
