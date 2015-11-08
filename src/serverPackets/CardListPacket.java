package serverPackets;

public class CardListPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private String[] cardList;

	public CardListPacket( String[] cardList ) {
		this.cardList = cardList;

		this.dataType = DataTypes.CARDLIST;
		this.data = this;
	}

	/**
	 * @return the cardList
	 */
	public String[] getCardList() {
		return cardList;
	}
}
