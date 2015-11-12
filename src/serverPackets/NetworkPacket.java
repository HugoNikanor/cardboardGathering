package serverPackets;

import java.io.Serializable;

/**
 * The only class that should be sent over the connection
 * for different types of data extend this class 
 */
public class NetworkPacket implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * The "labels" for the different packages
	 */
	public static enum DataTypes {
		INFO,
		CARDMOVE,
		CARDPLAYED,
		CARDDRAW,
		CARDFOCUS,
		HEALTHSET,
		POISONSET,
		CARDLIST
	}

	/**
	 * The "label" for the packages
	 * make sure that this is set for all packages
	 */
	protected DataTypes dataType;

	/**
	 * The actual information sent over the network
	 * extending classes should just put them self here
	 */
	protected NetworkPacket data;

	/**
	 * @return the dataType
	 */
	public DataTypes getDataType() {
		return dataType;
	}

	/**
	 * @return the data
	 */
	public NetworkPacket getData() {
		return data;
	}
}