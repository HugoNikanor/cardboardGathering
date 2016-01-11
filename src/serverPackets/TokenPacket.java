package serverPackets;

public class TokenPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	// value between 1 & 6
	private int tokenField;

	private int newValue;

	private long parentId;


	public TokenPacket( int tokenField, int newValue, long parentId ) {
		this.tokenField = tokenField;
		this.newValue = newValue;
		this.parentId = parentId;

		this.dataType = DataTypes.TOKEN;
	}

	/**
	 * @return the tokenField
	 */
	public int getTokenField() {
		return tokenField;
	}

	/**
	 * @return the newValue
	 */
	public int getNewValue() {
		return newValue;
	}

	/**
	 * @return the parentId
	 */
	public long getParentId() {
		return parentId;
	}
}
