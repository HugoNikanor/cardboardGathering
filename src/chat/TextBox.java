package chat;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class TextBox extends HBox {

	private TextField textField;
	private Button submitBtn;

	public TextBox( EventHandler<ActionEvent> handler ) {
		textField = new TextField();
		textField.setPromptText( "send message" );
		textField.setOnAction( handler );
		textField.setMaxWidth( Double.MAX_VALUE );
		textField.setPrefColumnCount( Integer.MAX_VALUE );

		submitBtn = new Button( ">" );
		submitBtn.setOnAction( handler );

		this.setFillHeight( true );
		this.getChildren().addAll( textField, submitBtn );

		this.setMaxWidth( Double.MAX_VALUE );
	}

	public String getTextAndClear() {
		String ret = textField.getText();
		textField.clear();
		return ret;
	}
}
