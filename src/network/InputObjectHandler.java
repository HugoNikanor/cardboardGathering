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

	public InputObjectHandler() { }

	public void handleObject( NetworkPacket data ) {
		switch( data.getDataType() ) {
			case INFO:
				System.out.println( "Object is INFO" );

				break;
			case CARDMOVE:
				System.out.println( "Object is CARDMOVE" );
				latestCardMoveObj = (CardMoveObject) data.getData();

				break;
			case CARDPLACE:
				System.out.println( "Object is CARDPLACE" );
				latestCardPlaceObj = (CardPlaceObject) data.getData();

				break;
			case CARDLIST:
				System.out.println( "Object is CARDLIST" );
				latestCardList = (CardListObject) data.getData();

				break;
			case CARDPLAYED:
				System.out.println( "Object is CARDPLAYED" );
				latestCardPlayed = (CardPlayedObject) data.getData();

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

}
