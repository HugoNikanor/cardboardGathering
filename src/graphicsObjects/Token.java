package graphicsObjects;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Token extends StackPane {
	private Circle circle;
	private Text tokenNumberText;

	private int tokenNumber;

	public Token( Paint color ) {
		circle = new Circle( 10, color );

		tokenNumber = 1;
		tokenNumberText = new Text( Integer.toString(tokenNumber) );
		//tokenNumberText.setMouseTransparent( true );

		this.getChildren().addAll( circle, tokenNumberText );

		//this.setPickOnBounds( false );

		this.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY) {
				tokenNumberText.setText( Integer.toString(++tokenNumber) );
			}
			if( e.getButton() == MouseButton.SECONDARY) {
				tokenNumberText.setText( Integer.toString(--tokenNumber) );
			}
			if( tokenNumber < 0 ) {
				((Pane) Token.this.getParent()).getChildren().remove( this );
			}
			e.consume();
		});
	}

}
