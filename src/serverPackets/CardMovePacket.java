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
	 *
	 * @param cardId the id of the card to move
	 * @param posX where the card has moved to horizontally
	 * @param posY where the card has moved to vertically
	 * @param rotate what angle the card has rotated to
	 */
	public CardMovePacket( long cardId, double posX, double posY, double rotate ) {
		this.id = cardId;
		this.posX = posX;
		this.posY = posY;
		this.rotate = rotate;
		
		this.dataType = DataTypes.CARDMOVE;
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
