package network;

import exceptions.CardNotFoundException;

import gamePieces.Battlefield;

import inputObjects.*;

/**
 * Takes care of the objects recieved by network.Connection
 */
public class InputObjectHandler {
	
	private Battlefield battlefield;

	public InputObjectHandler( Battlefield battlefield ) {
		this.battlefield = battlefield;
	}

	public void handleObject( NetworkPacket data ) {
		switch( data.getDataType() ) {
			case INFO:
				System.out.println( "Object is INFO" );

				break;
			case CARDMOVE:
				System.out.println( "Object is CARDMOVE" );
				CardMoveObject cmo = (CardMoveObject) data.getData();
				try {
					battlefield.getCards().getCard( cmo.getId() ).smoothMove( cmo.getChangeX(), cmo.getChangeY() );
				} catch( CardNotFoundException e ) {
					System.out.println( "no card with id: " + cmo.getId() );
				}

				break;
			case CARDPLACE:
				System.out.println( "Object is CARDPLACE" );
				CardPlaceObject cpo = (CardPlaceObject) data.getData();
				try {
					battlefield.getCards().getCard( cpo.getId() ).smoothPlace( cpo.getPosX(), cpo.getPosY() );
				} catch( CardNotFoundException e ) {
					System.out.println( "no card with id: " + cpo.getId() );
				}


				break;
			case CARDLIST:
				System.out.println( "Object is CARDLIST" );

				break;
			default:
				System.err.println( "Somethin is wrong with the data:" );
				System.err.println( data.toString() );
				break;
		}
	}
}
