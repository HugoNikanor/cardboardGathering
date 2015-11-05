package network;

import inputObjects.*;

/**
 * Takes care of the objects recieved by network.Connection
 */
public class InputObjectHandler {
	
	private CardMoveObject  latestCardMoveObj;
	private CardPlaceObject latestCardPlaceObj;
	private CardListObject  latestCardList;

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

}
