package chat;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import network.Connection;

public class ChatContainer extends VBox {

	private MessageArea msgArea;
	private TextBox chatBox;

	private HBox upperArea;
	private HBox lowerArea;

	private Button toggleDisplayBtn;

	/**
	 * The whole chat interface
	 */
	public ChatContainer( Connection connection ) {
		chatBox = new TextBox( e -> {
			ChatStream.print( chatBox.getTextAndClear(), MessageInfo.PLAYER, connection );
		} );

		msgArea = new MessageArea();

		toggleDisplayBtn = new Button();
		toggleDisplayBtn.setMinWidth( 30 );
		toggleDisplayBtn.setOnAction( e -> {
			toggleDisplay();
		} );

		upperArea = new HBox();
		upperArea.setAlignment( Pos.CENTER_RIGHT );
		upperArea.setPickOnBounds( false );

		lowerArea = new HBox();
		lowerArea.setPickOnBounds( false );
		lowerArea.setMaxWidth( Double.MAX_VALUE );

		lowerArea.getChildren().addAll( chatBox, toggleDisplayBtn );
		upperArea.getChildren().addAll( msgArea );
		this.getChildren().addAll( upperArea, lowerArea );

		this.setPickOnBounds( false );
		this.getStyleClass().add( "chat-container" );

		msgArea.visibleProperty().addListener( (ov, oldValue, newValue) -> {
			setDisplayBtnStr( newValue );
			upperArea.setMouseTransparent( !newValue );
		} );

		msgArea.setVisible( false );

		this.setPrefWidth( 500 );
		this.setFillWidth( true );
	}

	private void setDisplayBtnStr( boolean isOut ) {
		if( isOut ) toggleDisplayBtn.setText( "-" );
		else        toggleDisplayBtn.setText( "+" );
	}

	private void toggleDisplay() {
		Platform.runLater( new Thread(() -> {
			msgArea.setVisible( !msgArea.isVisible() );
		}) );
	}
}
