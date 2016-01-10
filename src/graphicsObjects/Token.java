package graphicsObjects;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Token extends StackPane {
	private Circle circle;
	private Text numberText;

	private IntegerProperty numberProperty;

	public Token( Paint color ) {
		circle = new Circle( 10, color );

		numberProperty = new SimpleIntegerProperty();

		numberProperty.set( 0 );
		this.setVisible( false );
		numberText = new Text( Integer.toString(numberProperty.get()) );

		this.getChildren().addAll( circle, numberText );

		numberProperty.addListener( (ov, oVal, nVal) -> {
			numberText.setText( Integer.toString(numberProperty.get()) );
			if( numberProperty.get() <= 0 ) {
				this.setVisible( false );
			}
			if( numberProperty.get() > 0 ) {
				this.setVisible( true );
			}
			System.out.println( numberProperty.get() );
		});

		this.setMouseTransparent( true );
	}

	public void incrament() {
		numberProperty.set( numberProperty.get() + 1 );
	}

	public void decrament() {
		numberProperty.set( numberProperty.get() - 1 );
		if( numberProperty.get() < 0 )
			numberProperty.set( 0 );
	}

	public void setNumber( int number ) {
		numberProperty.set( number );
	}

	/**
	 * @return the numberProperty
	 */
	public IntegerProperty getNumberProperty() {
		return numberProperty;
	}

}
