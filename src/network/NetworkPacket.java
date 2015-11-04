package network;

import java.io.Serializable;

/**
 * A class made for containing data to be sent over the network
 */
public class NetworkPacket implements Serializable {
	private static final long serialVersionUID = 7831538739600935223L;

	public static enum DataTypes {
		INFO,
		CARDMOVE,
		CARDLIST
	}

	private DataTypes dataType;
	private Object data;
	
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
