package chat;

import javafx.scene.control.ScrollPane;

public class MessageArea extends ScrollPane {

	/**
	 * The log from the chat
	 */
	public MessageArea() {
		this.setFitToWidth( true );
		this.setContent( ChatStream.getGraphic() );

		this.setPickOnBounds( false );

		this.setPrefWidth( 150 );
		this.setPrefHeight( 150 );

		this.getStyleClass().add( "message-area-scroll-pane" );

		ChatStream.getGraphic().visibleProperty().addListener( (ov, oldV, newV) -> {
			setVisible( newV );
		});
	}
}
