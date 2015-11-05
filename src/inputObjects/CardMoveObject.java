package inputObjects;

import network.NetworkPacket;

public class CardMoveObject extends NetworkPacket {
	private static final long serialVersionUID = -7680270814684327453L;
	private long id;
	private double changeX;
	private double changeY;

	public CardMoveObject( long cardId, double changeX, double changeY ) {
		this.id = cardId;
		this.changeX = changeX;
		this.changeY = changeY;
		
		this.dataType = DataTypes.CARDMOVE;
		this.data = this;
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the changeX
	 */
	public double getChangeX() {
		return changeX;
	}

	/**
	 * @return the changeY
	 */
	public double getChangeY() {
		return changeY;
	}
}
