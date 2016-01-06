package graphicsObjects;

import gamePieces.Battlefield;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * The players life counter
 * Holds the health and poison values
 * along with buttons for modifying it
 */
public class LifeCounter extends GridPane {
	
	private Button hpDownBtn;
	private Button hpUpBtn;

	private Button poisonDownBtn;
	private Button poisonUpBtn;
	
	private Button resetBtn;

	private Text lifeLabel;
	private Text poisonLabel;

	private StackPane lifeContainer;
	private StackPane poisonContainer;

	private Text lifeText;
	private Text poisonText;

	/**
	 * @param handler the handler for the buttons in the pane
	 * @see hpDownBtn
	 * @see hpUpBtn
	 * @see poisonDownBtn
	 * @see poisonUpBtn
	 */
	public LifeCounter(EventHandler<ActionEvent> handler, boolean isLocal ) {
		if( isLocal ) {
			hpDownBtn = new Button("-");
			hpDownBtn.setOnAction(handler);
			hpDownBtn.setPrefSize(30, 30);

			this.add(hpDownBtn, 2, 0);

			hpUpBtn = new Button("+");
			hpUpBtn.setOnAction(handler);
			hpUpBtn.setPrefSize(30, 30);
			this.add(hpUpBtn, 3, 0);

			poisonDownBtn = new Button("-");
			poisonDownBtn.setOnAction(handler);
			poisonDownBtn.setMaxSize(30, 30);
			this.add(poisonDownBtn, 2, 1);

			poisonUpBtn = new Button("+");
			poisonUpBtn.setOnAction(handler);
			poisonUpBtn.setPrefSize(30, 30);
			this.add(poisonUpBtn, 3, 1);
			
			resetBtn = new Button("Reset Values");
			resetBtn.setOnAction(handler);
			resetBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			GridPane.setConstraints(resetBtn, 0, 2, 4, 1);
			this.add(resetBtn, 0, 2);
		}


		lifeLabel = new Text("Health: ");
		lifeLabel.setMouseTransparent( true );
		this.add(lifeLabel, 0, 0);

		lifeText = new Text("20");

		lifeContainer = new StackPane();
		lifeContainer.setPrefSize(30, 30);
		lifeContainer.getChildren().add(lifeText);
		lifeContainer.setMouseTransparent( true );
		this.add(lifeContainer, 1, 0);

		poisonLabel = new Text("Poison: ");
		poisonLabel.setMouseTransparent( true );
		this.add(poisonLabel, 0, 1);

		poisonText = new Text("0");

		poisonContainer = new StackPane();
		poisonContainer.setPrefSize(30, 30);
		poisonContainer.getChildren().add(poisonText);
		poisonContainer.setMouseTransparent( true );
		this.add(poisonContainer, 1, 1);

		this.setTranslateX(10);
		// 90 is due to the 3 nodes in the pane all being 30px high
		this.setTranslateY( Battlefield.HEIGHT - 90 - 10 );
		if( !isLocal ) {
			this.setRotate( 180d );
		}

		this.setFocusTraversable( false );
	}

	/**
	 * Sets the displayed life value
	 * @param lifeValue the value to display
	 */
	public void setHealthValue(int lifeValue) {
		this.lifeText.setText(Integer.toString(lifeValue));
	}

	/**
	 * Sets the displayed poison value
	 * @param poisonValue the value to display
	 */
	public void setPoisonValue(int poisonValue) {
		this.poisonText.setText(Integer.toString(poisonValue));
	}

	public Button getHpUpBtn() {
		return hpUpBtn;
	}

	public Button getHpDownBtn() {
		return hpDownBtn;
	}

	public Button getPoisonUpBtn() {
		return poisonUpBtn;
	}

	public Button getPoisonDownBtn() {
		return poisonDownBtn;
	}

	public Button getResetBtn() {
		return resetBtn;
	}
}
