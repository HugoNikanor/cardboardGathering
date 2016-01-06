package chat;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	public ChatContainer( Connection connection ) {
		chatBox = new TextBox( e -> {
			msgArea.printMessage( chatBox.getTextAndClear(), MessageInfo.PLAYER );
		} );

		msgArea = new MessageArea( connection );

		toggleDisplayBtn = new Button();
		toggleDisplayBtn.setPrefWidth( 30 );
		toggleDisplayBtn.setOnAction( e -> {
			toggleDisplay();
		} );

		upperArea = new HBox();
		upperArea.setAlignment( Pos.CENTER_RIGHT );
		upperArea.setPickOnBounds( false );

		lowerArea = new HBox();
		lowerArea.setPickOnBounds( false );

		lowerArea.getChildren().addAll( chatBox, toggleDisplayBtn );
		upperArea.getChildren().add( msgArea );
		this.getChildren().addAll( upperArea, lowerArea );

		this.setTranslateX( 100 );
		this.setTranslateY( 10 );

		this.setPickOnBounds( false );
		this.getStyleClass().add( "chat-container" );

		msgArea.visibleProperty().addListener( new ChangeListener<Boolean>() {
			@Override
			public void changed( ObservableValue<? extends Boolean> observableValue,
			                     Boolean oldValue,
			                     Boolean newValue ) {
				setDisplayBtnStr( newValue );
				upperArea.setMouseTransparent( !newValue );
			}
		});

		msgArea.setVisible( false );
	}

	public void sendMessage( String message, MessageInfo type ) {
		msgArea.printMessage( message, type );
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
