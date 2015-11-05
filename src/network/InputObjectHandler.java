package network;

import inputObjects.CardListObject;
import inputObjects.CardMoveObject;
import inputObjects.CardPlaceObject;
import inputObjects.CardPlayedObject;
import inputObjects.NetworkPacket;

/**
 * Takes care of the objects recieved by network.Connection
 */
public class InputObjectHandler {
	
	private CardMoveObject   latestCardMoveObj;
	private CardPlaceObject  latestCardPlaceObj;
	private CardListObject   latestCardList;
	private CardPlayedObject latestCardPlayed;

	private boolean newInfo;
	private boolean newCardMove;
	private boolean newCardPlace;
	private boolean newCardList;
	private boolean newCardPlayed;

	public InputObjectHandler() { }

	public void handleObject( NetworkPacket data ) {
		switch( data.getDataType() ) {
			case INFO:
				System.out.println( "Object is INFO" );

				newInfo = true;
				break;
			case CARDMOVE:
				System.out.println( "Object is CARDMOVE" );
				latestCardMoveObj = (CardMoveObject) data.getData();

				newCardMove = true;
				break;
			case CARDPLACE:
				System.out.println( "Object is CARDPLACE" );
				latestCardPlaceObj = (CardPlaceObject) data.getData();

				newCardPlace = true;
				break;
			case CARDLIST:
				System.out.println( "Object is CARDLIST" );
				latestCardList = (CardListObject) data.getData();

				newCardList = true;
				break;
			case CARDPLAYED:
				System.out.println( "Object is CARDPLAYED" );
				latestCardPlayed = (CardPlayedObject) data.getData();

				newCardPlayed = true;
				break;
			default:
				System.err.println( "Somethin is wrong with the data:" );
				System.err.println( data.toString() );
				break;
		}
	}

	/**
	 * @return the latestCardMoveObj
	 */
	public CardMoveObject getLatestCardMoveObj() {
		return latestCardMoveObj;
	}

	/**
	 * @return the latestCardPlaceObj
	 */
	public CardPlaceObject getLatestCardPlaceObj() {
		return latestCardPlaceObj;
	}

	/**
	 * @return the latestCardList
	 */
	public CardListObject getLatestCardList() {
		return latestCardList;
	}

	/**
	 * @return the latestCardPlayed
	 */
	public CardPlayedObject getLatestCardPlayed() {
		return latestCardPlayed;
	}

	/**
	 * @return the newInfo
	 */
	public boolean isNewInfo() {
		return newInfo;
	}

	/**
	 * @param newInfo the newInfo to set
	 */
	public void setNewInfo(boolean newInfo) {
		this.newInfo = newInfo;
	}

	/**
	 * @return the newCardMove
	 */
	public boolean isNewCardMove() {
		return newCardMove;
	}

	/**
	 * @param newCardMove the newCardMove to set
	 */
	public void setNewCardMove(boolean newCardMove) {
		this.newCardMove = newCardMove;
	}

	/**
	 * @return the newCardPlace
	 */
	public boolean isNewCardPlace() {
		return newCardPlace;
	}

	/**
	 * @param newCardPlace the newCardPlace to set
	 */
	public void setNewCardPlace(boolean newCardPlace) {
		this.newCardPlace = newCardPlace;
	}

	/**
	 * @return the newCardList
	 */
	public boolean isNewCardList() {
		return newCardList;
	}

	/**
	 * @param newCardList the newCardList to set
	 */
	public void setNewCardList(boolean newCardList) {
		this.newCardList = newCardList;
	}

	/**
	 * @return the newCardPlayed
	 */
	public boolean isNewCardPlayed() {
		return newCardPlayed;
	}

	/**
	 * @param newCardPlayed the newCardPlayed to set
	 */
	public void setNewCardPlayed(boolean newCardPlayed) {
		this.newCardPlayed = newCardPlayed;
	}

}
