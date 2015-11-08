package inputObjects;

public class CardFocusObject extends NetworkPacket {
	private static final long serialVersionUID = 1L;
	private long id;

	public CardFocusObject( long id ) {
		this.id = id;

		this.dataType = DataTypes.CARDFOCUS;
		this.data = this;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

}