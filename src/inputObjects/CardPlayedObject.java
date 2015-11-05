package inputObjects;

public class CardPlayedObject extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private long id;
	private double posX;
	private double posY;

	public CardPlayedObject( long cardId, double posX, double posY ) {
		this.id = cardId;
		this.posX = posX;
		this.posY = posY;

		this.dataType = DataTypes.CARDPLAYED;
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
