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

		// This make it work, but it aint pretty
		this.setPrefWidth( 500 );
		this.setPrefHeight( 250 );

		this.getStyleClass().add( "message-area-scroll-pane" );

		ChatStream.getGraphic().visibleProperty().addListener( (ov, oldV, newV) -> {
			setVisible( newV );
		});

		this.setMaxWidth( Double.MAX_VALUE );
		this.setMaxHeight( 500 );

	}
}
