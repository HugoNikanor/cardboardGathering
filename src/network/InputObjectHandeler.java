package network;

/**
 * Takes care of the objects recieved by network.Connection
 */
public class InputObjectHandeler {

	public InputObjectHandeler() {
	}
	public void handleObject( NetworkPacket data ) {
		switch( data.getDataType() ) {
			case INFO:

				break;
			case CARDMOVE:

				break;
			case CARDLIST:

				break;
			default:
				System.err.println( "Somethin is wrong with the data:" );
				System.err.println( data.toString() );
				break;
		}
	}
}
