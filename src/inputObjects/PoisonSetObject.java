package inputObjects;

public class PoisonSetObject extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private int poison;

	public PoisonSetObject( int poison ) {
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
