package chat;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import network.Connection;

import serverPackets.ChatMessagePacket;

public class MessageArea extends ScrollPane {
	private TextFlow textArea;
	private ArrayList<String> textLog;

	private Connection connection;

	public MessageArea( Connection connection ) {
		this.connection = connection;

		textLog = new ArrayList<String>();
		textArea = new TextFlow();
		textArea.setPrefWidth( 200 );
		textArea.setPrefHeight( 200 );

		this.setFitToWidth( true );
		this.setContent( textArea );

		textArea.setMouseTransparent( true );
		textArea.setPickOnBounds( false );
		textArea.getChildren().add( new Text( "Welcome\n" ) );

		this.setPickOnBounds( false );

		this.setMinWidth( 150 );
		this.setMinHeight( 150 );

		this.getStyleClass().add( "message-area-scroll-pane" );
	}

	public void printMessage( String message, MessageInfo sender ) {
		String finalMessage = "";

		if( message.equals("") ) {
			return;
		}

		Text text = new Text();

		switch( sender ) {
			case PLAYER:
				finalMessage = finalMessage.concat( "<Player> " );
				text.getStyleClass().add( "chat-player" );
				break;
			case OPONENT:
				finalMessage = finalMessage.concat( "<Oponent> " );
				text.getStyleClass().add( "chat-oponent" );
				break;
			case SYSTEM:
				text.getStyleClass().add( "chat-system" );
				break;
			case ERROR:
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
		textLog.add( finalMessage );

		connection.sendPacket( new ChatMessagePacket( finalMessage, MessageInfo.OPONENT ) );
		textArea.getChildren().add( text );

		this.setVisible( true );
	}

	/**
	 * TODO write this method
	 */
	public void saveLogToFile( String filepath ) {
	}



}
