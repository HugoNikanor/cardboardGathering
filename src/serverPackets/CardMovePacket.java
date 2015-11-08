package serverPackets;

public class CardMovePacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;
	private long id;
	private double posX;
	private double posY;
	// rotate is in degrees
	private double rotate;

	/**
	 * This is for sending any form of movement the card has done
	 */
	public CardMovePacket( long cardId, double posX, double posY, double rotate ) {
		this.id = cardId;
		this.posX = posX;
		this.posY = posY;
		this.rotate = rotate;
		
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

	/**
	 * @return the rotate
	 */
	public double getRotate() {
		return rotate;
	}
}
