package serverPackets;

public class PoisonSetPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private int poison;

	public PoisonSetPacket( int poison ) {
		this.poison = poison;

		this.dataType = DataTypes.POISONSET;
		this.data = this;
	}

	/**
	 * @return the poison
	 */
	public int getPoison() {
		return poison;
	}
}
