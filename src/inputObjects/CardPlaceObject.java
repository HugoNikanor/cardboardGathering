package inputObjects;

import network.NetworkPacket;

public class CardPlaceObject extends NetworkPacket {
	private static final long serialVersionUID = -7680270814684327453L;
	private long id;
	private double posX;
	private double posY;

	public CardPlaceObject( long cardId, double posX, double posY ) {
		this.id = cardId;
		this.posX = posX;
		this.posY = posY;
		
		this.dataType = DataTypes.CARDPLACE;
		this.data = this;
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the posX
	 */
	public double getPosX() {
		return posX;
	}

	/**
	 * @return the posY
	 */
	public double getPosY() {
		return posY;
	}

}
