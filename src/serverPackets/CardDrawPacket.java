package serverPackets;

public class CardDrawPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private long id;
	
	public CardDrawPacket( long cardId ) {
		this.id = cardId;
		this.dataType = DataTypes.CARDDRAW;
		this.data = this;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
}
