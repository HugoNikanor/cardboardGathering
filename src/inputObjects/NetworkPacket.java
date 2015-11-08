package inputObjects;

import java.io.Serializable;

/**
 * A class made for containing data to be sent over the network
 * TODO this shoule maybe not be able to be used by itself...
 */
public class NetworkPacket implements Serializable {
	private static final long serialVersionUID = 1L;

	public static enum DataTypes {
		INFO,
		CARDPLACE,
		CARDPLAYED,
		CARDDRAW
	}

	protected DataTypes dataType;
	protected Object data;
	
	protected NetworkPacket() {}
	public NetworkPacket( DataTypes dataType, Object data ) {
		this.dataType = dataType;
		this.data = data;
	}

	public DataTypes getDataType() {
		return dataType;
	}

	public Object getData() {
		return data;
	}
}
