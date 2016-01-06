package serverPackets;

import chat.MessageInfo;

public class ChatMessagePacket extends NetworkPacket {
	private static final long serialVersionUID = 1L;

	private String message;
	private MessageInfo messageInfo;

	public ChatMessagePacket( String message, MessageInfo messageInfo ) {
		this.message = message;
		this.messageInfo = messageInfo;
		this.dataType = NetworkPacket.DataTypes.CHATMESSAGE;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the messageInfo
	 */
	public MessageInfo getMessageInfo() {
		return messageInfo;
	}
}
