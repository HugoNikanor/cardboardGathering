package serverPackets;

public class CardFromDatabasePacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private String cardName;

	public CardFromDatabasePacket( String cardName ) {
		this.cardName = cardName;
		this.dataType = DataTypes.CARDFROMDATABASE;
	}

	/**
	 * @return the cardName
	 */
	public String getCardName() {
		return cardName;
	}
}
