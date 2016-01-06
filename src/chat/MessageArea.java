package chat;

//import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextFlow;

import network.Connection;

public class MessageArea extends ScrollPane {
	//private TextFlow textArea;
	//private ArrayList<String> textLog;

	public MessageArea( Connection connection ) {
		//textLog = new ArrayList<String>();
		//textArea = new TextFlow();
		//textArea.setPrefWidth( 200 );
		//textArea.setPrefHeight( 200 );

		this.setFitToWidth( true );
		//this.setContent( textArea );
		this.setContent( ChatStream.getGraphic() );

		//textArea.setMouseTransparent( true );
		//textArea.setPickOnBounds( false );
		//textArea.getChildren().add( new Text( "Welcome\n" ) );

		this.setPickOnBounds( false );

		this.setMinWidth( 150 );
		this.setMinHeight( 150 );

		this.getStyleClass().add( "message-area-scroll-pane" );
	}
}
