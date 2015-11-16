package serverPackets;

public class HealthSetPacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private int health;

	public HealthSetPacket( int health ) {
		this.health = health;

		this.dataType = DataTypes.HEALTHSET;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

}
