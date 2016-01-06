package chat;

import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import network.Connection;

import serverPackets.ChatMessagePacket;

public class ChatStream {
	private static volatile TextFlow instance;

	private ChatStream() { }
	
	private static TextFlow getInstance( TextFlow linstance ) {
		if( linstance == null ) {
			synchronized (ChatStream.class) {
                if (linstance == null) {
                    linstance = new TextFlow();
                }
            }
		}
		return linstance;
	}

	public static TextFlow getGraphic() {
		instance = getInstance( instance );
		return instance;
	}

	public static void print( String message, MessageInfo type, Connection connection ) {
		instance = getInstance( instance );

		boolean shouldSend = false;
		String finalMessage = "";

		if( message.equals("") ) {
			return;
		}

		Text text = new Text();

		switch( type ) {
			case PLAYER:
				finalMessage = finalMessage.concat( "<Player> " );
				text.getStyleClass().add( "chat-player" );
				shouldSend = true;
				break;
			case OPONENT:
				finalMessage = finalMessage.concat( "<Oponent> " );
				text.getStyleClass().add( "chat-oponent" );
				break;
			case SYSTEM:
				finalMessage = finalMessage.concat( "@" );
				text.getStyleClass().add( "chat-system" );
				break;
			case ERROR:
				finalMessage = finalMessage.concat( "!" );
				text.getStyleClass().add( "chat-error" );
				text.setUnderline( true );
				break;
			case OTHER:
				text.getStyleClass().add( "chat-other" );
				break;
		}
		finalMessage = finalMessage
			.concat( message )
			.concat( "\n" );

		text.setText( finalMessage );
		//textLog.add( finalMessage );

		if( shouldSend ) {
			try {
				connection.sendPacket( new ChatMessagePacket( message, MessageInfo.OPONENT ) );
			} catch( NullPointerException e ) {
				ChatStream.print( "Connection missing", MessageInfo.ERROR, null );
			}
		}

		Platform.runLater( new Thread(() -> {
			instance.getChildren().add( text );
			instance.setVisible( false );
			instance.setVisible( true );
		}) );

	}
}
