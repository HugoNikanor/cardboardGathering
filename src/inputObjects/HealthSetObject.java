package inputObjects;

public class HealthSetObject extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private int health;

	public HealthSetObject( int health ) {
		this.health = health;

		this.dataType = DataTypes.HEALTHSET;
		this.data = this;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

}
